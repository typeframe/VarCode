package io.varcode.context.lib.state;

import java.util.HashSet;
import java.util.Set;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.context.VarScript.ScriptInputParser;

/** Set a Default Value */
/** i.e. sets a value IF AND ONLY IF IT IS NULL */
/**
 * i.e.
 * {+$default(name,$quote(larry))+}
 * @author eric
 *
 */
public enum SetDefault 
	implements VarScript, ScriptInputParser
{
	INSTANCE;
	
	@Override
	public ScriptInputParser getInputParser() 
	{
		return this;
	}

	@Override
	public Object eval( VarContext context, String input ) 
	{
		String[] nameValue = input.split( "," );
		String varName = nameValue[ 0 ];
		Object value = context.resolveVar( varName );
		if( value == null )
		{			
			Object valueAsVar = context.get( nameValue[ 1 ] );
			if( valueAsVar != null )
			{
				context.set( varName, valueAsVar );
			}
			else
			{
				context.set( varName, nameValue[ 1 ] );
			}
		}		
		return "";
	}

	@Override
	public Object parse( VarContext context, String scriptInput ) 
	{
		return scriptInput.split( "," )[0];
	}

	@Override
	public Set<String> getAllVarNames( String input ) 
	{
		String[] nameValue = input.split( "," );
		Set<String> theSet = new HashSet<String>();
		theSet.add( nameValue[ 0 ] );
		theSet.add( nameValue[ 1 ] );
		return theSet;
	}

	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
	
}
