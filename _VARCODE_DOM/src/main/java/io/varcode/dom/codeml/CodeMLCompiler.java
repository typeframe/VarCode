package io.varcode.dom.codeml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.varcode.Lang;
import io.varcode.VarException;
import io.varcode.Metadata.SimpleMetadata;
import io.varcode.context.VarContext;
import io.varcode.dom.Dom;
import io.varcode.dom.MarkupException;
import io.varcode.markup.MarkupRepo.MarkupStream;

/**
 * Multi-Language {@code Markup} Compiler for Source Code. 
 * (Reads in Text which could be of ANY possible source language) 
 * <UL>
 *  <LI>java
 *  <LI>c
 *  <LI>c++
 *  <LI>javascript
 *  <LI>...
 * </UL> 
 * interprets EVERYTHING not within a {@code MarkAction} as just "text" rather 
 * than source code:
 * 
 * Reads/Parses text <B>line-by-line</B> and to build up and returns 
 * a {@code Markup} containing {@code MarkAction}s, Text, and Blanks.
 * 
 * Internally acts like a "State Machine" that follows: 
 * 
 * <OL>
 *   <LI>seek next open tag within the text (at {@code tagOpenIndex})
 *   <LI>add all static text between {@code charCursor} and the {@code tagOpenIndex} 
 *   to the state
 *   <LI>seek the matching "close" tag {@code tagCloseIndex} within the 
 *   text after the {@code tagOpenIndex}.
 *   <LI>using the {@code CodeMLParser} parse the Mark text between {@code tagOpenIndex}
 *   and {@code tagCloseIndex} and add the parsed {@code Mark} to the {@code CodeMLParseState} 
 *   (signifying the blank if appropriate)
 *   <LI>set {@code charCursor} to after {@code tagCloseIndex}
 *   <LI>goto step (1) until the end of document is reached  
 * </OL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class CodeMLCompiler
{
	private static final String N = System.lineSeparator();
	
    public static final CodeMLCompiler INSTANCE = 
        new CodeMLCompiler(  );
    
    //private final VarNameAudit nameAudit;
    
    public CodeMLCompiler( )
    { }
    
    /** Base Method for compiling CodeML text source to a {@code Markup} */
    public static Dom fromString( String codeMLText )
    {   
        ByteArrayInputStream bais = 
            new ByteArrayInputStream( 
                codeMLText.getBytes( StandardCharsets.UTF_8 ) ); 
        
        return fromInputStream( bais );
    }
    
    public static Dom fromMarkupStream( MarkupStream codeMLStream )
    {
    	BufferedReader br = 
            new BufferedReader( 
                new InputStreamReader( codeMLStream.getInputStream() ) );
            
        return fromReader( br );
    }
    
    public static Dom fromInputStream( InputStream codeMLInputStream )
    {
        BufferedReader br = 
            new BufferedReader( 
                new InputStreamReader( codeMLInputStream ) );
        
        return fromReader( br );
    }
    
    public static Dom fromReader( BufferedReader codeMLBufferedReader  )
    {
        return INSTANCE.compile( codeMLBufferedReader );
    }
    
    public Dom compile( MarkupStream markupStream )
        throws MarkupException
    {
        if( markupStream == null )
        {
            throw new VarException ( "the MarkupStream  is null " );
        }
        CodeMLParseState parseState = new CodeMLParseState( 
            new VarContext(), 
            new SimpleMetadata() );
        
        parseState.metadata.put( Dom.MARKUP_STREAM, markupStream.describe() );
        parseState.metadata.put( Dom.MARKUP_LANGUAGE, "CodeML" );
        parseState.metadata.put( Dom.MARKUP_ID, markupStream.getMarkupId() );
        parseState.metadata.put( Dom.LANG, Lang.fromCodeId( markupStream.getMarkupId() ) );
        parseState.metadata.put( Dom.DOM_COMPILE_TIMESTAMP, System.currentTimeMillis() );
        
        return compile( 
        	new BufferedReader(
                new InputStreamReader( markupStream.getInputStream() ) ),
            parseState );        
    }
    
	/**  
	 * read/parse the source Markup for {@code MarkAction}s 
	 * and returns the {@code Markup}.
	 * 
	 * @param theReader reader for the text of the {@code Markup} 
	 * NOTE: we use a BufferedReader since it has the readLine() method.
	 * @return {@code Markup} able to be specialized 
	 * @throws MarkupException if the compilation fails
	 */
	public Dom compile( 
	    BufferedReader theReader )
		throws MarkupException
	{
	    CodeMLParseState parseState = new CodeMLParseState( 
	        new VarContext(),
	        new SimpleMetadata() );
	    
	    return compile( theReader, parseState );
	}
	
	
	public Dom compile( 
	    BufferedReader sourceReader, CodeMLParseState parseState )
	{
		try
		{
			String line = "";
			int lineNumber = 1; //initialize the line Number
			
			boolean firstLine = true;
			
			while( ( line = sourceReader.readLine() ) != null ) 
			{
				if( !firstLine )   
				{   //"prepend" the new line before processing the line 
					if( parseState.isMarkOpen() ) 
					{   //previous line ended and a Mark wasn't closed
					    parseState.addToMark( N );
					}
					else
					{   //move static text to next line
						parseState.addText( N ); 
					}
				}
				else 
				{
					firstLine = false;
				}				
				if( parseState.isMarkOpen() ) 
				{   //if previous MARKS aren't closed  
					lineOpenMark( 
				        line,   
						lineNumber, 
						parseState 
					);
				}
				else
				{
					line( 
						line, 
						lineNumber, 
						parseState );						
				}			
				lineNumber++;
			}
			//REACHED THE END OF Markup Source 
			if( parseState.isMarkOpen() )
			{   //UNCLOSED MARK
				throw new MarkupException( 
				    "END OF FILE; missing close Tag \"" + parseState.getCloseTagForOpenMark() 
				  + "\""+ N + " for OPEN Mark : " + N 
				  + parseState.getMarkContents() );
			}
			return parseState.compile( (Lang)parseState.getMetadata().get( "lang" ) );
		}
		catch( IOException ioe )			
		{
			throw new MarkupException( 
			    "Problem reading from Reader ", ioe );
		}
		finally
		{
		    try
            {
                sourceReader.close();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
		}		
	}
	
	/**
	 * Process the next line of text from the code varcode source, 
	 * knowing that a mark (from some previous line) has not been closed.<BR>
	 *  
	 * (a Mark spans multiple lines)
	 * 
	 * @return an Empty StringBuilder if we have closed the marks 
	 *         -or- a StingBuilder containing internal Mark data  from the line 
	 */
	private void lineOpenMark( 
	    String sourceLine, 
		int lineNumber, 
		CodeMLParseState parseState )
	{   //check if the open Mark is closed this line
	    String closeTag = parseState.getCloseTagForOpenMark();
	    
		int indexOfCloseMark = 
	        sourceLine.indexOf( closeTag );
		
		if( indexOfCloseMark >= 0 )
		{
		    parseState.completeMark( 
		        sourceLine.substring( 
		            0, 
		            indexOfCloseMark + closeTag.length() ),			        
		        lineNumber );
		    
			//process what is left of the line (after the close mark)
			line( 
			    sourceLine.substring( indexOfCloseMark + closeTag.length() ), 
				lineNumber, 
				parseState );
			return;
		}
		parseState.addToMark( sourceLine );
	}		
    
	/**
	 * Process the data within the {@code sourceLine} at {@code lineNumber}
	 * (update the {@code builder} with any tags, internal test or document
	 * text  
	 * 
	 * @param sourceLine the source line to parse
	 * @param lineNumber 
	 * @param parseState state of the {@code Markup} being built 
	 */
	private void line( 
	    String sourceLine, 
		int lineNumber, 
		CodeMLParseState parseState )
	{   //this is the CLOSE Mark for EITHER ALONE or REPLACE Marks		    
	    
		String firstOpenTag =
			parseState.getFirstOpenTag( sourceLine );	
		
		if( firstOpenTag != null )
		{   //Opened a mark this line
		    int indexOfOpenMark = sourceLine.indexOf( firstOpenTag );
		    //add everything before the open tag
		    parseState.addText( sourceLine.substring( 0, indexOfOpenMark ) );
		    
		    String matchingCloseTag =
		    	parseState.getCloseTagFor( firstOpenTag );	
		    
		    //find a close tag AFTER the OPEN Tag
		    int closeTagIndex = sourceLine.indexOf( 
		        matchingCloseTag, indexOfOpenMark + firstOpenTag.length() );
		    
		    if( closeTagIndex >= 0 )
		    {   //the Mark is closed this line
		        parseState.completeMark( 
                    sourceLine.substring( 
                        indexOfOpenMark, 
                        closeTagIndex + matchingCloseTag.length() ), 
                    lineNumber );
		        
		        //process the rest of the line (AFTER the CLOSE TAG of MARK)
                line( 
                    sourceLine.substring( 
                        closeTagIndex + matchingCloseTag.length(), 
                        sourceLine.length() ),
                    lineNumber,                 
                    parseState );
                return;
		    }
		    else
		    {   //the mark is not closed this line
		        parseState.startMark( 
		            sourceLine.substring( indexOfOpenMark ), 
		            firstOpenTag );
		        return;
		    }
		}
		else
		{   //NO Tags/ Marks this line (just text)
		    parseState.addText( sourceLine );
		}			
	}		
} //BaseCompiler