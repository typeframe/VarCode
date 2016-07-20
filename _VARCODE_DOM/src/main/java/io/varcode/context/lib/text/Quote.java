package io.varcode.context.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

public enum Quote
    implements VarScript
    {
    	INSTANCE;

    public static Object doDoubleQuote( Object target )
    {
    	if ( target == null )
    	{
    		return null;
    	}
        if( target instanceof String )
        {
            //return "\"" + (String)target + "\"";
            return "\"" + EscapeString.escapeJavaString( (String)target ) + "\"";
        }
        if( target.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( target );
            String[] quoted = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( target, i );
                if( idx != null )
                {
                    quoted[ i ] = "\"" + EscapeString.escapeJavaString( idx.toString() ) + "\"";
                }
                else
                { //watch out for NPEs!
                    quoted[ i ] = null;
                }
            }
            return quoted;
        }
        if( target instanceof Collection )
        {
            Object[] arr = ( (Collection<?>)target ).toArray();
            int len = arr.length;
            String[] quoted = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    quoted[ i ] =
                        "\"" + EscapeString.escapeJavaString( idx.toString() ) + "\"";
                }
                else
                { //watch out for NPEs!
                    quoted[ i ] = null;
                }
            }
            return quoted;
        }
        return "\"" + EscapeString.escapeJavaString( target.toString() ) + "\"" ;        
    }

    @Override
    public Object eval( VarContext context, String input )
    {
        return doDoubleQuote( 
        	this.getInputParser().parse( context, input) );
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