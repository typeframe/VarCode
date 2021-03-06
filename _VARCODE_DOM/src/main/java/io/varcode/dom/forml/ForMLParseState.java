package io.varcode.dom.forml;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import io.varcode.context.VarContext;
import io.varcode.dom.FillInTheBlanks;
import io.varcode.dom.MarkupException;
import io.varcode.dom.ParseState;
import io.varcode.dom.form.FormDom;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.Mark.ParseStateAware;
import io.varcode.text.ParseException;

/**
 * Mutable Intermediate Representation (IR) Parse State for producing a 
 * {@code Form} while source is being read in and parsed
 * 
 * Contains {@code MarkActions}s assigned to FillBlanks and static text.
 * 
 * Internally acts like a "State Machine" that follows: 
 * 
 * <OL>
 *   <LI>seek next open tag within the text (at {@code tagOpenIndex})
 *   <LI>add all static text between {@code charCursor} and 
 *   {@code tagOpenIndex} to the state
 *   <LI>seek the matching "close" tag {@code tagCloseIndex} within the 
 *   text after the {@code tagOpenIndex}.
 *   <LI>using the {@code MarkParser} parse the Mark text between {@code tagOpenIndex}
 *   and {@code tagCloseIndex} and add the parsed {@code Mark} to the {@code CompileState} 
 *   (signifying the blank if appropriate)
 *   <LI>set {@code charCursor} to after {@code tagCloseIndex}
 *   <LI>goto step (1) until the end of document is reached  
 * </OL>
 * 
 * The following methods mutate the contents of the State
 * <UL>
 * <LI>startMark
 * <LI>appendToMark
 * <LI>completeMark
 * <LI>appendText
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ForMLParseState 
    implements ParseState
{
    public static final String N = System.lineSeparator();
    
    /** All static text and a BitSet indicating {@code MarkAction}s locations */
    public final FillInTheBlanks.Builder fillBuilder;
    
    /** stores the character indexes of ALL {@code Mark}s within the form markup */
    public final BitSet allMarkIndexes;
    
    /** All marks extracted from the form markup */  
    public final List<Mark> allMarks;

    /** If the current mark is open, the matching close tag to complete the mark
     * (otherwise null) */ 
    private String closeTagForCurrentOpenMark;
    
    /** Buffer for text inside a Mark that spans lines */
    private final StringBuilder markBuffer;
    
    /** contains any modules for parsing (effectively immutable) */
    private final VarContext parseContext;

    /** *MUTABLE* current character cursor index within the markup */
    public int cursorIndex;
    
    public ForMLParseState()
    {
    	this( new VarContext() );
    }
    
    public ForMLParseState( VarContext parseContext )
    {
        this.fillBuilder = new FillInTheBlanks.Builder();
        this.allMarkIndexes = new BitSet();
        this.allMarks = new ArrayList<Mark>();
        this.parseContext = parseContext;
        
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
        this.fillBuilder.text( staticText );
        this.cursorIndex += staticText.length();
    }
    
    /** Append text to the exiting open mark*/
    public void startMark( String markText, String openTag )
    {
        this.closeTagForCurrentOpenMark = ForMLParser.INSTANCE.closeTagFor( openTag );
        this.markBuffer.append( markText );
    }
    
    /** adds text to a Mark Buffer */
    public void addToMark( String markText )
    {
        this.markBuffer.append( markText );
    }
    
    /**Reserves a Fillable blank at the current char position in the Form */
    public void reserveBlank()
    {
    	this.fillBuilder.blank(); 
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
              + "... expected content ending with closing Mark \""
              + closeTagForCurrentOpenMark + "\"" );
        }
        markBuffer.append( contentWithCloseTag );
        
        
        Mark ma = ForMLParser.INSTANCE.parseMark( 
        	parseContext,	
            markBuffer.toString(), 
            lineNumber );
        allMarks.add( ma );
        
        allMarkIndexes.set( cursorIndex );
        
        this.cursorIndex ++;
        
        if( ma instanceof Mark.ParseStateAware )
        {   //MarkActions often need to update the existing ParseState (i.e.
            //add a Blank at a certain location of the {@VarCode} so it
            //can populate it later
            ParseStateAware sa = (ParseStateAware)ma;
            sa.onMarkParsed( this );
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
    public FormDom compile() 
    {
        if( isMarkOpen() )
        {   //UNCLOSED MARK
            throw new MarkupException( 
                "Unclosed Form Mark :" + N 
              + getMarkContents() + N 
              + "...expected close Tag \"" + this.closeTagForCurrentOpenMark + "\"" );
        }    
        return new FormDom( 
            fillBuilder.compile(), 
            allMarks.toArray( new Mark[ 0 ] ),
            allMarkIndexes );
    }
}