package io.varcode.context.lib.math;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

public enum GenerateUUID
	implements VarScript
{
	INSTANCE;

	@Override
	public ScriptInputParser getInputParser() 
	{
		return VarScript.IGNORE_INPUT;
	}

	@Override
	public Object eval( VarContext context, String input ) 
	{
		return java.util.UUID.randomUUID();
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}
