package io.varcode.dom.mark;

import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.context.EvalException;
import io.varcode.dom.DomParseState;
import io.varcode.dom.MarkupException;
import io.varcode.dom.mark.Mark.Derived;
import io.varcode.dom.mark.Mark.HasExpression;
import io.varcode.dom.mark.Mark.IsNamed;

//STATIC DEFINE Marks 
//  IMMUTABLE at "runtime"
//  derived by the Parser/Compiler
/*{##c:(( Math.sqrt( a * a + b * b ) ))##}*/

// --------------------------------------
//INSTANCE DEFINE Marks 
//   they are MUTABLE and 
//   derived by the "Tailor"-time/ Runtime 

/*{#c:(( Math.sqrt(a * a + b * b ) ))#}*/
//---------------------------------


/**
 * Define a "local" var for use within the {@code Context} 
 * by evaluating a function and returning a value
 * 
 * <B>Eval</B>uate a script and <B>derive</B> a named local variable in the 
 * {@code MutableContext}.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class DefineVarAsExpressionResult
    extends Mark
    implements IsNamed, HasExpression, Derived
{
    /** the REQUIRED name of the var to be defined */
    public final String varName;
    
    public final String expression;
    
    
    public DefineVarAsExpressionResult( 
        String text, 
        int lineNumber,
        String varName, 
        String expression )
    {
        super( text, lineNumber );
        this.varName = varName;
        this.expression = expression;
    }
    
    @Override
    public String getVarName()
    {
        return varName;
    }
    
   
    public String getExpression()
    {
    	return this.expression;
    }
	
    private static boolean isNonEmptyString( Object obj )
    {
    	if( obj instanceof String )
    	{
    		if( ((String)obj).length() == 0 )
    		{
    			return false;
    		}
    		return true;
    	}
    	return true;
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
                "Exception evaluating expression \"" + expression + "\" for var \"" 
              + varName  + "\" of mark:" 
              + N + text + N  + "on line[" + lineNumber + "]", t );
        }       
    }
    
    public static final class InstanceVar
        extends DefineVarAsExpressionResult
        implements Bind, BoundDynamically
    {
    	
        public InstanceVar( 
            String text, 
            int lineNumber, 
            String varName, 
            String expression )
        {
            super( text, lineNumber, varName, expression );
        }        
        
        //Bind the derived value to the context (as an instance)
        //provided it is non-null or the empty string
        public void bind( VarContext context )
        {
            Object derived = derive( context );
           
            if( derived != null && isNonEmptyString( derived ) )
            {
            	context.set( varName, derived, VarScope.INSTANCE );
            }
        }
                
        public String toString()
        {            
            return "Dynamic Var " + varName + " : ((" 
                + expression + "))"; 
        }
    }

    public static final class StaticVar
        extends DefineVarAsExpressionResult
        implements BoundStatically 
    {
        public StaticVar( 
            String text, 
            int lineNumber, 
            String varName,
            String expression )
        {
            super( text, lineNumber, varName, expression );
        }
        
        @Override
        public void onMarkParsed( DomParseState parseState )
        {
            //Setting things Statically can only rely on OTHER statically defined 
            // variables and scripts that EXIST in the MarkupState...
            
            // (we cant use ANY instance variables because we are currently 
            // PARSING/COMPILING the code (and those instance variables dont exist
            // until later (when we tailor)
            Object derived = null;
            
            try
            {
                derived = derive( parseState.getParseContext() );
                if( derived != null && isNonEmptyString( derived ) )
                {
                	parseState.setStaticVar( varName, derived );
                }                                  
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Unable to derive static var \"" + varName 
                  + "\" with expression \"" + expression + "\"" 
                  + " for STATIC scope with mark: " + N + text + N + 
                  "on line [" + lineNumber + "]", t );
            }                                
        }
        
        public String toString()
        {            
            return "STATIC Var " + varName + " : ((" 
                + expression + "))"; 
        }
    }
}
