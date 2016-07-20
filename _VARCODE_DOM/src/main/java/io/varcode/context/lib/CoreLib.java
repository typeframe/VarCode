package io.varcode.context.lib;

import io.varcode.context.VarBindings;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.context.lib.math.MathLib;
import io.varcode.context.lib.state.StateLib;
import io.varcode.context.lib.text.TextLib;

/**
 * Core {@code VarScript}s and {@code TailorDirective}s that are 
 * included in all {@code VarContext}s at {@VarScope.LIBRARY} scope.  
 * 
 * NOTE: this is like "importing" "java.lang.*;" where it makes
 * all the functionality ({@code VarScript}s, etc) in java.lang 
 * available to all (without having to "manually" import it each time) 
 *   
 * This allows each context to contain some base "scripts" 
 * NOTE: you can override any of these simply by binding variables
 * at a narrower {@code VarScope} than {@code VarScope.CORE} 
 */
public enum CoreLib
	implements Library
{
    INSTANCE;//Singleton enum idiom
    
	/**
	 * Loads the Core {@code VarScript}s / {@code TailorDirective}s into the 
	 * {@code VarContext} 
	 * 
	 * @param context
	 */
	public void load( VarContext context )
	{
		loadAtScope( context, VarScope.CORE_LIBRARY );
	}
	
	public String toString()
	{
		return getName() + "." + getVersion();
	}
	
    public void loadAtScope( VarContext context, VarScope scope )
    {
        //populate the Core libraries
        VarBindings bindings = context.getOrCreateBindings( scope );
        
        bindings.put( getName() + "." + getVersion(), this );
        
        TextLib.INSTANCE.loadAtScope( context, scope );
        MathLib.INSTANCE.loadAtScope( context, scope );
        StateLib.INSTANCE.loadAtScope( context, scope );
    }

	@Override
	public String getName() 
	{   
		return "Core";
	}

	@Override
	public String getVersion() 
	{
		return "0.9";
	}
	
}
