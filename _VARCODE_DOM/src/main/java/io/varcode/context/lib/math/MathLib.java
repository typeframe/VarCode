package io.varcode.context.lib.math;

import io.varcode.context.VarBindings;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.context.VarScript;
import io.varcode.context.lib.Library;
import io.varcode.tailor.Directive;

public enum MathLib
	implements Library
{
	INSTANCE;

	@Override
	public String getName() 
	{
		return "Math";
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
        
        //Math
        bindScript( bindings, Count.INSTANCE, "#", "count" );
        bindScript( bindings, CountIndex.INSTANCE, "[#]", "countIndex", "indexCount" );
        bindScript( bindings, RandomValue.INSTANCE , "random" );
        bindScript( bindings, GenerateUUID.INSTANCE , "uuid" );
        bindDirective( bindings, SHA1Checksum.INSTANCE , "checksum", "sha1" );		
	}

	public void bindDirective( 
		VarBindings bindings, Directive boundTo, String... names )
	{
		 for( int i = 0; i < names.length; i++ )
		 {
			 bindings.putDirective( names[ i ], boundTo ); //firstCap    		
		 }
    }
	
    public void bindScript(
        VarBindings bindings, VarScript script, String... names )
    {
    	for( int i = 0; i < names.length; i++ )
		{
    		bindings.putScript( names[ i ], script ); //firstCap    		
		}
    }
}
