package io.varcode.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.varcode.VarException;
import io.varcode.dom.MarkupComponent;
import io.varcode.tailor.TailorComponent;

/**
 * Algorithms for resolving values for named Vars and Scripts
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Resolve 
{
	;	
/*{-?(removeLog==true):*/
	private static final Logger LOG = 
        LoggerFactory.getLogger( Resolve.class );
/*-}*/
	
	/**
	 * Knows how to resolve the value of a (data) Var given it's name
	 */
	public interface VarResolver
		extends TailorComponent, MarkupComponent
	{		
		/**
		 * @param context the context to resolve the Var
		 * @param varName the name of the var to resolve
		 * @return the var or null
		 */
		public Object resolveVar( VarContext context, String varName );
	}
	
	/** 
	 * Knows how to resolve a VarScript
	 */
	public interface ScriptResolver
		extends TailorComponent, MarkupComponent
	{	
		public VarScript resolveScript( 
			VarContext context, String scriptName, String scriptInput );
	}

	/**
	 * Adapts a Static Method call to the {@code VarScript} interface
	 * so that we might call static methods as if they implemented
	 * {@code VarScript}  
	 *  
	 */
	public static class StaticMethodScriptAdapter
		implements VarScript
	{
		private final Method method;
		
		private final Object[] params;
		
		public StaticMethodScriptAdapter( Method method, Object... params )
		{
			this.method = method;
			if( params.length == 0 )
			{
				this.params = null;
			} 
			else
			{
				this.params = params;
			}			
		}

		@Override
		public ScriptInputParser getInputParser() 
		{
			return VarScript.IGNORE_INPUT;
		}

		@Override
		public Object eval( VarContext context, String input ) 
		{
			try 
			{
				return method.invoke( null, params );
			} 
			catch( IllegalAccessException e ) 
			{
				throw new EvalException( e );
			} 
			catch (IllegalArgumentException e) 
			{
				throw new EvalException( e );
			} 
			catch (InvocationTargetException e) 
			{
				if( e.getCause() instanceof VarException )
				{
					throw (VarException) e.getCause();
				}
				throw new EvalException( e.getCause() );
			}
		}
		
		public String toString()
		{
			return "Wrapper to " + method.toString();
		}		
	}
	
	public enum SmartScriptResolver
		implements ScriptResolver
	{
		INSTANCE;
		
		private static Class<?> getClassForName( String className )
		{
			try 
			{
				Class<?>c = Class.forName( className ); 
				LOG.debug( "found class : \""+ className +"\"");
				
				return c;
			} 
			catch ( ClassNotFoundException e ) 
			{
				LOG.debug( "class \"" + className + "\" not found " );
				return null;
			}
		}
		
		private static Method tryAndGetMethod( 
			Class<?> clazz, String name, Class<?>...params )
		{
			try 
			{
				return clazz.getMethod( name, params );
			} 
			catch( NoSuchMethodException e ) 
			{
				return null;
			} 
			catch( SecurityException e ) 
			{
				return null;
			}
		}
		
		private static VarScript findStaticMethod( 
			VarContext context,  
			Class<?> clazz, 
			String methodName,
			String scriptInput )
		{
			try
			{
				Method m = tryAndGetMethod( 
					clazz, methodName, VarContext.class, String.class );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m, context, scriptInput );
				}
				m = tryAndGetMethod( 
					clazz, methodName, VarContext.class );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m, context );
				}
				m = tryAndGetMethod( 
						clazz, methodName, String.class );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m, scriptInput );
				}
				
				m = tryAndGetMethod( 
						clazz, methodName, Object.class );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m, scriptInput );
				}
				
				m = tryAndGetMethod( clazz, methodName );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m );
				}
				return null;				
			}
			catch( Exception e )
			{
				return null;
			}
		}
		
		/** Return the Singleton INSTANCE VarScript */  
		private static VarScript getSingletonField( Class<?> clazz )
		{
			try
			{							
				Field field = clazz.getField( "INSTANCE" );
				if( Modifier.isStatic( field.getModifiers() ) )
				{
					return (VarScript)field.get( null );
				}
				return null;
			}
			catch( Exception e )
			{							
				return null;
			}	
		}
		@Override
		public VarScript resolveScript( 
			VarContext context, String scriptName, String scriptInput ) 
		{
			// 1) see if the script is loaded in the context
			String scriptLookupName = "$" + scriptName ;
			LOG.trace( "   resolving script \"" + scriptLookupName + "\""  );
			Object vs = context.get( scriptLookupName );
			if( vs != null )
			{
				if( vs instanceof VarScript )
				{
					LOG.trace( "   resolved script \"" + scriptLookupName + "\" in context " + vs );
					return (VarScript)vs;
				}				
			}	
			
			//I COULD have ScriptBindings where I "manually" REgister/
			// assign scripts (i.e. ExpressionScript)s to names
			
			// IF the name contains a '.' (run (2) and (3) 
			int indexOfLastDot = scriptName.lastIndexOf( '.' );
			
			if( indexOfLastDot > 0 )
			{
				String theMethodName = scriptName.substring( 
					indexOfLastDot + 1, 
					scriptName.length() );
				
				String theClassName = scriptName.substring( 0, indexOfLastDot );
				
				if( LOG.isTraceEnabled() ) { 
					LOG.trace( "   trying to resolve class \"" + theClassName + "\"" );
				}
				Class<?> clazz = getClassForName( theClassName );
				
				if( clazz != null )
				{	
					if( LOG.isTraceEnabled() ) {
						LOG.trace( "  resolved class \"" + clazz  + "\"" );
					}
					//does the class implement VarScript?
					if( VarScript.class.isAssignableFrom( clazz  ) )
					{
						if( LOG.isTraceEnabled() ) {
							LOG.trace( "  class \"" + clazz  + "\" is a VarScript" );
						}
						if( clazz.isEnum() )
						{
							if( LOG.isTraceEnabled() ) {
								LOG.trace( "  class \"" + clazz  + "\" is a VarScript & an enum " );
							}
							return (VarScript)clazz.getEnumConstants()[ 0 ];
						}
						Object singleton = getSingletonField( clazz );
						if( singleton != null && LOG.isTraceEnabled() ) 
						{
							LOG.trace( "  returning INSTANCE field on \"" + clazz  + "\" as VarScript" );
						}
						try
						{   LOG.trace( "  trying to create (no-arg) instance of \"" + clazz  + "\" as VarScript" );
							return (VarScript)clazz.newInstance();
						}
						catch( Exception e )
						{
							LOG.trace( "  failed creating a (no-arg) instance of \"" + clazz  + "\" as VarScript" );
							return null;
						}
					}
					else
					{   //found a class, now find the method, (just chooses the first one by this name)
						if( LOG.isTraceEnabled() ) {
							LOG.trace( "  resolving static Method \"" + theMethodName 
								+ "\" on class \"" + clazz + "\"" );
						}
						return findStaticMethod( 
							context, clazz, theMethodName, scriptInput );
					}					
				}				
			}
			return null;			
		}		
	}
	
	/**
	 * Resolves the Var "through" the {@code ExpressionEvaluator} 
	 * (checking the vars that were originated within the ExpressionEvalator
	 * And the vars that originated within the VarScopeBindings of the VarContext.  
	 */
	public enum SmartVarResolver
		implements VarResolver
	{	
		INSTANCE;
	
		/**
		 * Tries to resolve the object, returning null if the 
		 * Var is not bound (as EITHER:
		 * <UL>
		 *   <LI>a var in the ( Javascript ) Expression engine
		 *   <LI>a value within the {@code ScopeBindings}
		 * </UL>   
		 */
		public Object resolveVar( VarContext context, String varName ) 
		{
			if( varName == null || varName.trim().length() == 0 )
			{
				return null;
			}
			ExpressionEvaluator ee = context.getExpressionEvaluator();
	
			// run this expression, which will determine if 
			// the varName is "bound" in EITHER: 
			//   the ScopeBindings  (i.e. "{##a:100##}" )
			//   instance "expressions" in JS (i.e. "{((var a = 100;))}" )
			String expressionText = 
				"typeof " + varName + " !== typeof undefined ? true : false;";
	
			//NOTE: this will try to resolve the varName in EITHER the scopeBindings
			// 	or the INSTANCE
			try
			{
				Object isSet = ee.evaluate( 
					context.getScopeBindings(), 
					expressionText );
				
				if( (Boolean)isSet )
				{   //resolve the ACTUAL value (from the varName)
					return ee.evaluate( context.getScopeBindings(),  varName );
				}
			}
			catch( Exception  e )
			{
				return null;
			}
	
				
			return null;
		}
	}	
}
