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

import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.ResultRequiredButNull;
import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.context.VarScript;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasScript;
import io.varcode.dom.mark.Mark.MayBeRequired;
import io.varcode.text.TextBuffer;

/**
 * Mark to Add Code within the varcode (given a name) 
 * Optionally provide a default in case the name resolves to null.
 */ 

// usual form, register a script "derive" to the environment
// example      : "/*{+$derive(valueObject, a)}*/"
//	   Prefix   : "/*{+$"
//     Postfix  : "}*/"
//     Context  : default / java
//     Script   : derive
//     params   : {valueObject,"a"}

/*{+$intframe(age[0...130]("years old"),height[0...200]("inches"),weight[0...900]("lbs."))*/

public class AddScriptResult
	extends Mark
	implements BlankFiller, HasScript, MayBeRequired
{	
	private final String scriptName;
	
	private final String scriptInput;
	
	private final boolean isRequired;
	
	public AddScriptResult(
        String text, 
        int lineNumber,
        String scriptName,
        String scriptInput,
        boolean isRequired )
    {
        super( text, lineNumber );
        this.scriptName = scriptName;
        this.scriptInput = scriptInput;     
        this.isRequired = isRequired;
    }
	
	public String toString()
	{
		return text;
	}
	
	public Object derive( VarContext context ) 
	{	
	    VarScript script = context.getVarScript( scriptName );
        if( script != null )
        {            
        	Object derived = null;
            try
            {
                derived = script.eval( context, scriptInput );                
            }
            catch( Exception e )
            {
                throw new VarException( 
                    "Error evaluating AddScriptResult Mark \"" + N + text + N 
                  + "\" on line [" + lineNumber + "] as :" + N 
                  + this.toString() + N, e );
            }
            if( derived == null && isRequired )
            {
            	
            	throw new ResultRequiredButNull( scriptName, text, lineNumber );
            }
            return derived;
        }
        if( isRequired )
        {
        	throw new VarRequiredButNull( scriptName, text, lineNumber );
        }
        //the script wasnt required
        return "";
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
    public String getScriptName()
    {
        return scriptName;
    }

    @Override
    public boolean isRequired()
    {
        return isRequired;
    }

	@Override
	public String getScriptInput() 
	{
		return scriptInput;
	}

	@Override
	public Set<String> getAllVarNames( VarContext context ) 
	{
		VarScript script = context.getVarScript( scriptName );
		if( script == null )
		{ 
			throw new VarException( 
				"Unable to resolve script named \"" 
			   + getScriptName()+"\" to get Var Names " );
		}
		return script.getInputParser().getAllVarNames( scriptInput );
	}

}	