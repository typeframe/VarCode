package io.varcode.java;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.dom.Dom;
import io.varcode.dom.MarkupException;
import io.varcode.dom.codeml.CodeML;
import io.varcode.java.javac.InMemoryJavaClassLoader;
import io.varcode.java.javac.InMemoryJavaSource;
import io.varcode.markup.MarkupRepo;
import io.varcode.markup.MarkupRepo.MarkupStream;
import io.varcode.tailor.Directive;

/**
 * The combination of {@code Dom} and {@code VarContext} to 
 * represent specialized Java Source code.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class JavaCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( JavaCase.class );
    
    /** 
     * The Tailored Java Code 
     * manufactured from ({@code Dom} + {@code VarContext}) 
     */
    private final InMemoryJavaSource tailoredJavaCode;
    
    /**
     * Using the current environment, parse the Dom and return it
     * 
     * NOTE: the MarkupClazz MUST BE a TOP LEVEL class (not a styatic inner class)
     * 
     * @param markupClazz the class marked up with CodeML marks
     * @return the dom
     */
    public static final Dom dom( Class<?> markupClazz )
    	throws MarkupException
    {
         return dom( JavaMarkupPath.INSTANCE, markupClazz );
    }
    
    public static final Dom dom( MarkupRepo markupRepo, Class<?> markupClazz )
    { 
    	MarkupStream markupStream = markupRepo.markupStream( 
            markupClazz.getCanonicalName() + ".java" );
                
        Dom dom = CodeML.compile( markupStream );
        LOG.debug( "Compiled Dom from \""+ markupClazz +"\"" );
        return dom;
    }
    
    /**
     * Using the Class, lookup the source for the class (assuming it is in the
     * Standard {@code JavaSrcPath}
     * @param markupClazz the clazz (whos source is VarSource)
     * @param keyValuePairs key-value pairs of vars and scripts 
     * @return the Case (representing the Markup and VarContext) 
     */
    public static final JavaCase of( Class<?> markupClazz, Object... keyValuePairs )
    {
        return of( JavaMarkupPath.INSTANCE, markupClazz, keyValuePairs );
    }
    
    public static final JavaCase of( Class<?> markupClazz, VarContext context )
    {
        return of( JavaMarkupPath.INSTANCE, markupClazz, context );
    }

    
    /**
     * Using the Class, lookup the source for the class (assuming it is in the
     * Standard {@code JavaSrcPath}
     * @param clazz the clazz (whos source is VarSource)
     * @param keyValuePairs key-value pairs of vars and scripts 
     * @return the Case (representing the Markup and VarContext) 
     */
    public static final JavaCase of( Dom dom, Object... keyValuePairs )
    {
        return new JavaCase( dom, keyValuePairs );
    }
    
    public static final JavaCase of( Dom dom, VarContext varContext )
    {
        return new JavaCase( dom, varContext );
    }
    
    /**
     * 
     * @param markupRepo the repository location of markup (java source code) 
     * @param fullyQualifiedClassName (i.e. "java.lang.String.java")
     * @param keyValuePairs key/Values to comprise the {@code VarContext}
     * @return
     */
    public static final JavaCase of( 
    	MarkupRepo markupRepo, String fullyQualifiedClassName, Object...keyValuePairs )
    {
    	 MarkupStream markupStream = markupRepo.markupStream( fullyQualifiedClassName );
    	 
    	 Dom markup = CodeML.compile( markupStream ); 
    	       
    	 return new JavaCase( markup, keyValuePairs );
    }
    
    /**
     * Using the markupRepo resolve the markup source for {@code clazz}, 
     * and create a new Case based on a {@code VarContext} containing {@code pairs} 
     * (pairs is data passed in as alternating key,value, key, value)
     *  
     * @param markupRepo where to obtain the source for the class
     * @param markupClass the class
     * @param keyValuePairs specialization values for tailoring
     * @return the JavaCase
     */
    public static final JavaCase of( 
        MarkupRepo markupRepo, Class<?> markupClass, Object... keyValuePairs )
    	throws MarkupException
    {
    	Dom dom = dom( markupRepo, markupClass );
    	return new JavaCase( dom, keyValuePairs );
    }
    
    public static Directive[] getInnerClassTailorDirectives( Class<?> markupClass )    
    {
        //Check if this declares any Local TailorDirectives 
        Class<?>[] declaredClasses = markupClass.getDeclaredClasses();
        List<Directive> directives = new ArrayList<Directive>();
        for( int i = 0; i < declaredClasses.length; i++ )
        {
        	//System.out.println( declaredClasses[ i ] );
        	if( Directive.class.isAssignableFrom( declaredClasses[ i ] ) )
        	{
        		try 
        		{
					directives.add( (Directive)declaredClasses[ i ].newInstance() );
				} 
        		catch( InstantiationException e ) 
        		{
					LOG.error( "Unable to load Tailor Directive " + declaredClasses[ i ], e );
				} 
        		catch( IllegalAccessException e ) 
        		{
					LOG.error( "Unable to load Tailor Directive " + declaredClasses[ i ], e );
				}
        	}
        }
        return directives.toArray( new Directive[ 0 ] );
    }
    /**
     * Using the sourcePath resolve the source for {@code clazz}, and create
     * a new Case based on a {@code VarContext} containing {@code pairs} 
     * (pairs is data passed in as alternating key,value, key, value)
     *  
     * @param sourcePath the path to use to resolve the source
     * @param markupClass the class
     * @param keyValuePairs
     * @return
     */
    public static final JavaCase of( 
        MarkupRepo markupRepo, Class<?> markupClass, VarContext varContext )
    {
        MarkupStream markupStream = markupRepo.markupStream( 
            markupClass.getCanonicalName() + ".java" );

        Dom dom = CodeML.compile( markupStream ); 
        
        Directive[] directives = 
        	getInnerClassTailorDirectives( markupClass ); 
        
        return new JavaCase( dom, varContext, directives );
    }

    public JavaCase( Dom dom, Object... pairs )
    {
        this( dom, VarContext.of( pairs ) );
    }
    
    public JavaCase( Dom dom, VarContext context, Directive...directives )
    {
        this.tailoredJavaCode = JavaTailor.javaCode( dom, context, directives );
    }
    

    public String toString()
    {
    	return this.tailoredJavaCode.getCode();
    }
    
    public InMemoryJavaSource javaCode()
    {
        return this.tailoredJavaCode;
    }
        
    public Class<?> loadClass( InMemoryJavaClassLoader classLoader )
    {
    	return JavaTailor.loadJavaClass( 
    		classLoader, this.tailoredJavaCode );
    }
    
    public Class<?> loadClass()
    {
        return JavaTailor.loadJavaClass( this.tailoredJavaCode );
    }
        
    /**
     * <UL>
     * <LI>Tailor the Java Code for this Case
     * <LI>Compile the "tailored" java code to a Tailored Java Class
     * <LI>Load the "tailored" class into a classLoader 
     * <LI>use reflection to find the appropriate constructor (given the parameters)
     * <LI>call the constructor to create a new instance and return it
     * </UL>
     * 
     * @param constructorArgs
     * @return
     */
    public Object instance( Object... constructorArgs )
    {
        Class<?> theClass = loadClass();
        return instanceOfClass( theClass, constructorArgs );
    }
        
    public static Object instanceOfClass( Class<?>clazz, Object...parameters )
    {
        Object instance = JavaTailor.instance( clazz, parameters );
        return instance;
    }
    
    /**
     * Invokes the instance method and returns the result
     * @param instance the target instance to invoke the method on
     * @param methodName the name of the method
     * @param params the parameters to pass to the method
     * @return the result of the call
     */
    public static Object invoke( 
    	Object instance, String methodName, Object... params )
    {
        return JavaTailor.invokeTargetMethod( instance, methodName, params );
    }

    /**
     * Invokes a static class method and returns the results
     * 
     * @param clazz the class to invoke method 
     * @param methodName the name of the method
     * @param params the params to the method
     * @return the result of the method
     */
    public static Object invokeStatic( 
    	Class<?> clazz, String methodName, Object... params )
    {
        return JavaTailor.invokeStaticMethod( clazz, methodName, params );
    }

	public static Object getStaticField( Class<?> clazz, String fieldName ) 
	{
		try 
		{
			Field f = clazz.getField( fieldName );
			return f.get( clazz );
		} 
		catch (IllegalArgumentException e) 
		{
			throw new VarException( e );
		} 
		catch (IllegalAccessException e) 
		{	
			throw new VarException( e );
		} 
		catch( NoSuchFieldException e ) 
		{
			throw new VarException( e );
		} 
		catch( SecurityException e ) 
		{
			throw new VarException( e );
		}
	}
}
