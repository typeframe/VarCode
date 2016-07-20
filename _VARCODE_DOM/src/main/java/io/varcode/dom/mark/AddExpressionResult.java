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
import io.varcode.context.VarContext;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasExpression;
import io.varcode.text.TextBuffer;

/**
 * Add the Result of the Expression to the Tailored Text
 */ 

// usual form, register a script "derive" to the environment
// example        : "/*{+(( Math.sqrt( Math.pow( a,2 ) + Math.pow(b ,2) ) ))+}*/"
//	   Prefix     : "/*{+(("
//     Postfix    : "))+}*/"
//     expression : "Math.sqrt( Math.pow( a,2 ) + Math.pow(b ,2 ) "

/*{+(( Math.sqrt( a * a + b * b ) ))+}*/

public class AddExpressionResult
	extends Mark
	implements BlankFiller, HasExpression
{	
	private final String expressionText;
	
	public AddExpressionResult(
        String text, 
        int lineNumber,
        String expressionText )
    {
        super( text, lineNumber );
        this.expressionText = expressionText;        
    }
	
	public String toString()
	{
		return text;
	}
	
	public Object derive( VarContext context ) 
	{	
        try
        {
        	Object res = context.getExpressionEvaluator().evaluate( 
        		context.getScopeBindings(), expressionText );
        	return res;
        }
        catch( Exception e )
        {
        	 throw new EvalException( 
                 "Error evaluating Expression \"" + expressionText 
               + "\" for mark " + N + text + N 
               + "on line [" + lineNumber + "]", e );
        }
	}
	
	public void fill( VarContext context, TextBuffer buffer )
	{
	    buffer.append( derive( context ) );
	}

    @Override
    public void onMarkParsed( ParseState parseState )
    {    
        parseState.reserveBlank();
    }

	@Override
	public String getExpression() 
	{
		return this.expressionText;
	}
}	