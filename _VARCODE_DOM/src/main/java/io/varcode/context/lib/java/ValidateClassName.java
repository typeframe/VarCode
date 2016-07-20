package io.varcode.context.lib.java;

import java.lang.reflect.Array;
import java.util.Collection;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.java.JavaNaming;

/**
 * Creates an index count (an array of sequential indexes) for all
 * elemnts in the array
 * 
 * For example:
 * <PRE> 
 * if I have the input String[]{ "A", "B", "C", "D", "E" };
 * it will return int[]{ 0, 1, 2, 3, 4 };
 * 
 * if I have the input String[]{"Yes" "No", "Maybe"};
 * it will return int[]{ 0, 1, 2 };
 * </PRE>
 */
public enum ValidateClassName
    implements VarScript
{
    INSTANCE;
        
    @Override
    public Object eval( VarContext context, String input )
    {
        return validate( context, input );
    }

    public Object validate( VarContext context, String varName )
    {
        Object var = context.resolveVar( varName );
        if( var != null )
        {
            if( var.getClass().isArray() )
            {
                int len = Array.getLength( var );                    
                for( int i = 0; i < len; i++ )
                {
                    Object o = Array.get( var, i );
                    if( o != null )
                    {
                        JavaNaming.ClassName.validateSimpleName( o.toString() );
                    }
                    else
                    {
                        throw new VarException( 
                            "null class name for \"" + varName + 
                            "\" at index [" + i + "]" );
                    }
                }
                return var;
            }
            if( var instanceof Collection )
            {
                Object[] coll = ((Collection<?>) var).toArray( new Object[ 0 ] );
                
                for( int i = 0; i < coll.length; i++ )
                {
                    Object o = coll[ i ];
                    if( o != null )
                    {
                        JavaNaming.ClassName.validateSimpleName( o.toString() );
                    }
                    else
                    {
                        throw new VarException( 
                            "null class name at index [" + i + "]" );
                    }
                }
                return var;
            }
            try
            {
            	JavaNaming.ClassName.validateSimpleName( var.toString() );
            }
            catch( Exception e )
            {
            	throw new VarException( e); 
            }
            return var;
        }
        throw new VarException( 
            "invalid, null class name for var \"" + varName + "\"" );
    }     
    
    @Override
	public ScriptInputParser getInputParser() 
	{
		return ScriptInputParser.InputVarName.INSTANCE;
	}     
}