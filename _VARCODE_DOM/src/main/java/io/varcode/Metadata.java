package io.varcode;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores key value pairs that represent metadata of a Document
 * (Code, Binaries, etc.)  
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Metadata
{	
    public Object get( String key );

    public void put( String key, Object value );

    public void addAll( Map<String, Object> properties );
    
    public String[] getKeys();

    public void merge( Metadata metadata );
    
    /** "Top Level" {@code PropertyLabel} for Markup metadata */
    public static final String MARKUP = "markup";
    
    /** "Top Level" {@code PropertyLabel} for {@code Dom} metadata*/
    public static final String DOM = "dom";
    
    /** "Top Level" {@code PropertyLabel} for "tailored Code" metadata */
    public static final String TAILOR = "tailor";
    
    /**
     * Creates and returns a Hierarchial Property Label
     * (which is technically just a formatted String)
     * 
     * Properties are often hierarchial and this will attempt
     * to provide some standard ways of creating 
     * @author eric
     *
     */
    public static class PropertyLabel
    {
    	public static String of( String...labels )
    	{
    		String theLabel = "";
    		for( int i = 0; i < labels.length; i++ )
    		{
    			if( i > 0 )
    			{
    				theLabel += ".";
    			}
    			theLabel += labels[ i ];
    		}
    		return theLabel;
    	}
    }
    public static class SimpleMetadata
        implements Metadata
    {
        private final Map<String, Object> properties;

        public SimpleMetadata()
        {
            this.properties = new HashMap<String, Object>();
        }

        @Override
        public Object get( String key )
        {
            return this.properties.get( key );
        }

        @Override
        public void put( String key, Object value )
        {
            this.properties.put( key, value );
        }

        @Override
        public void addAll( Map<String, Object> properties )
        {
            this.properties.putAll( properties );
        }
        
        public String toString()
        {
        	return properties.toString();
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
}