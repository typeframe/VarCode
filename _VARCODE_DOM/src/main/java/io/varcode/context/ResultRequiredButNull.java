package io.varcode.context;

/** The Markup requires the Script return a value, but it returned a null*/ 
public class ResultRequiredButNull
	extends VarBindException
{
	private static final long serialVersionUID = -4144904828522592133L;
	
	private final String scriptName;
	
	private final String markText;
	
	private final int lineNumber;
	
	public static final String N = System.lineSeparator();
	
	public ResultRequiredButNull( String scriptName, String markText, int lineNumber )
	{
		super( "Required* script \"" + scriptName 
	            + "\" for mark: " + N + markText + N + "on line [" 
	            + lineNumber + "] result is null" );
		this.scriptName = scriptName;
		this.markText = markText;
		this.lineNumber = lineNumber;
	}

	public ResultRequiredButNull( String scriptName, String scriptInput, String markText, int lineNumber )
	{
		super( "Required* script \"" + scriptName +"\" with input (" + scriptInput + ")"
	            + " for mark: " + N + markText + N + "on line [" 
	            + lineNumber + "] result is null" );
		this.scriptName = scriptName;
		this.markText = markText;
		this.lineNumber = lineNumber;
	}
	
	public String getScriptName() 
	{
		return scriptName;
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
