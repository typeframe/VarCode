package io.varcode.dom.mark;

import io.varcode.context.ExpressionEvaluator;
import io.varcode.context.VarContext;
import io.varcode.context.EvalException;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.text.TextBuffer;

/**
 * Wraps code with a condition:
 * <UL>
 *   <LI> If the condition evaluates to TRUE the code is CUT/REMOVED 
 *   from the tailored code.
 *   <LI> otherwise the code remains intact
 * </UL>
 *   
 * @author M. Eric DeFazio eric@varcode.io
 */
public class CutIfExpression
    extends Mark
    implements BlankFiller
{    
    protected final String expression;
    
    protected final String code; 
    
    public CutIfExpression( 
        String text, int lineNumber, String expression, String code )
    {
        super( text, lineNumber );
        this.expression = expression;
        this.code = code;
    }
    
    public String getExpression()
    {
        return expression;
    }
    
    public String getConditionalContent()
    {
        return code;
    }

    public void fillTo( VarContext context, StringBuilder out )
    {
        out.append( derive( context ) );
    }
    
    public void fill( VarContext context, TextBuffer buffer )
    {
        buffer.append( derive( context ) );
    }
    
    public Object derive( VarContext context )
    {
        ExpressionEvaluator ce = context.getExpressionEvaluator();
        try
        {
            Object result = ce.evaluate( context.getScopeBindings(), expression );
        
            if( result instanceof Boolean && ( (Boolean)result).booleanValue() )
            {
                return null;                      
            }
            return code;
        }
        catch( Exception e )
        {   
            throw new EvalException( 
                "Unable to evaluate CutIfExpression : " + N + text + N
               +"on line [" + lineNumber + "]", e );
        }        
    }
    
    @Override
    public void onMarkParsed( ParseState markupState )
    {   
        markupState.reserveBlank();
    }

}
