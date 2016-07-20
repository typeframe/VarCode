package io.varcode.context;

import java.util.List;

import io.varcode.Metadata;
import io.varcode.VarException;
import io.varcode.context.Resolve.ScriptResolver;
import io.varcode.context.Resolve.VarResolver;
import io.varcode.context.lib.Library;
import io.varcode.dom.VarNameAudit;
import io.varcode.tailor.Directive;

/**
 * Container for (vars, scripts) for applying "specializations" 
 * applied to {@code Markup}
 * 
 * Maintains hierarchical "Scoped" 
 * <UL>
 *   <LI>Var(s) key value associated variables 
 *   (where the key is a String and variable is any Object) 
 *   <LI>VarScript(s) String key associated with {@code VarScript}
 *   <LI>Form(s) {@code VarForm}  
 * </UL> 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarContext
{   
    public static VarContext load( Library... libraries )
    {
    	VarContext context = new VarContext();
    	for( int i = 0; i < libraries.length; i++ )
    	{
    		libraries[ i ].loadAtScope( context, VarScope.CORE_LIBRARY );
    	}
    	return context;
    }
    
    public static VarContext of( Object... nameValuePairs )
    {
        return ofScope( VarScope.INSTANCE.getValue(), nameValuePairs );
    }
    
    public static VarContext ofScope( VarScope scope, Object... nameValuePairs )
    {    	
        return ofScope( scope.getValue(), nameValuePairs );
    }

    public static VarContext ofScope( int scope, Object... nameValuePairs )
    {
        if( nameValuePairs.length % 2 != 0 )
        {
            throw new VarException( 
                "Pairs values must be passed in as pairs, length ("
                + nameValuePairs.length + ") not valid" );
        }

        if( nameValuePairs.length == 0 )
        {
            return new VarContext( );
        }
        VarContext context = new VarContext( );

        for( int i = 0; i < nameValuePairs.length; i += 2 )
        {
            context.set( 
                nameValuePairs[ i ].toString(), 
                nameValuePairs[ i + 1 ], 
                scope );
        }
        return context;
    }

    /** Bindings (by Scope) of Vars and Scripts by name for use of 
     *  specialization/Tailoring*/
    private final ScopeBindings scopeBindings;
    
    public VarContext()
    {   
        this( new ScopeBindings() );        
    }           

    public VarContext( ScopeBindings scopeBindings )
    {
        this.scopeBindings = scopeBindings;
        Bootstrap.init( this );   
    }  
    
    public ScopeBindings getScopeBindings()
    {
        return scopeBindings;
    }
    
    public VarBindings getBindings( VarScope scope )
    {
        return scopeBindings.getBindings( scope );
    }
    
    public VarBindings getBindings( int scope )
    {
        return scopeBindings.getBindings( scope );
    }
    
    public VarBindings getOrCreateBindings( VarScope scope )
    {
        return scopeBindings.getOrCreateBindings( scope );
    }
    
    public VarBindings getOrCreateBindings( int scope )
    {
        return scopeBindings.getOrCreateBindings( VarScope.fromScope( scope ) );
    }  

    public VarContext set( String name, Object value )
    {
    	return set( name, value, VarScope.INSTANCE );
    }
    
    public VarContext set( String name, Object value, VarScope scope )
    {
    	 VarBindings vb = getOrCreateBindings( scope );
    	 vb.put( name, value );
         return this;
    }
    
    public VarContext set( String name, Object value, int scope )
    {
        VarBindings vb = getOrCreateBindings( scope );
        vb.put( name, value );
        return this;
    }
    
    public Object get( String name )
    {
        return scopeBindings.get( name );
    }
    
    public Object get( String name, VarScope scope )
    {
        return scopeBindings.get( name, scope.getValue() );
    }
    
    public Object get( String name, int scope )
    {
        return scopeBindings.get( name, scope );
    }
   
    public Directive getDirective( String name )
    {
    	if( name.startsWith( "$" ) )
    	{
        	return scopeBindings.getDirective( name );
    	}
    	return scopeBindings.getDirective( "$" + name );
    }
    
    public VarScript getVarScript( String name )
    {
    	if( name.startsWith( "$" ) )
    	{
    		VarScript vs = scopeBindings.getScript( name );    	
        	return vs;
    	}
    	return (VarScript) scopeBindings.getScript( "$" + name );    	
    }

    public List<Integer>getScopes()
    {
        return ScopeBindings.ALL_SCOPES;
    }
    
    /** The scope of the var with name, -1 if not found */
    public int getScopeOf( String name )
    {
        return scopeBindings.getScopeOf( name );
    }

    public Object clear( String name, int scope )
    {
        VarBindings vb = scopeBindings.getBindings( scope );
        if( vb != null )
        {
            return vb.remove( name );
        }
        return null;
    }
    
    public String toString()
    {
    	return "_____________________________________________________" 
               + System.lineSeparator() +
    			"VarContext" + System.lineSeparator() +     		     
    			scopeBindings.toString()+ System.lineSeparator() +
    	       "_____________________________________________________";
    }
 
    public static final String VAR_RESOLVER_NAME = "_VAR_RESOLVER";
    

    public static final String SCRIPT_RESOLVER_NAME = "_SCRIPT_RESOLVER";
    
    public static final String VAR_NAME_AUDIT_NAME = "_VAR_NAME_AUDIT";
    
    public static final String EXPRESSION_EVALUATOR_NAME = "_EXPRESSION_EVALUATOR";
    
    public static final String METADATA_NAME = "_METADATA";
    
    public Metadata getMetadata()
    {
    	return (Metadata)get( METADATA_NAME );
    }

	public Object resolveVar( String varName ) 
	{
		return getVarResolver().resolveVar( this, varName );
	}
	
	public VarResolver getVarResolver() 
	{
		return (VarResolver)get( VAR_RESOLVER_NAME );
	}
	
	public VarNameAudit getVarNameAudit()
	{
		return (VarNameAudit)get( VAR_NAME_AUDIT_NAME );
	}
	
	public ExpressionEvaluator getExpressionEvaluator()
	{
		return (ExpressionEvaluator)
			scopeBindings.get( EXPRESSION_EVALUATOR_NAME );
	}

	public ScriptResolver getScriptResolver() 
	{
		return (ScriptResolver)get( SCRIPT_RESOLVER_NAME );
	}
	
	public VarScript resolveScript( String scriptName, String scriptInput ) 
	{
		ScriptResolver sr = (ScriptResolver)get( SCRIPT_RESOLVER_NAME );		
		return sr.resolveScript( this, scriptName, scriptInput );
	}
	
	public Object evaluate( String expression )
	{
		return getExpressionEvaluator().evaluate( this, expression );
	}

}
