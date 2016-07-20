package io.varcode.context.lib.java;

import io.varcode.context.VarBindings;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.context.lib.Library;
import io.varcode.dom.form.VarForm;
import io.varcode.dom.forml.ForMLCompiler;

/**
 * Scripts designed specifically for Tailoring Java code
 * (i.e. Validation Scripts for ClassNames, PackageNames, etc.)
 * Source Forms for Classes, Enums, packages, imports, etc.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaLib
	implements Library
{
	INSTANCE;

	/** this is a Form for importing a class */
	public static final VarForm IMPORT_CLASS_FORM = (VarForm) 
		ForMLCompiler.INSTANCE.compile(
		"import {+importClass+};" + System.lineSeparator() );
	
	@Override
	public void load( VarContext context ) 
	{
		loadAtScope( context, VarScope.LIBRARY );
	}
	
	@Override
	public void loadAtScope( VarContext varContext, VarScope varScope ) 
	{
		VarBindings bindings = varContext.getOrCreateBindings( varScope );
		
		bindings.put( getName() + "." + getVersion(), this );
		
		
		bindings.put( "validateClassName", ValidateClassName.INSTANCE );
		bindings.put( "!className", ValidateClassName.INSTANCE );
		
		bindings.put( "validateIdentifierName", ValidateIdentifierName.INSTANCE );
		bindings.put( "validateIdentifier", ValidateIdentifierName.INSTANCE );		
		bindings.put( "!identifierName", ValidateIdentifierName.INSTANCE );
		
		bindings.put( "validatePackageName", ValidatePackageName.INSTANCE );		
		bindings.put( "!packageName", ValidatePackageName.INSTANCE );				
	}

	@Override
	public String getName() 
	{
		return "JavaLib";
	}

	@Override
	public String getVersion() 
	{
		return "0.1";
	}
}
