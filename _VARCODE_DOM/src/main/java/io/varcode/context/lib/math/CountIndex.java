package io.varcode.context.lib.math;

import java.lang.reflect.Array;
import java.util.Collection;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

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
public enum CountIndex
    implements VarScript
{
	INSTANCE;

    @Override
    public Object eval( 
        VarContext context, String input )
    {
        return getCountIndex( context, input );
    }

    public Object getCountIndex( VarContext context, String varName )
    {
      //the user passes in the NAME of the one I want index for
        //Object var = context.get( sourceCode );
        Object var = context.resolveVar( varName );
        if( var != null )
        {
            if( var.getClass().isArray() )
            {
                int len = Array.getLength( var );
                int[] countIndex = new int[ len];
                for(int i=0; i<len; i++)
                {
                    countIndex[ i ] = i;
                }
                return countIndex;
            }
            if( var instanceof Collection )
            {
                Collection<?> coll = (Collection<?>) var;
                int len = coll.size();
                int[] countIndex = new int[ len];
                for( int i = 0; i < len; i++ )
                {
                    countIndex[ i ] = i;
                }
                return countIndex;
            }
            return new int[]{ 0 };
        }
        return null;
    }

	@Override
	public ScriptInputParser getInputParser() 
	{
		return ScriptInputParser.InputVarName.INSTANCE;
	}    
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
	
}