package io.varcode.context.lib.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

/**
 * To prefix each of the lines with "    " ((4) spaces
 * in the (multi-line) String value of var "multiLineText"
 * 
 * {+$prefixLines(multiLineText,    )+}
 * 
 * {+$prefixLines(multiLineText, *)+}
 * 
 */
public enum PrefixEachLineWith
	implements VarScript, VarScript.ScriptInputParser
{
	INSTANCE;
	
	private static class VarAndPrefix
	{
		public Object varValue;
		public String prefix;
		
		public VarAndPrefix( Object varValue, String prefix )
		{
			this.varValue = varValue;
			this.prefix = prefix;			
		}
	}
			
	@Override
	public Object parse( VarContext context, String scriptInput ) 
	{
		String[] varAndPrefix = scriptInput.split( "," );
		
		VarAndPrefix vap =  new VarAndPrefix( 
			context.get( varAndPrefix[ 0 ] ),
			varAndPrefix[ 1 ] ); 
		return vap;
	}
	
	@Override
	public Set<String> getAllVarNames( String input ) 
	{
		Set<String> nameSet = new HashSet<String>();
		String[] varAndPrefix = input.split( "," );		
		nameSet.add( varAndPrefix[ 0 ] );
		return nameSet;
	}
	
	@Override
	public ScriptInputParser getInputParser() 
	{
		return this;
	}

	@Override
	public Object eval( VarContext context, String input ) 
	{
		VarAndPrefix vap = 
			((VarAndPrefix)parse( context, input ) );
				
		String s = vap.varValue.toString();
		return doPrefix( s, vap.prefix );				
	}				
	
	public String doPrefix( String input, String prefix )
	{
		if (input == null )
		{
			return null;
		}
		StringBuilder fb = new StringBuilder();
		
		BufferedReader br = new BufferedReader( 
			new StringReader( input ) );
		
		String line;
		try 
		{
			line = br.readLine();
			boolean firstLine = true;
			while( line != null )
			{
				if(! firstLine )
				{
					fb.append( System.lineSeparator() );					
				}
				fb.append( prefix );
				fb.append( line );
				firstLine = false;
				line = br.readLine();
			}
			return fb.toString();
		} 
		catch( IOException e ) 
		{
			throw new VarException( "Error indenting spaces" );
		}	
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}