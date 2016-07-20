package io.varcode.context.lib.state;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

public enum SystemProperty
	implements VarScript
{
	INSTANCE;
	
	@Override
	public ScriptInputParser getInputParser() 
	{
		return VarScript.STRING_INPUT;
	}

	@Override
	public Object eval( VarContext context, String input ) 
	{
		//System.out.println( "Getting property : " + input +" : "+System.getProperty( input ) );
		return System.getProperty( input );
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}
