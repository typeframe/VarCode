package io.varcode.java.javac;

import java.util.HashMap;
import java.util.Map;

import io.varcode.VarException;

/**
 * A ClassLoader that is used for "introducing" {@code InMemoryJavaClass}es
 * at runtime. 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class InMemoryJavaClassLoader
    extends ClassLoader
{
    private Map<String, InMemoryJavaClass> classNameToInMemoryClass = 
        new HashMap<String, InMemoryJavaClass>();

    public InMemoryJavaClassLoader()
    {
        this( ClassLoader.getSystemClassLoader() );
    }
    
    public InMemoryJavaClassLoader( ClassLoader parent ) 
    {
        super( parent );
    }

    /** Adds/Loads the inMemoryClass to the classLoader*/
    public void introduce( InMemoryJavaClass inMemoryClass ) 
    {
        classNameToInMemoryClass.put( inMemoryClass.getName(), inMemoryClass );
    }

    public Map<String, InMemoryJavaClass>getClassMap()
    {
    	return classNameToInMemoryClass;
    }
    
    @Override
    public Class<?> findClass( String name ) 
        throws VarException 
    {
    	try
    	{
    		InMemoryJavaClass inMemClass = classNameToInMemoryClass.get( name );
    		if( inMemClass == null ) 
    		{
    			return super.findClass( name );
    		}
    		byte[] byteCode = inMemClass.toByteArray();
    		return defineClass( name, byteCode, 0, byteCode.length );
    	}
    	catch( ClassNotFoundException e )
    	{
    		throw new VarException( 
    			"Couldn't find class \"" + name + "\"", e );
    	}
    }
}
