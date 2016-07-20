package io.varcode.context.lib.math;

import java.lang.reflect.Array;
import java.util.Collection;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

/**
 * Count of the number of Elements of a  bound variable 
 */
public enum Count
    implements VarScript
{
    INSTANCE;
    
    @Override
    public Object eval( VarContext context, String input )
    {
        return getCount( context, input );
    }
    
    public Integer getCount( VarContext context, String varName )
    {
      //the user passes in the NAME of the one I want index for
        //Object var = context.get( varName );
        Object var = context.resolveVar( varName );
        if( var != null )
        {
            if( var.getClass().isArray() )
            {
                return Array.getLength( var );
            }
            if( var instanceof Collection )
            {
                return ((Collection<?>)var).size();
            }
            return 1;
        }
        return null;
    }

	@Override
	public ScriptInputParser getInputParser() 
	{
		return VarScript.VAR_NAME_INPUT;
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}