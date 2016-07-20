package io.varcode.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.dom.Dom;
import io.varcode.java.javac.InMemoryJavaClassLoader;
import io.varcode.java.javac.InMemoryJavaSource;
import io.varcode.java.javac.InMemoryJavac;
import io.varcode.java.javac.JavacOptions;
import io.varcode.tailor.Directive;
import io.varcode.tailor.Tailor;
import io.varcode.tailor.TailorState;
import io.varcode.text.SmartBuffer;
import io.varcode.text.TextBuffer;


public enum JavaTailor
{
    INSTANCE;
	
    private static final Logger LOG = 
    	LoggerFactory.getLogger( JavaTailor.class );

	private static void addPropertyIfNonNull( StringBuilder sb, String propertyName )
	{
		String propertyValue = System.getProperty( propertyName );
		if( propertyValue != null && propertyValue.trim().length() > 0 )
		{
			sb.append( propertyName );
			sb.append( " = " );
			sb.append( propertyValue );
			sb.append( System.lineSeparator() );
		}		
	}
	
	/**
	 * Describes the Current Java Environment (at Runtime)
	 * 
	 * @return a String that details particulars about the Java Runtime
	 */
	public static String describeCurrentJavaEnvironment()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "--- Current Java Environment --- " );
		sb.append( System.lineSeparator() );
		addPropertyIfNonNull( sb, "java.vm.name" );
		addPropertyIfNonNull( sb, "java.runtime.version" );
		addPropertyIfNonNull( sb, "java.library.path" );
		addPropertyIfNonNull( sb, "java.vm.version" );
		addPropertyIfNonNull( sb, "sun.boot.library.path" );
		sb.append( "--------------------------------- " );
		sb.append( System.lineSeparator() );
		return sb.toString();
	}
	
    /**
     * 
     * @param varContext containing the specialization to be applied to the Markup
     * @return
     */
    public static InMemoryJavaSource javaCode( 
    	Dom dom, VarContext context, Directive...directives )    
    {        
    	TextBuffer textBuffer = SmartBuffer.createInstance();
        
    	TailorState tailorState = 
        	new TailorState( dom, context, textBuffer, directives );
    	
        Tailor.tailor( tailorState );
        
        return JavaCodeFactory.doCreate( tailorState ); //dom, context, textBuffer );            
    }
    
    /**
     * 
     * @param javaCode
     * @param compilerOptions Optional Compiler Arguments (@see JavacOptions)
     * @return
     */
    public static Class<?> loadJavaClass( 
    	InMemoryJavaSource javaCode,
    	JavacOptions.CompilerOption...compilerOptions )
    {
        InMemoryJavaClassLoader inMemClassLoader = new InMemoryJavaClassLoader();
        return loadJavaClass( inMemClassLoader, javaCode, compilerOptions );        
    }
    
    public static Map<String, Class<?>> loadJavaClasses(
    	List<InMemoryJavaSource> javaCode,
    	JavacOptions.CompilerOption...compilerOptions )
    {
    	InMemoryJavaClassLoader inMemClassLoader = new InMemoryJavaClassLoader();
        return loadJavaClasses( inMemClassLoader, javaCode, compilerOptions );        
    }
    
    public static Map<String, Class<?>> loadJavaClasses(
    	InMemoryJavaClassLoader classLoader,	
       	List<InMemoryJavaSource> javaCode,
       	JavacOptions.CompilerOption...compilerOptions )
    {
    	Map<String, Class<?>> compiled = 
    		InMemoryJavac.compileLoadClasses(
    			classLoader, 
    			javaCode, 
    			compilerOptions );
    	return compiled;        
    }
    
    
    public static Class<?> loadJavaClass( 
    	InMemoryJavaClassLoader classLoader, 
    	InMemoryJavaSource javaCode,
    	JavacOptions.CompilerOption...compilerOptions )
    {
    	List<InMemoryJavaSource> codeList = new ArrayList<InMemoryJavaSource>();
        codeList.add( javaCode );
        Map<String, Class<?>> codeMap = 
        	InMemoryJavac.compileLoadClasses( 
        		classLoader, 
        		codeList, 
        		compilerOptions );
        
        LOG.debug( "Loaded Class \"" + javaCode.className + "\"" );
        return codeMap.get( javaCode.getClassName() );       
	}
    
    /**
     * For the record, I'm not too fond of having any "factories"
     * in the code, but (unfortunately) as it turns out I can't know
     * (apriori) what the "tailored" class name is BEFORE tailoring,
     * and I need a mutable abstraction to follow through the lifecycle
     * from the time the code is generated to the time it is exported 
     * or compiled.
     */
    public static class JavaCodeFactory
    {   
    	public static final JavaCodeFactory INSTANCE = new JavaCodeFactory();
    	
    	public static InMemoryJavaSource doCreate( TailorState tailorState )
    	{
    		return doCreate(tailorState.getDom(), tailorState.getContext(), tailorState.getTextBuffer() ); 
    	}
    	
    	public static InMemoryJavaSource doCreate( Dom dom, VarContext context, TextBuffer buffer )
    	{
    		String tailoredSource = buffer.toString();
            String theClassName = 
            	JavaNaming.ClassName.extractFromSource( tailoredSource );
            String packageName = 
            	JavaNaming.PackageName.extractFromSource( tailoredSource );
            
            if( theClassName == null )
            {
                theClassName = (String)context.get( "className" );
                if( theClassName == null )
                {
                    throw new VarException( 
                        "unable to find var className within source or VarContext" );
                }             
            }
            if( packageName == null )
            {
                packageName = (String)context.get( "packageName" );                         
            }
            if( packageName != null )
            {
                InMemoryJavaSource tailoredJavaSource =
                    new InMemoryJavaSource( packageName, theClassName, tailoredSource );
                
                LOG.debug( "Tailored \"" + packageName + "." + theClassName + ".java\"" );
                return tailoredJavaSource;
            }
            LOG.debug( "Tailored: \"" + theClassName + "\"" );
            return new InMemoryJavaSource( theClassName, tailoredSource );            
    	}
    }
    
    /**
     * Tailor the Source Code, 
     * Compile the Tailored Source Code into a Class
     * Load the Class into a ClassLoader
     * return  
     * @param context
     * @param codeExporter
     * @return
     */
    public static Class<?> loadJavaClass( 
        Dom markup, 
        VarContext context, 
        InMemoryJavaClassLoader memClassLoader,
        JavacOptions.CompilerOption...compilerOptions )
    {
        InMemoryJavaSource tailoredCode = javaCode( markup, context );
        
        List<InMemoryJavaSource> codeList = new ArrayList<InMemoryJavaSource>();
        
        Map<String, Class<?>> codeMap = 
        	InMemoryJavac.compileLoadClasses( memClassLoader, codeList, compilerOptions );
        return codeMap.get( tailoredCode.getClassName() ); 
    }

    private static final Map<Class<?>, Set<Class<?>>> SOURCE_CLASS_TO_TARGET_CLASSES= 
        new HashMap<Class<?>, Set<Class<?>>>();
    
    static
    {
        Set<Class<?>>byteMapping = new HashSet<Class<?>>();
        byteMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   byte.class, Byte.class, short.class, Short.class, int.class, 
                    Integer.class, long.class, Long.class} ) );
        
        SOURCE_CLASS_TO_TARGET_CLASSES.put( byte.class, byteMapping );
        
        Set<Class<?>>shortMapping = new HashSet<Class<?>>();
        shortMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   short.class, Short.class, int.class, 
                    Integer.class, long.class, Long.class } ) );
        
        SOURCE_CLASS_TO_TARGET_CLASSES.put( short.class, shortMapping );
        
        Set<Class<?>>intMapping = new HashSet<Class<?>>();
        intMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   int.class, Integer.class, long.class, Long.class } ) );
        
        SOURCE_CLASS_TO_TARGET_CLASSES.put( int.class, intMapping );
        
        Set<Class<?>>longMapping = new HashSet<Class<?>>();
        longMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   long.class, Long.class } ) );
        
        SOURCE_CLASS_TO_TARGET_CLASSES.put( long.class, longMapping );
    }
    
    protected static boolean translatesTo( Object source, Class<?>target )
    {
        Set<Class<?>> clazzes = SOURCE_CLASS_TO_TARGET_CLASSES.get( source.getClass() );
        if( target != null )
        {
            return clazzes.contains( target );
        }
        return false;
    }
    
    protected static boolean isParamAssignable( Class<?> target, Object source )
    {
        return source == null || source.getClass().isInstance( target ) 
              || ( source.getClass().isPrimitive() && ( translatesTo( source, target ) ) );
    }

    protected static boolean allParamsAssignable( Class<?>[] target, Object... source )
    {
        if( target == null && source == null || target.length == 0 && source.length == 0 )
        {
            return true;
        }
        if( target.length == source.length )
        {   //they have the same number of arguments, but are they type compatible?
            for( int pt = 0; pt < target.length; pt++ )
            {
                if( !isParamAssignable( source[ pt ].getClass(), target[ pt ] ) )
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static Object construct( Constructor<?> constructor, Object[] arguments )
    {
    	 try
         {
    		 if( arguments.length > 0 )
    		 {
    			 LOG.debug( "Calling constructor >" 
    			     + constructor + " with [" + arguments.length + "] arguments" );
    		 }
    		 else
    		 {
    			 LOG.debug( "Calling constructor > " + constructor );
    		 }
             return constructor.newInstance( arguments );
         }
         catch( InvocationTargetException e )
         {
             throw new VarException( e );
         }
         catch( InstantiationException ie )
         {
             throw new VarException( ie );
         }
         catch( IllegalAccessException iae )
         {
             throw new VarException( iae );
         }
         catch( IllegalArgumentException ite )
         {
             throw new VarException( ite );
         }
    }
    
    /** 
     * <UL>
     * <LI>creates an instance of the tailored class constructor 
     * (given the constructor params)
     * <LI>returns an instance of the Tailored Class.
     * </UL>
     * 
     * @param constructorParams params passed into the constructor
     * @return an Object instance of the tailored class
     */
    public static Object instance( 
        Class<?> theClass, Object... constructorParams )
    {
        Constructor<?>[] constructors = theClass.getConstructors();
        List<Constructor<?>> sameArgCount = new ArrayList<Constructor<?>>();
        
        if( constructors.length == 1 )
        {
        	return construct( constructors[ 0 ], constructorParams );
        }
        for( int i = 0; i < constructors.length; i++ )
        {
        	if( constructors[ i ].getParameters().length == constructorParams.length )
        	{
        		sameArgCount.add( constructors[ i ] );
        	}
        }
        if( sameArgCount.size() == 1 )
        {
        	return construct( sameArgCount.get( 0 ), constructorParams );
        }
        for( int i = 0; i < constructors.length; i++ )
        {
            Class<?>[] paramTypes = constructors[ i ].getParameterTypes();
            if( allParamsAssignable( paramTypes, constructorParams ) )
            {
            	return construct( constructors[ i ], constructorParams );
            }
        }
        throw new VarException( "Could not find a matching constructor for input" );
    }

    public static Object invokeStaticMethod( 
        Class<?> clazz, String methodName, Object... params )
    {
        try
        {
            Method method = getStaticMethod( 
                clazz.getMethods(), methodName, params );
            if( method == null )
            {
                throw new VarException(
                    "Could not find method \"" + methodName + "\" on \"" 
                   + clazz.getName() + "\"" );
            }
            return method.invoke( null, params );            
        }
        catch( IllegalAccessException iae )
        {
            throw new VarException( 
                "Could not call \"" + clazz.getName() + "." + methodName + "();", iae );
        }
        catch( IllegalArgumentException iae )
        {
            throw new VarException( 
                "Could not call \"" + clazz.getName() + "." + methodName + "();", iae );
        }
        catch( InvocationTargetException ite )
        {        	
        	throw new VarException( 
        		ite.getTargetException().getMessage() + " calling \"" + clazz.getName() + "." + methodName + "();", ite.getTargetException() );            
        }
    }

    /**
     * Reflectively call {@code methodName} on {@code target} with {@code params} 
     * @param target the target instance to call the method on
     * @param methodName the name of the method
     * @param params the parameters to pass to teh method
     * @return the result of the method
     */
    public static Object invokeTargetMethod( Object target, String methodName, Object... params )
    {
        try
        {
            Method method = getMethod( target.getClass().getMethods(), methodName, params );
            if( method == null )
            {
                throw new VarException(
                    "Could not find method \"" + methodName + "\" on \"" + target + "\"" );
            }
            return method.invoke( target, params );
        }
        catch( IllegalAccessException iae )
        {
            throw new VarException( 
                "Could not call \"" + target + "." + methodName + "();", iae );
        }
        catch( IllegalArgumentException iae )
        {
            throw new VarException( 
                "Could not call \"" + target + "." + methodName + "();", iae );
        }
        catch( InvocationTargetException ite )
        {
            throw new VarException( 
                "Could not call \"" + target + "." + methodName + "();", ite );
        }
    }

    public static Method getStaticMethod( Method[] methods, String methodName, Object[] params )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( ( methods[ i ].getModifiers() & Modifier.STATIC ) > 0
                && methods[ i ].getName().equals( methodName ) )
            {
                if( allParamsAssignable( methods[ i ].getParameterTypes(), params ) )
                {
                    return methods[ i ];
                }
            }
        }
        return null;
    }

    public static Method getMethod( Method[] methods, String methodName, Object[] params )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( methods[ i ].getName().equals( methodName ) )
            {
                if( allParamsAssignable( methods[ i ].getParameterTypes(), params ) )
                {
                    return methods[ i ];
                }
            }
        }
        return null;
    }

    public static Object doInvokeMethod( Object target, String methodName, Object... params )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Method method = getMethod( target.getClass().getMethods(), methodName, params );

        return method.invoke( target, params );
    }

}
