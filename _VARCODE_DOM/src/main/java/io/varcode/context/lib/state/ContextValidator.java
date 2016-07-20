package io.varcode.context.lib.state;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.lib.math.Count;
import io.varcode.tailor.Directive;
import io.varcode.tailor.TailorState;

/**
 * Provides a means of deeply validating the {@code VarContext} as the input
 * during the pre-processing step of tailoring code.
 * <UL>
 *  <LI>Cross Field Validation
 * </UL>
 *    
 * @see io.varcode.tailor.Tailor   
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class ContextValidator
	extends Directive.PreProcessor
{	
	
	@Override
	public void preProcess( TailorState tailorState ) 
	{
		validateContext( tailorState.getContext() );
	}
	
	public abstract void validateContext( VarContext context );
	
	public static String getString( VarContext context, String name )
	{
		Object val = context.get( name );
		if( val != null )
		{
			return val.toString();
		}
		return null;
	}
		
	public static Integer getCount( VarContext context, String varName )
	{
		return Count.INSTANCE.getCount( context, varName );
	}
	
	public static Integer assertCount( VarContext context, String varName, Integer count )
	{
		Integer actual = getCount ( context, varName );
		if( count !=  actual )
		{
			throw new VarException(
				"Count of var \"" + varName + "\" is (" + actual + ") expected (" + count + ")" ); 
		}			
		return actual;
	}

	public static Integer assertCount( VarContext context, String varName, int min, int max )
	{
		Integer actual = getCount ( context, varName );
		if ( actual == null || actual < min || actual > max )
		{
			throw new VarException(
				"Count of var \"" + varName+"\" is (" + actual + ") expected  value in [" + min + "..." + max + "]" ); 
		}			
		return actual;
	}
}

