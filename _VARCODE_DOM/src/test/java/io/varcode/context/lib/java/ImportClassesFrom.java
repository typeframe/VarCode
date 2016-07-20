package io.varcode.context.lib.java;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.context.VarScript.ScriptInputParser;
/** 
 * Pass in a bunch of var names, and read all of the Vars
 * and import all classes for these vars RESOLVE a VAR called IMPORTS_VAR_NAME
 * that contains the Imports to be printed
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */

/*{#$importsFrom(fields,labels)#}*/
public enum ImportClassesFrom
	implements VarScript, ScriptInputParser
{
	INSTANCE;

	public static final String IMPORTS_VAR_NAME = "importClasses";
	
	/*
	public static final Markup IMPORT_CLASS = BindML.compile(
		"import {+importClass+};" + System.lineSeparator() );
	*/
	@Override
	public ScriptInputParser getInputParser() 
	{
		return this;
	}

	@Override
	public Object eval( VarContext context, String input ) 
	{
		//
		//List<Object> existingImports = getExistingImports( context );
		Object existingImports = context.get( IMPORTS_VAR_NAME );
		if( existingImports != null )
		{
			throw new VarException(
				"VAR name \""+IMPORTS_VAR_NAME+"\" was already set; "
			  + "it must be blank to derive import classes" );
		}
		//find the classes given the input
		Set<Class<?>>classesSet = findClasses( context, input );
		Class<?>[] classes = classesSet.toArray( new Class<?>[ 0 ] );
		return classes;
	}

	public void addClassesForValue( Object value, Class<?> clazz, Set<Class<?>> classes )
	{
		Package pkg = clazz.getPackage(); 
		if( clazz.isPrimitive() 
		    || ( pkg != null && pkg.getName().startsWith( "java.lang" ) ) )
		{
			System.out.println( "EXCLUDED" );
		}
		else
		{
			if( value instanceof Class )
			{
				Class<?> classVal = (Class<?>) value;
				if( classVal.isPrimitive() )
				{
				    return;				     
				}
				Package classPkg = classVal.getPackage(); 
				if( classPkg != null && classPkg.getName().startsWith( "java.lang" ) )
				{
					return;
				}
				classes.add( classVal );
			}
			if( clazz.isArray() )
			{
				int len = Array.getLength( value );
				for( int i = 0; i < len; i++ )
				{
					Object val = Array.get( value, i );
					if( value != null )
					{
						addClassesForValue( val, val.getClass(), classes );
					}
				}
			}
			else if( value instanceof Collection )
			{
				//add the collection class
				classes.add( value.getClass() );
				
				//now look through the values and add them
				Collection<?> coll = (Collection<?>)value;
				Iterator<?> it = coll.iterator();
				while( it.hasNext() )
				{
					Object val = it.next();
					if( value != null )
					{
						addClassesForValue( val, val.getClass(), classes );
					}
				}
			}
		}
	}
	
	private Set<Class<?>> findClasses( VarContext context, String input ) 
	{
		Iterator<String> varNames = getAllVarNames( input ).iterator();
		Set<Class<?>> classes = new HashSet<Class<?>>();
		while( varNames.hasNext() )
		{
			String varName = varNames.next();
			Object varValue = context.get( varName );
			if( varValue instanceof Class )
			{
				classes.add( (Class<?>)varValue );
				continue;
			}
			if( varValue != null )
			{
				Class<?> varClass = varValue.getClass(); 
				if( varClass.isPrimitive() )
				{
					System.out.println( "PRIMITIVE" );
				    continue;				     
				}
				Package pkg = varClass.getPackage(); 
				if( pkg != null && pkg.getName().startsWith( "java.lang" ) )
				{
					//System.out.println( pkg );
					continue;
				}
				if( varClass.isArray() )
				{
					System.out.println( "ARRAY" );
					Package componentPkg = varClass.getComponentType().getPackage(); 
					if( !varClass.isPrimitive()  
						&& componentPkg != null
						&& !componentPkg.getName().startsWith( "java.lang" ) )
					{
						System.out.println( varClass.getComponentType() );
						classes.add( varClass.getComponentType() );						
					}			
				}
				else if( varClass.isSynthetic() )
				{
					System.out.println( "Synthetic class" );
				}
				else if( varClass.isMemberClass() )
				{
					System.out.println( "MEMBER CLASS" );
					classes.add( varClass );
				} 
				else
				{
					System.out.println( "ADDING "+ varClass );
					classes.add( varClass );
				}
					/** UGG GENERICS HOW I HATE THE LET ME COUNT THE WAYS
					else if( varClass.getTypeParameters().length > 1 )
					{
						varClass.getTypeParameters()[ 0 ].
					}
					*/
					
				
			}
		}
		return classes;
	}

	@Override
	public Object parse( VarContext context, String scriptInput ) 
	{
		return getAllVarNames( scriptInput ).toArray( new String[ 0 ] );
	
	}

	@Override
	public Set<String> getAllVarNames( String input ) 
	{
		String[] varNames = input.split( "," );
		Set<String>uniqueVarNames = new HashSet<String>();
		
		for( int i = 0; i < varNames.length; i++ )
		{
			uniqueVarNames.add( varNames[ i ].trim() );
			
		}
		return uniqueVarNames;
	}	
}
