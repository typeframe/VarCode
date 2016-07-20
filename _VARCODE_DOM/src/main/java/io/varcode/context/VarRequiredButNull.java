package io.varcode.context;

/** The Dom requires a value for a Var, but it is null */ 
public class VarRequiredButNull
	extends VarBindException
{
	private static final long serialVersionUID = 8363867570858757199L;
	
	private final String varName;
	private final String markText;
	private final int lineNumber;
	
	public static final String N = System.lineSeparator();
	
	public VarRequiredButNull( String varName, String markText, String type, int lineNumber )
	{
		super( "Required* var \"" + varName 
	            + "*\" for " + type + ": " + N + markText + N + "on line [" 
	            + lineNumber + "] is null" );
		this.varName = varName;
		this.markText = markText;
		this.lineNumber = lineNumber;
	}
	
	public VarRequiredButNull( String varName, String markText, int lineNumber )
	{
		this( varName, markText, "mark", lineNumber );		
	}

	public String getVarName() 
	{
		return varName;
	}

	public String getMarkText() 
	{
		return markText;
	}

	public int getLineNumber() 
	{
		return lineNumber;
	}    
}
