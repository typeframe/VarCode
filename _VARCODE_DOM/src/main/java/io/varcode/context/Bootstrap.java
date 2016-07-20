package io.varcode.context;

import io.varcode.Metadata;
import io.varcode.context.lib.CoreLib;
import io.varcode.dom.VarNameAudit;

/**   
 * This class will initialize EVERY new VarContext
 * to provide default functionality for: 
 * <UL>
 *   <LI>parsing Markup into a {@code Dom}
 *   <LI>filling a {@code Dom} as a tailored document  
 * </UL>   
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Bootstrap 
{
	;	
	public static void init( VarContext context )
	{
		//load all the core libraries into the context
		CoreLib.INSTANCE.load( context );
	
		//set the expression evaluator in the context
		context.set( 
			VarContext.EXPRESSION_EVALUATOR_NAME, 
			ExpressionEvaluator_JavaScript.INSTANCE, 
			VarScope.CORE );
		
		context.set( 
			VarContext.VAR_NAME_AUDIT_NAME, 
			VarNameAudit.BASE, 
			VarScope.CORE );

		context.set( 
			VarContext.VAR_RESOLVER_NAME,
			Resolve.SmartVarResolver.INSTANCE,
			VarScope.CORE );
		
		context.set( 
			VarContext.SCRIPT_RESOLVER_NAME,
			Resolve.SmartScriptResolver.INSTANCE,
			VarScope.CORE );
		
		context.set( 
			VarContext.METADATA_NAME,
			new Metadata.SimpleMetadata(),
			VarScope.INSTANCE );
	}
}
