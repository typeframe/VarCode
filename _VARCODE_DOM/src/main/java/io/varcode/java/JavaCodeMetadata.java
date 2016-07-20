package io.varcode.java;

import java.util.List;
import java.util.Map;

import io.varcode.Metadata;

/**
 * Markup Metadata specific to Java Code.
 * 
 * Captures salient information with respect to the the Java Code 
 * <UL>
 *  <LI>which version of Java is the code written in? (JDK1.8?)
 *  <LI>are there any library dependencies required? (junit?)
 *  <LI>
 * </UL>   
 *  
 * @author M. Eric DeFazio
 */
public class JavaCodeMetadata
    implements Metadata
{
    /** The JDK version 1.1, 1.2...1.9*/
    public String jdkMinVersion;
    
    public String targetPlatform;
    
    public List<String> compilerFlags;
    
    public List<Dependency>compileDependency;
    
    public final long createTimestamp;
    
    public Map<String,Object> properties;
    
    public static class Dependency
    {
        public String name;
        public String version;
        public String type;        
    }
    
    public JavaCodeMetadata()
    {
        this.createTimestamp = System.currentTimeMillis();
    }
    
    @Override
    public Object get( String key )
    {
        return properties.get( key );
    }

    @Override
    public void put( String key, Object value )
    {
        properties.put( key, value );        
    }

    @Override
    public void addAll( Map<String, Object> properties )
    {
        this.properties.putAll( properties );
    }

	@Override
	public String[] getKeys() 
	{
		return properties.keySet().toArray( new String[ 0 ] );
	}

	@Override
	public void merge( Metadata metadata ) 
	{
		if( metadata != null )
		{
			String[] keys = metadata.getKeys();
			for( int i = 0; i < keys.length; i++ )
			{
				this.put( keys[ i ], metadata.get( keys[ i ] ) );
			}
		}
	}
}
