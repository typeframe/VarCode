package io.varcode.context.lib.state;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

/**
 * This is a great debugging tool,
 * if the result for a Mark, etc. is not what you like,
 * you can print it out (or the entire state of the 
 * @author eric
 *
 */
/*{$print(*)$}*/
public enum PrintState
	implements VarScript
{
	INSTANCE;

	public static final String CONTEXT_VAR_NAME = "*";
	
	@Override
	public ScriptInputParser getInputParser() 
	{
		return VarScript.SMART_INPUT;
	}

	@Override
	public Object eval( VarContext context, String input ) 
	{
		if( input == null )
		{
			return "";
		}
		if( input.equals( CONTEXT_VAR_NAME ) || input.trim().length() == 0 )
		{	
			System.out.println( context );
			return "";
		}
		System.out.println( getInputParser().parse( context, input ) );
		return "";
	}

	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}
