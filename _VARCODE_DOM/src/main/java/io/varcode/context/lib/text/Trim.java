package io.varcode.context.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

public enum Trim
	implements VarScript
{
	INSTANCE;

	public static Object doTrim( Object var )
	{
		if( var == null )
		{
			return null; 
		}
		if( var instanceof String )
		{
			return ( (String)var ).trim();
		}
		if( var.getClass().isArray() )
		{ //need to "firstCap" each element within the array
			int len = Array.getLength( var );
			String[] allCaps = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = Array.get( var, i );
				if( idx != null )
				{
					allCaps[ i ] = idx.toString().trim();
				}
				else
				{   //watch out for NPEs!
					allCaps[ i ] = null;
				}
			}
			return allCaps;
		}
		if( var instanceof Collection )
		{
			Object[] arr = ( (Collection<?>)var ).toArray();
			int len = arr.length;
			String[] allCaps = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = arr[ i ];
				if( idx != null )
				{
					allCaps[ i ] = idx.toString().trim();
				}
				else
				{ //watch out for NPEs!
					allCaps[ i ] = null;
				}
			}
			return allCaps;
		}
		return var.toString().trim();    		
	}

	@Override
	public Object eval( VarContext context, String input )
	{
		return doTrim( 
			getInputParser().parse( context, input ) );
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