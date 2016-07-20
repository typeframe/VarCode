package io.varcode.dom.bindml;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import io.varcode.Lang;
import io.varcode.Metadata;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.dom.Dom;
import io.varcode.dom.DomParseState;
import io.varcode.dom.FillInTheBlanks;
import io.varcode.dom.FillInTheBlanks.Builder;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.Mark.BoundStatically;
import io.varcode.dom.mark.Mark.ParseStateAware;
import io.varcode.text.ParseException;

/**
 * Mutable Intermediate Representation (IR) Compile State for producing a 
 * {@code Markup} while Markup source is being read in and parsed by the 
 * {@code MarkupParser}. 
 * Contains {@code MarkActions}s assigned to {@code FillBlanks} and static text.
 * 
 * Internally acts like a "State Machine" that follows: 
 * 
 * <OL>
 *   <LI>seek next open tag within the text (at {@code tagOpenIndex})
 *   <LI>add all static text between {@code charCursor} and 
 *   {@code tagOpenIndex} to the state
 *   <LI>seek the matching "close" tag {@code tagCloseIndex} within the 
 *   text after the {@code tagOpenIndex}.
 *   <LI>using the {@code MarkupParser} parse the text between {@code tagOpenIndex}
 *   and {@code tagCloseIndex} and add the parsed {@code MarkAction} to 
 *   the {@code CodeMarkupState} (adding a fill blank if appropriate)
 *   <LI>set {@code charCursor} to the char after {@code tagCloseIndex}
 *   <LI>goto step (1) until the end of document is reached  
 * </OL>
 * 
 * <BLOCKQUOTE>
 * The {@code Dom} does not create a <A HREF="https://en.wikipedia.org/wiki/Parse_tree">parse tree</A> 
 * or an <A HREF="https://en.wikipedia.org/wiki/Abstract_syntax_tree">"AST"</A> 
 * (we don't need a "tree" at all; each "Mark is a self-contained command"). 
 * The {@code BindMLCompiler} <B>sequentially</B> reads text and each {@code Mark}s 
 * (matching close tags to open tags) within the markup then parses each {@Mark} 
 * one (to add to the while the document is being read in)
 * </BLOCKQUOTE>
 *   
 * @author M. Eric DeFazio eric@varcode.io
 */
