package io.varcode.dom.mark;

import io.varcode.VarException;
import io.varcode.context.EvalException;
import io.varcode.context.VarContext;
import io.varcode.dom.mark.Mark.Derived;
import io.varcode.dom.mark.Mark.HasExpression;

/* BindML */
// {(( expression ))}
// ---            ---
// OPEN TAG       CLOSE TAG

/* CodeML */
/*{(( expression ))}*/ 
/**
 * Evaluates an INSTANCE expression (usually for loading one or more JavaScript functions
 * that may be called later
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class EvalExpression
    extends Mark
    implements HasExpression, Derived
{
    /** the expression to evaluate */
    private final String expression;
    
    // (( Math.PI * r * r ))
    // /*{(( Math.PI * r * r ))*}*/ //REQUIRED
    public EvalExpression( 
    	String text, int lineNumber, String expression )
    {
        super( text, lineNumber );
        this.expression = expression;        
    }

    @Override
    public Object derive( VarContext context )
    	throws EvalException
    {
    	try
    	{
    		context.getExpressionEvaluator().evaluate( 
    			context.getScopeBindings(), expression );
    	}
    	catch( Throwable t )
    	{
    		if( t instanceof VarException )
    		{   //we already created an exception and or wrapper or re-throw (no wrapping)    			
    			throw (VarException)t;
    		}
    		//something unexpected happened, I need to wrap and throw 
    		throw new EvalException(
    			"Expression \"" + expression + "\" for mark :" + N + text + N
                +" on line [" + lineNumber + "] failed", t );
    	}
        return null;
    }

	@Override
	public String getExpression() 
	{
		return expression;
	}
}
