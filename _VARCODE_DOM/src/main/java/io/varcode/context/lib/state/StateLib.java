package io.varcode.context.lib.state;

import io.varcode.context.VarBindings;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.context.VarScript;
import io.varcode.context.lib.Library;
import io.varcode.tailor.Directive;

public enum StateLib
	implements Library
{
	INSTANCE;

	@Override
	public String getName() 
	{
		return "State";
	}

	@Override
	public String getVersion() 
	{
		return "0.1";
	}

	@Override
	public void load( VarContext context ) 
	{
		loadAtScope( context, VarScope.LIBRARY );
	}

	@Override
	public void loadAtScope( VarContext context, VarScope scope ) 
	{
		 //populate the Core libraries
        VarBindings bindings = context.getOrCreateBindings( scope );
        
        bindings.put( getName() + "." + getVersion(), this );
        
        //State
        bindScript( bindings, SetDefault.INSTANCE , "setDefault", "default" );
        bindScript( bindings, SystemProperty.INSTANCE , "sysProp", "sysprop" );
        bindScript( bindings, DateTime.DATE_FORMAT, "date" );  
        bindScript( bindings, PrintState.INSTANCE , "print", "printout" );        	
	}

	public void bindDirective( 
		VarBindings bindings, Directive boundTo, String... names )
	{
		 for( int i = 0; i < names.length; i++ )
		 {
			 bindings.putDirective( names[ i ], boundTo ); 	
		 }
    }
	
    public void bindScript(
        VarBindings bindings, VarScript script, String... names )
    {
    	for( int i = 0; i < names.length; i++ )
		{
    		bindings.putScript( names[ i ], script );    		
		}
    }
}
