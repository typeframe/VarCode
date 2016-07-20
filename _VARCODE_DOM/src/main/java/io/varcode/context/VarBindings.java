package io.varcode.context;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.script.Bindings;

/*{-?(removeLog==true):*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*-}*/

import io.varcode.VarException;
import io.varcode.tailor.Directive;

/**
 * Simple implementation of JSR-223 {@code Bindings} 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarBindings
    implements Bindings
{   
	/*{-?(removeLog==true):*/
	private static final Logger LOG = 
        LoggerFactory.getLogger( VarBindings.class );
	/*-}*/
	
    private final TreeMap<String, Object> kvMap;
 
    public VarBindings( )
    {
        this( new TreeMap<String, Object>() );
    }
    
    private VarBindings( Map<String, Object> keyValuePairMap )
    {
        this.kvMap = new TreeMap<String, Object>();
        this.kvMap.putAll( keyValuePairMap );
    }
    
    public boolean containsKey( Object key )
    {
        return kvMap.containsKey( key );         
    }
    
    public Object get( Object key )
    {
        return kvMap.get( key );       
    }
    
    public int size()
    {
        return kvMap.size();
    }

    public boolean isEmpty()
    {
        return kvMap.isEmpty();
    }

    public boolean containsValue( Object value )
    {
        return kvMap.containsValue( value );
    }

    public void clear()
    {
        kvMap.clear();
    }

    public Set<String> keySet()
    {
        return kvMap.keySet();
    }

    public Collection<Object> values()
    {
        return kvMap.values();
    }

    public Set<java.util.Map.Entry<String, Object>> entrySet()
    {
        return kvMap.entrySet();
    }

    /** 
     * Convenience Method
     * 
     * Adds all the properties on the target Bean  
     * (as name/value pairs)
     * 
     * it calls ALL getter methods (accept getClass()) 
     * and populates the values based on the name
     * 
     * @param bean a bean containing getter methods
     */
    public VarBindings setBeanProperties( Object bean )
    {
        if( bean == null )
        {
            return this;
        }
        try 
        {
            BeanInfo bi = Introspector.getBeanInfo( bean.getClass() );
            PropertyDescriptor[] pds = bi.getPropertyDescriptors();
            for( int i = 0; i < pds.length; i++ ) 
            {
                // Get property name
                String propName = pds[ i ].getName();
                
                Method m = pds[ i ].getReadMethod();                
                Object value = m.invoke( bean );
                Object replaced = put( propName, value );
                /*{-?(removeLog==true):*/
                if( replaced != null )
                {
                	if( LOG.isDebugEnabled() )
                	{	
                		LOG.debug( "overwriting \"" + propName +"\" with value \""
                			+ replaced + "\" with bean property \""+ value + "\"" );
                	}
                }
                /*-}*/                
            }
            return this;
        } 
        catch( java.beans.IntrospectionException e ) 
        {
            throw new VarException(
                "Unable to populate bean properties for " 
               + bean + " into context ", e );
        }
        catch( Exception e )
        {
            throw new VarException(
                "Unable to populate bean properties for " 
               + bean + " into context ", e );
        }
    }
    
    /**
     * Sets all of the Properties within the Map
     * @param mapProperties
     */
    public void setMap( Map<String,Object> mapProperties )
    {
        String[] keys = mapProperties.keySet().toArray( new String[ 0 ] );
        
        for( int i = 0; i < keys.length; i++ )
        {
        	 
            Object replaced = 
            	put( keys[ i ],  mapProperties.get( keys[ i ] ) );
            /*{-?(removeLog==true):*/
            if( replaced != null )
            {
            	if( LOG.isDebugEnabled() )
            	{
            		LOG.debug( "overwriting \"" + keys[ i ] +"\" with value \""
            			+ replaced + "\" with Map property \"" 
            			+ mapProperties.get( keys[ i ] ) + "\"" );
            	}
            }
            /*-}*/
        }        
    }
    
    /** 
     * converts a list of Beans into parallel arrays
     * of properties
     * NOTE:
     * ALL Beans MUST BE OF THE SAME CLASS/TYPE
     * 
     * This is converting from: 
     * Array of Structs 
     *      -to- 
     * Structs of Arrays
     * 
     * @param beans the beans (ALL MUST BE OF THE SAME TYPE)
     */
    public void setAllBeanProperties( Object...beans )
    {
        if( beans.length == 0 )
        {
            return;
        }
        try
        {
            BeanInfo bi = Introspector.getBeanInfo( 
                beans[ 0 ].getClass() );
        
            PropertyDescriptor[] pds = bi.getPropertyDescriptors();
        
            Object[][] soa = new Object[ pds.length ][ beans.length ];
        
            String[] propertyNames = new String[ pds.length ];
            for( int i = 0; i < pds.length; i++ ) 
            {
                // Get property name
                propertyNames[ i ] = pds[ i ].getName();
                //  get the method
                Method m = pds[ i ].getReadMethod();
                for( int j = 0; j < beans.length; j++ )
                {
                    Object value = m.invoke( beans[ j ] );
                    soa[ i ][ j ] = value;
                }            
            }
            for( int i = 0; i < propertyNames.length; i++ )
            {
                Object replaced = put( propertyNames[ i ], soa[ i ] );
                /*{-?(removeLog==true):*/
                if( replaced != null && LOG.isDebugEnabled() )
                {
                	LOG.debug( "overwriting \"" + propertyNames[ i ] +"\" with value \""
                	    + replaced + "\" with Map property \"" 
                		+ soa[ i ] + "\"" );
                }
                /*-}*/
            }
        }
        catch( java.beans.IntrospectionException e )
        {
            throw new VarException(
                "Unable to populate bean properties for " 
                 + beans + " into context ", e );
        }
        catch( Exception e )
        {
            throw new VarException(
                "Unable to populate bean properties for " 
                + beans + " into context ", e );
        }        
    }
    
    public Object putScript( String name, VarScript script )
    {
        if( name == null )
        {
            throw new VarException( "VarBinding name cannot be null" ); 
        }
        if( name.startsWith( "$" ) )
        {
        	return kvMap.put( name, script );
        }
        return kvMap.put( "$" + name, script );       
    }
    
    public Object putDirective( String name, Directive directive ) 
	{
    	if( name == null )
        {
            throw new VarException( "VarBinding name cannot be null" ); 
        }
        if( name.startsWith( "$" ) )
        {
        	return kvMap.put( name, directive );
        }
        return kvMap.put( "$" + name, directive );       
	}
    
    public Object put( String name, Object value )
    {
    	if( value instanceof VarScript || value instanceof Directive )
    	{
    		if( name.startsWith( "$" ) )
    		{
    			return kvMap.put( name, value );
    		}
    		return kvMap.put( "$" + name, value );
    	}
        return kvMap.put( name, value );
    }

    public void putAll( Map<? extends String, ? extends Object> toMerge )
    {
    	Iterator<?> it = toMerge.keySet().iterator();
    	while( it.hasNext() )
    	{
    		String name = (String)it.next();
    		Object value = toMerge.get( name );
    		put( name, value );
    	}
    }

    public Object remove( Object key )
    {
        return kvMap.remove( key );
    }
    
    public String toString()
    {
        return kvMap.toString();
    }
}
