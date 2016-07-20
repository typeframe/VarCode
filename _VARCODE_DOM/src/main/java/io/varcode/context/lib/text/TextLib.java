package io.varcode.context.lib.text;

import io.varcode.context.VarBindings;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.context.VarScript;
import io.varcode.context.lib.Library;
import io.varcode.tailor.Directive;

public enum TextLib
	implements Library
{
	INSTANCE;

	@Override
	public String getName() 
	{
		return "Text";
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
		 VarBindings bindings = context.getOrCreateBindings( scope );
	        
	     bindings.put( getName() + "." + getVersion(), this );
	     
		 bindScript( bindings, AllCap.INSTANCE, "^^", "cap", "caps", "allCap" );
	     bindScript( bindings, FirstCap.INSTANCE, "^", "firstCap", "firstCaps" );	     
	     bindScript( bindings, LowerCase.INSTANCE, "lower", "allLower" );
	     bindScript( bindings, FirstLower.INSTANCE, "firstLower" );
	     bindScript( bindings, EscapeString.INSTANCE, "escapeString", "escape" );
	     bindScript( bindings, Trim.INSTANCE, "trim" );
	     bindScript( bindings, Quote.INSTANCE, "\"", "quote" );
	     bindScript( bindings, RemoveEmptyLines.INSTANCE, "-{}", "removeEmptyLines" );  
	     bindDirective( bindings, StripMarks.INSTANCE , "stripMarks" );
	     bindScript( bindings, Indent4Spaces.INSTANCE, "indent", "indent4", "indent4Spaces" );
	     bindScript( bindings, PrefixEachLineWith.INSTANCE, "prefixEachLine", "prefixLines" );
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
