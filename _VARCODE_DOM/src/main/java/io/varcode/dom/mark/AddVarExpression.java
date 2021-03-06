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

import java.util.HashSet;
import java.util.Set;

import io.varcode.context.EvalException;
import io.varcode.context.VarBindException;
import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.dom.MarkupException;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasVars;
import io.varcode.dom.mark.Mark.IsNamed;
import io.varcode.dom.mark.Mark.MayBeRequired;
import io.varcode.text.TextBuffer;

/**
 * Adds Code (bound to a given var name). 
 * Provides an Expression to verify any value bound to this value is valid
 * Optionally provide a default in case the var name resolves to null.
 * 
 */

//BindML Examples
// BindML {+bitCount:(( bitCount > 0 && bitCount <= 64 ))+}
//                     --------------------------------
//                                  expression 

//BindML {+bitCount:(( bitCount > 0 && bitCount <= 64 ))*+} REQUIRED
//BindML {+bitCount:(( bitCount > 0 && bitCount <= 64 ))|1+} DEFAULT

//CODEML examples
// CodeML /*{+bitCount:(( bitCount > 0 && bitCount <= 64 ))+}*/
//                       --------------------------------
//                                 expression
//CodeML /*{+bitCount:(( bitCount > 0 && bitCount <= 64 ))*+}*/ REQUIRED
//CodeML /*{+bitCount:(( bitCount > 0 && bitCount <= 64 ))|1+}*/ WITH DEFAULT
public class AddVarExpression
    extends Mark
	implements IsNamed, BlankFiller, HasVars, MayBeRequired 
{	
	private final String varName;
	
	private final String defaultValue;
	
	/** 
	 * An Expression that takes a candidate value
	 * to be assigned to the var and tests to see if it is valid
	 * 
	 * NOTE: this expression should Always complete and return true or false  
	 */
	private final String validationExpression;
	
	/** is a value for this varName required to be bound at "tailor-time" */
	private final boolean isRequired;
	
	
	private final Set<String> vars;
	
	/**
	 * 
	 * 
	 * @param text the mark text
	 * @param lineNumber the line number where this mark text occurs
	 * @param varName the name of the var
	 * @param isRequired is a value required to be bound for this varName
	 * @param validationExpression (OPTIONAL)an expression used to restrict the possible values that can be bound
	 * to this varName <BLOCKQUOTE>NOTE: <B>the expression can assume the value assigned to varName is NOT NULL</B> when executing)</BLOCKQUOTE>
	 * @param defaultValue (OPTIONAL) the value to use if there is no value bound to this var or the value bound
	 * to this var does not pass the validationExpression.
	 */
	public AddVarExpression( 
	    String text, 
	    int lineNumber,
	    String varName, 
	    boolean isRequired,
	    String validationExpression,
	    String defaultValue )
	{
	    super( text, lineNumber );
	    this.varName = varName;
	    
	    
	    this.defaultValue = defaultValue;
	    this.isRequired = isRequired;
	    
	    if( this.defaultValue != null && this.isRequired )
	    {
	    	throw new MarkupException(
	    		"AddVarExpression Mark :" + N + text + N + 
	    		"on line [" + lineNumber + "] cannot have a defaultValue AND be REQUIRED" );
	    }
	    this.validationExpression = validationExpression;
	    
	    this.vars = new HashSet<String>();
	    vars.add( varName );
	}


	public String getVarName()
	{
		return varName; 
	}
	
	public String toString()
	{
		return text;
	}

	public Object derive( VarContext context ) 
	{
		Object resolved = 
			context.resolveVar( varName );
		
		if ( resolved == null )
		{
		    if( isRequired )
            {
		    	throw new VarRequiredButNull( varName, text, lineNumber );                
            }
		    //we dont run the expression if null
		    return defaultValue;
		}
		if( this.validationExpression != null )
		{
			Boolean isValidVar = Boolean.FALSE;
			try
			{
				isValidVar = (Boolean)context.evaluate( validationExpression );
			}
			catch( Exception e )
			{
				if( defaultValue == null )
				{
					throw new EvalException( e );
				}				
			}
			if( !isValidVar )
			{
				if( defaultValue == null )
				{
					throw new VarBindException(
						"var \"" + varName + "\" with value \"" + resolved 
						+ "\" for mark : " + N + text + N + 
						" on line[" + lineNumber + "]" +
						" does not satisfy validation expression: " + N + "(( " + validationExpression+ " ))" );
				}
				return defaultValue;
			}			
		}
		return context.resolveVar( varName );
	}

	public void fill( VarContext context, TextBuffer buffer )
	{
	    buffer.append( derive( context ) );
	}
	
	public boolean isRequired()
	{
	    return isRequired;
	}
	
    public String getDefault()
    {
        return this.defaultValue;
    }

    @Override
    public void onMarkParsed( ParseState parseState )
    { 
        parseState.reserveBlank();
    }

    @Override
    public Set<String> getAllVarNames( VarContext context )
    {
        return vars;
    }
}	