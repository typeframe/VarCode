package io.varcode.context.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

public enum FirstLower
    implements VarScript
{
    INSTANCE;

    /**
     * Given a String capitalize the first character and return
     * @param string the target string
     * @return 
     * <UL>
     * <LI>null if string is null 
     * <LI>"" if string is ""
     * <LI>"FirstCap" if the string is "firstCap"
     * </UL> 
     */
    private static final String lowercaseFirstChar( String string )
    {
        if( string == null )
        {
            return null;
        }
        if ( string.length() == 0 )
        {
            return "";
        }
        return string.substring( 0, 1 ).toLowerCase() + string.substring( 1 );      
    }
    
    
    public static Object doFirstLower( Object var )
    {
    	if( var == null )
    	{
    		return null;
    	}
        if( var instanceof String )
        {
            return lowercaseFirstChar( ( (String)var ) );
        }
        if( var.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( var );
            String[] firstLower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( var, i );
                if( idx != null )
                {
                    firstLower[ i ] = 
                        lowercaseFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstLower[ i ] = null;
                }
            }
            return firstLower;
        }
        if( var instanceof Collection )
        {
            Object[] arr = ( (Collection<?>)var ).toArray();
            int len = arr.length;
            String[] firstLower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    firstLower[ i ] = 
                        lowercaseFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstLower[ i ] = null;
                }
            }
            return firstLower;
        }
        return lowercaseFirstChar( var.toString() );        
    }

    @Override
    public Object eval( VarContext context, String input)
    {
        return doFirstLower(
        	this.getInputParser().parse( context, input ) );
    }
    
	@Override
	public ScriptInputParser getInputParser() 
	{
		return VarScript.SMART_INPUT;
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}