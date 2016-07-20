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

import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasVars;
import io.varcode.dom.mark.Mark.IsNamed;
import io.varcode.dom.mark.Mark.MayBeRequired;
import io.varcode.text.TextBuffer;

/**
 * Adds Code (bound to a given var name). 
 * Optionally provide a default in case the var name resolves to null.
 */
//
//example      : "{+name+}"
//    Prefix   : "{+"
//    Postfix  : "+}"
//    VarName  : "name"
//    Default  : ""
//    Required : false

// example      : "/*{+name+}*/"
//	   Prefix   : "/*{+"
//     Postfix  : "+}*/"
//     VarName  : "name"
//     Default  : ""
//     Required : false

// example      : "/*{+name|default+}*/"
//     Prefix   : "/*{+"
//     Postfix  : "+}*/"
//     VarName  : "name"
//     Default  : "default"
//     Required : false

// example      : "/*{+name*+}*/"
//     Prefix   : "/*{+"
//     Postfix  : "+}*/"
//     VarName  : "name"
//     Default  : "default"
//     Required : true 

public class AddVar
    extends Mark
	implements IsNamed, BlankFiller, HasVars, MayBeRequired 
{	
	private final String varName;
	
	private final String defaultValue;
	
	private final boolean isRequired;
	
	private final Set<String> vars;
	
	public AddVar( 
	    String text, 
	    int lineNumber,
	    String varName, 
	    boolean isRequired,
	    String defaultValue )
	{
	    super( text, lineNumber );
	    this.varName = varName;
	    this.defaultValue = defaultValue;
	    this.isRequired = isRequired;
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
			context.getVarResolver().resolveVar( context, varName );
		
		if ( resolved == null )
		{
		    if( isRequired )
            {
		    	throw new VarRequiredButNull( varName, text, lineNumber );                
            }
		    resolved = defaultValue;
		}
		return resolved;	
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