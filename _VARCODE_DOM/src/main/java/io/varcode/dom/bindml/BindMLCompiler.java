package io.varcode.dom.bindml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.varcode.Lang;
import io.varcode.Metadata;
import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.dom.Dom;
import io.varcode.dom.MarkupException;
import io.varcode.markup.MarkupRepo.MarkupStream;

/**
 * BindML Markup Compiler 
 * 
 * Reads/Parses text <B>line-by-line</B> and to build up and returns 
 * a {@code Markup} containing {@code Mark}s, Text, and Blanks.
 * 
 * Internally acts like a "State Machine" that follows: 
 * 
 * <OL>
 *   <LI>seek next open tag within the text (at {@code tagOpenIndex})
 *   <LI>add all static text between {@code charCursor} and the {@code tagOpenIndex} 
 *   to the state
 *   <LI>seek the matching "close" tag {@code tagCloseIndex} within the 
 *   text after the {@code tagOpenIndex}.
 *   <LI>using the {@code MarkParser} parse the Mark text between {@code tagOpenIndex}
 *   and {@code tagCloseIndex} and add the parsed {@code Mark} to the {@code CompileState} 
 *   (signifying the blank if appropriate)
 *   <LI>set {@code charCursor} to after {@code tagCloseIndex}
 *   <LI>goto step (1) until the end of document is reached  
 * </OL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class BindMLCompiler
{
	private static final String N = System.lineSeparator();
	
    public static final BindMLCompiler INSTANCE = 
        new BindMLCompiler( );
    
    public BindMLCompiler( )
    { }
    
    /** Base Method for compiling a {@code Dom} from makrup */
    public static Dom fromString( String markup )
    {   
        ByteArrayInputStream bais = 
            new ByteArrayInputStream( 
                markup.getBytes( StandardCharsets.UTF_8 ) ); 
        
        return fromInputStream( bais );
    }
    
    public static Dom fromMarkupStream( MarkupStream ms )
    {
    	BufferedReader br = 
            new BufferedReader( 
                new InputStreamReader( ms.getInputStream() ) );
            
        return fromReader( br );
    }
    public static Dom fromInputStream( InputStream is )
    {
        BufferedReader br = 
            new BufferedReader( 
                new InputStreamReader( is ) );
        
        return fromReader( br );
    }
    
    public static Dom fromReader( 
        BufferedReader br  )
    {
        return INSTANCE.from( br );
    }
    
    private BindMLState initializeParseState( MarkupStream markupStream )
    {
    	 VarContext vc = new VarContext();
         Metadata metadata = vc.getMetadata();
         if( metadata == null )
         {
        	 metadata = new Metadata.SimpleMetadata();
        	 vc.set( VarContext.METADATA_NAME, metadata );
         }
         metadata.put( Dom.MARKUP_STREAM, markupStream.describe() );
         metadata.put( Dom.MARKUP_LANGUAGE, "BindML" );
         metadata.put( Dom.MARKUP_ID, markupStream.getMarkupId() );
         metadata.put( Dom.LANG, Lang.fromCodeId( markupStream.getMarkupId() ) );
         metadata.put( Dom.DOM_COMPILE_TIMESTAMP, System.currentTimeMillis() );
         BindMLState parseState = new BindMLState( vc );
         
         return parseState;
    }
    
    public Dom from( MarkupStream markupStream )
        throws MarkupException
    {
    	if( markupStream == null )
        {
            throw new VarException ( "the MarkupStream  is null " );
        }        
        BindMLState parseState = initializeParseState( markupStream );
        return compile( new BufferedReader(
                new InputStreamReader( markupStream.getInputStream() ) ),
            parseState );        
    }
    
    
    private BindMLState initializeParseState( BufferedReader reader )
    {
    	 VarContext vc = new VarContext();
         Metadata metadata = vc.getMetadata();
         if( metadata == null )
         {
        	 metadata = new Metadata.SimpleMetadata();
        	 vc.set( VarContext.METADATA_NAME, metadata );
         }
         metadata.put( Dom.MARKUP_LANGUAGE, "BindML" );
         metadata.put( Dom.DOM_COMPILE_TIMESTAMP, System.currentTimeMillis() );
         BindMLState parseState = new BindMLState( vc );
         
         return parseState;
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
	public Dom from( BufferedReader theReader )
		throws MarkupException
	{
		return compile( theReader, initializeParseState( theReader ) );
	}
	
	
	public Dom compile( 
	    BufferedReader sourceReader, BindMLState compileState )
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
					if( compileState.isMarkOpen() ) 
					{   //previous line ended and a Mark wasn't closed
					    compileState.addToMark( N );
					}
					else
					{   //move static text to next line
						compileState.addText( N ); 
					}
				}
				else 
				{
					firstLine = false;
				}				
				if( compileState.isMarkOpen() ) 
				{   //if previous MARKS aren't closed  
					lineOpenMark( 
				        line,   
						lineNumber, 
						compileState );
				}
				else
				{
					line( 
						line, 
						lineNumber, 
						compileState );						
				}			
				lineNumber++;
			}
			//REACHED THE END OF VarCode Source 
			if( compileState.isMarkOpen() )
			{   //UNCLOSED MARK
				throw new MarkupException( 
				    "END OF FILE; missing close Tag \"" + compileState.getCloseTagForOpenMark() 
				  + "\""+ N + " for OPEN Mark : " + N 
				  + compileState.getMarkContents() );
			}
			return compileState.compile();
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
		BindMLState parseState )
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
	 * @param parseState updates the {@code Dom} being built
	 */
	private void line( 
	    String sourceLine, 
		int lineNumber, 
		BindMLState parseState )
	{   //this is the CLOSE Mark for EITHER ALONE or REPLACE Marks		    
	    
		String firstOpenTag = BindMLParser.INSTANCE.getFirstOpenTag( sourceLine );
		
		if( firstOpenTag != null )
		{   //Opened a mark this line
		    int indexOfOpenMark = sourceLine.indexOf( firstOpenTag );
		    //add everything before the open tag
		    parseState.addText( sourceLine.substring( 0, indexOfOpenMark ) );
		    
		    String matchingCloseTag = 
		    	BindMLParser.INSTANCE.closeTagFor( firstOpenTag );
		    
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
} //BindMLCompiler