package io.varcode.context.lib.java;

import io.varcode.context.VarContext;
import io.varcode.context.lib.state.ContextValidator;

/**
 * Validation Helper Methods for validating Tailored Java classes  
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class JavaContextValidator
	extends ContextValidator
{
	public static void validateClassName( VarContext context )
	{
		validateClassName( context, "className" );
	}
	
	public static void validateClassName( VarContext context, String varName )
	{
		ValidateClassName.INSTANCE.eval( context, varName );
	}
	
	public static void validatePackageName( VarContext context )
	{
		validatePackageName( context, "packageName" );
	}
	
	public static void validatePackageName( VarContext context, String varName )
	{
		ValidatePackageName.INSTANCE.eval(context, varName );
	}
	
	public static void validateIdentifiers( VarContext context, String varName )
	{
		ValidateIdentifierName.INSTANCE.eval(context, varName );
	}	
}
