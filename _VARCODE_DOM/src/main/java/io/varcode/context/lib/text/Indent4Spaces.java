package io.varcode.context.lib.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.tailor.Directive;
import io.varcode.tailor.TailorState;
import io.varcode.text.TextBuffer.FillBuffer;

/**
 * Given a String, indents each line a number of spaces
 * 
 * @author M. Eric DeFazio
 */
public enum Indent4Spaces 
	implements VarScript, Directive
{
	INSTANCE;
	
	@Override
	public void preProcess( TailorState tailorState ) 
	{
		//do nothing
	}

	@Override
	public void postProcess( TailorState tailorState ) 
	{
		String original = tailorState.getTextBuffer().toString();
		tailorState.setTextBuffer( new FillBuffer(  doIndent( original ) ) );			
	}

	public StringBuilder doIndent( String input )
	{
		if (input == null )
		{
			return new StringBuilder();
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
				fb.append( "    " );
				fb.append( line );
				firstLine = false;
				line = br.readLine();
			}
			return fb;
		} 
		catch( IOException e ) 
		{
			throw new VarException( "Error indenting spaces" );
		}	
	}
	
	@Override
	public ScriptInputParser getInputParser() 
	{
		return VarScript.SMART_INPUT;
	}

	@Override
	public Object eval( VarContext context, String input ) 
	{
		String s = (String)this.getInputParser().parse( context, input );		
		return doIndent( s ).toString();
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}
