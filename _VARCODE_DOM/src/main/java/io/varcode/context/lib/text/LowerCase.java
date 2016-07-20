package io.varcode.context.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

public enum LowerCase
	implements VarScript
{
	INSTANCE;

	public static Object doLowercase( Object var )
	{
		if( var == null )
		{
			return null;
		}
		if( var instanceof String )
		{
			return ( (String)var ).toLowerCase();
		}
		if( var.getClass().isArray() )
		{ //need to "firstCap" each element within the array
			int len = Array.getLength( var );
			String[] lower = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = Array.get( var, i );
				if( idx != null )
				{
					lower[ i ] = idx.toString().toLowerCase();
				}
				else
				{ //watch out for NPEs!
					lower[ i ] = null;
				}
			}
			return lower;
		}
		if( var instanceof Collection )
		{
			Object[] arr = ( (Collection<?>)var ).toArray();
			int len = arr.length;
			String[] lower = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = arr[ i ];
				if( idx != null )
				{
					lower[ i ] = idx.toString().toLowerCase();
				}
				else
				{ //watch out for NPEs!
					lower[ i ] = null;
				}
			}
			return lower;
		}
		return var.toString().toLowerCase();        
	}

	@Override
	public Object eval( 
		VarContext context, String input )
	{
    	return doLowercase( 
    		this.getInputParser().parse( context, input ) );
	}

	@Override
	public ScriptInputParser getInputParser() 
	{
		return VarScript.SMART_INPUT;
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}