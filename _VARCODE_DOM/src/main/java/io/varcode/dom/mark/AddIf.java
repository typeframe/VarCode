/*
 * Copyright 2015 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.varcode.dom.mark;

import io.varcode.context.EvalException;
import io.varcode.context.ExpressionEvaluator;
import io.varcode.context.VarContext;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.text.TextBuffer;

/**
 * A Form of Code (one or more java statements) that is <I>conditionally</I> 
 * written to the tailored source.
 * 
 *  / *{+?( bitCount > 31 && value < 0 ):-1+}* /</PRE>  
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
 /*{+?(log==trace):LOG.trace( "In Method " );+}*/

public class AddIf
    extends Mark
    implements BlankFiller 
{
    /** the expression to be evaluated (if true) */
    public final String expression;
    
    /** code/text conditionally written to the tailored text*/ 
    public final String conditionalText;
    
    public AddIf( 
        String text, 
        int lineNumber, 
        String expression,
        String conditionalText )
    {
        super( text, lineNumber );
        this.expression = expression;
        this.conditionalText = conditionalText;     
    }
   
    public String getCondition()
    {
        return expression;
    }
    
    public String getCode()
    {
        return conditionalText;
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
                return conditionalText;                    	
            }
            return null;
        }
        catch( Exception e )
        {    
            throw new EvalException( 
                "Error evaluating expression ((" + expression + "))" 
               +" for Mark :" + N + text + N + "on line [" + lineNumber + "]", e );
        }        
    }
    
    @Override
    public void onMarkParsed( ParseState parseState )
    {
        parseState.reserveBlank();
    }
}