public class BindMLState 
    implements DomParseState
{
    public static final String N = System.lineSeparator();
    
    /** Contains Utilities and Statically defined vars in the {@code Dom}*/
    public final VarContext parseContext;
        
    /** Builds a document with blanks and {@code Mark}s*/
    public final FillInTheBlanks.Builder domBuilder;
    
    /** stores the character indexes of ALL {@code Mark}s within the {@code Dom} */
    public final BitSet allMarkIndexes;
    
    /** All {@code Mark}s extracted from the {@code Dom} */  
    public final List<Mark> allMarks;

    /** Buffer for text bound to be a Mark inside a {@code Mark}s that spans lines */
    private final StringBuilder markBuffer;
    
    /** * MUTABLE* If the current Mark is Open,  the matching close tag to look 
     * for (otherwise null) */ 
    private String closeTagForCurrentOpenMark;
    
    /** *MUTABLE* current character cursor index within the Markup */
    public int cursorIndex;
    
    public BindMLState( VarContext parseContext )
    {
        this.parseContext = parseContext;
        this.domBuilder = new FillInTheBlanks.Builder();
        this.allMarkIndexes = new BitSet();
        this.allMarks = new ArrayList<Mark>();
        this.markBuffer = new StringBuilder();
        this.cursorIndex = 0;
    }
    
    public boolean isMarkOpen()
    {
        return markBuffer.length() > 0;
    }
    
    /** add "static" text to the document */ 
    public void addText( String staticText )
    {
        domBuilder.text( staticText );
        cursorIndex += staticText.length();
    }
    
    /** Append text to the exiting open mark*/
    public void startMark( String markText, String openTag )
    {
        this.closeTagForCurrentOpenMark = BindMLParser.INSTANCE.closeTagFor( openTag );
        this.markBuffer.append( markText );
    }
    
    /** adds more text to a Mark */
    public void addToMark( String markText )
    {
        this.markBuffer.append( markText );
    }
    
    /**Reserves a Fillable blank at the current char position in the Form */
    public void reserveBlank()
    {
    	domBuilder.blank(); 
    }
    
    /** 
     * Add content and the closing tag to the current (buffered) mark, 
     * then create/add the {@code MarkAction} and return the State
     * @param contentWithCloseTag
     * @param lineNumber
     */
    public void completeMark( String contentWithCloseTag, int lineNumber )
    {
        if( isMarkOpen() && 
            !contentWithCloseTag.endsWith( closeTagForCurrentOpenMark ) )
        {
            throw new ParseException(
                "could not complete a Mark : " + N
              + markBuffer.toString() + N + " with : " + N 
              + contentWithCloseTag + N  
              + "... expected content ending with closing tag \""
              + closeTagForCurrentOpenMark + "\"" );
        }
        markBuffer.append( contentWithCloseTag );
        String theMarkAsString = markBuffer.toString();
        
        //count the number of lines the Mark spans 
        int numLines = Lines.countTotal( theMarkAsString );
        Mark markAction = BindMLParser.INSTANCE.parseMark(
            this.parseContext,
            theMarkAsString, 
            ( lineNumber - ( numLines -1 ) ) //set the line number where the mark STARTS
            );
        
        allMarks.add( markAction );
        
        allMarkIndexes.set( cursorIndex );
        
        this.cursorIndex ++;
        
        if( markAction instanceof ParseStateAware )
        {   //MarkActions often need to update the existing MarkupState 
            //(i.e. add a Blank at a certain location of the {@Markup} so it
            //can populate it later, 
            //derive a static value
            //
            ParseStateAware markStateAware = (ParseStateAware)markAction;
            markStateAware.onMarkParsed( this );
        }            
        //If the Mark is a Static Var assignment 
        //Setting MetaData for the Markup 
        if( markAction instanceof BoundStatically )
        {
        	BoundStatically boundStatically = (BoundStatically)markAction;
            boundStatically.onMarkParsed( this );
        }
        resetMark();
    }
    
    public String getCloseTagForOpenMark()
    {
        return this.closeTagForCurrentOpenMark;
    }
    
    public String getMarkContents()
    {
        return markBuffer.toString();
    }
    
    private void resetMark()
    {
        markBuffer.delete( 0, markBuffer.length() );
        closeTagForCurrentOpenMark = null;
    }
    
    /** 
     * Convert the mutable Intermediate Representation (parsed by the compiler) 
     * into an immutable {@code VarCode}.
     * 
     * @return the immutable {@VarCode}  
     */
    public Dom compile() 
    {
        if( isMarkOpen() )
        {   //UNCLOSED MARK
            throw new ParseException( 
                "Unclosed Mark : " + N 
              + getMarkContents() + N 
              + "EXPECTED close Tag \"" + this.closeTagForCurrentOpenMark + "\"");
        }    
        
        Lang lang = null;
        if( ( parseContext != null ) && parseContext.getMetadata() != null )
        {
        	lang = (Lang) parseContext.getMetadata().get( Dom.LANG );
        }
        
        return new Dom(
        	lang,	
            domBuilder.compile(), 
            allMarks.toArray( new Mark[ 0 ] ),
            allMarkIndexes, 
            parseContext.getOrCreateBindings( VarScope.STATIC ), 
            parseContext.getMetadata() );
    }

    public Builder getFillBuilder()
    {
        return domBuilder;
    }

    public Metadata getMetadata()
    {
        return getParseContext().getMetadata();
    }

    public VarContext getParseContext()
    {
        return this.parseContext;
    }

	@Override
	public void setStaticVar( String varName, Object value ) 
	{
		parseContext.getOrCreateBindings(VarScope.STATIC).put( varName, value );
	}   
}