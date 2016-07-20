package io.varcode.context.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.context.VarScript.ScriptInputParser;

public enum FirstCap
    implements VarScript, ScriptInputParser
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
    public static final String capitalizeFirstChar( String string )
    {
        if( string == null )
        {
            return null;
        }
        if ( string.length() == 0 )
        {
            return "";
        }
        return string.substring( 0, 1 ).toUpperCase() + string.substring( 1 );      
    }
    
    public static Object doFirstCaps( Object var )
    {
        if( var == null )
        {
            return null;
        }
        if( var instanceof String )
        {
        	return capitalizeFirstChar( ( (String)var ) );
        }
        if( var.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( var );
            String[] firstCaps = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( var, i );
                if( idx != null )
                {
                    firstCaps[ i ] = 
                        capitalizeFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstCaps[ i ] = null;
                }
            }
            return firstCaps;
        }
        if( var instanceof Collection )
        {
        	//List<String> l = (List<?>)var;
        	
            Object[] arr = ( (Collection<?>)var ).toArray();
            int len = arr.length;
            String[] firstCaps = new String[ len ];
            
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                	//l.set(i, capitalizeFirstChar( idx.toString() ) );
                    
                	firstCaps[ i ] = 
                        capitalizeFirstChar( idx.toString() );
                    
                }
                else
                { //watch out for NPEs!
                    firstCaps[ i ] = null;
                }
            }
            return firstCaps;
        }
        return capitalizeFirstChar( var.toString() );        
    }

    @Override
    public Object eval( VarContext context, String input)
    {
    	Object resolved = 
    		this.getInputParser().parse( context, input );
        return doFirstCaps( resolved );
    }
    
	@Override
	public ScriptInputParser getInputParser() 
	{
		return this; //VarScript.VAR_NAME_INPUT;
	}

	@Override
	public Object parse( VarContext context, String scriptInput ) 
	{
		if( scriptInput != null && 
			scriptInput.startsWith( "$" ) &&    				
			scriptInput.endsWith( ")" ) &&
			scriptInput.indexOf( '(' ) > 0 )
		{
				//I first need to 
		        // {#id:$quote($uuid())#}
		        //             $uuid()
				
				//               (
			int openIndex = scriptInput.indexOf( '(' );
			String scriptName = scriptInput.substring( 1, openIndex ); 
			VarScript innerScript = context.getVarScript( scriptName );
			String innerScriptInput = 
				scriptInput.substring( 
					openIndex + 1,
					scriptInput.length() -1 );
			Object innerScriptResult = 
				innerScript.eval( context, innerScriptInput );
			return innerScriptResult;
		}
		return context.get( scriptInput );
	}

	@Override
	public Set<String> getAllVarNames( String input ) 
	{
		if( input != null )
		{
			Set<String> s = new HashSet<String>();
			s.add( input.replace( "$(","").replace( ")", "" ) );
			return s;
		}
		return Collections.emptySet();
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}