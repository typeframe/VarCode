package io.varcode.dom;

import io.varcode.text.ParseException;

/**
 * Failure that occurs when the Markup source is being Read in / parsed
 * (Synonymous with a "Compile Time" exception when reading a source file).
 * <BR><BR> 
 * 
 * Attempt to Read/Parse/Compile a {@code VarCode} from an input source 
 * failed. 
 */
public class MarkupException
	extends ParseException
{
	private static final long serialVersionUID = 4145424684335838684L;

	public String markText = null;
	
	public int lineNumber = -1;
	
    public MarkupException( String message, Throwable throwable )
    {
    	super( message, throwable );
    }
    
    public MarkupException( 
    	String message, String markText, int lineNumber )
    {
    	super( message );
        this.markText = markText;
        this.lineNumber = lineNumber;
    }
        
    public MarkupException( 
        String message, String markText, int lineNumber, Throwable throwable )
    {
        super( message, throwable );
        this.markText = markText;
        this.lineNumber = lineNumber;
    }
        
    public MarkupException( String message )
    {
        super( message );
    }

    public MarkupException( Throwable throwable )
    {
        super( throwable );
    }
}