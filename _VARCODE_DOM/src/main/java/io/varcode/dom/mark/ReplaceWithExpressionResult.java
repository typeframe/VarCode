package io.varcode.dom.mark;

import io.varcode.context.EvalException;
import io.varcode.context.VarContext;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasExpression;
import io.varcode.dom.mark.Mark.WrapsContent;
import io.varcode.text.TextBuffer;

/**
 * Replaces "wrapped" data with that of the result of an expression
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
//  /*{+(( (a + b / 2) |0 ))*/ 37 /*}*/
public class ReplaceWithExpressionResult
    extends Mark
    implements BlankFiller, WrapsContent, HasExpression
{    
	/** the expression to be evaluated */
    private final String expression;
    
    /** Content wrapped between the open and close tags */
    private final String wrappedContent;
    
    public ReplaceWithExpressionResult( 
        String text, 
        int lineNumber, 
        String expression, 
        String wrappedContent )
    {
        super( text, lineNumber );
        this.expression = expression;
        this.wrappedContent = wrappedContent; 
    }
    
    public void fill( VarContext context, TextBuffer buffer )
    {
        buffer.append( derive(context ) );
    }
    
    public Object derive( VarContext context )
    {
        try
        {
        	return context.getExpressionEvaluator().evaluate( 
        		context.getScopeBindings(), expression );
        }
        catch( Throwable t )
        {
        	throw new EvalException( 
        		"Error evaluating mark: " + N + text + N 
               + "with expression \"" + expression 
               + "\" on line [" + lineNumber + "] with content :" + N 
                  + wrappedContent + N, t );
        }        
    }
    
    public String getWrappedContent()
    {
        return this.wrappedContent;
    }
    
    @Override
    public void onMarkParsed( ParseState parseState )
    {
    	parseState.reserveBlank();
    }

    @Override
    public String getExpression()
    {
        return expression;
    }    
}   
