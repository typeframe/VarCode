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

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasVars;
import io.varcode.dom.mark.Mark.IsNamed;
import io.varcode.dom.mark.Mark.MayBeRequired;
import io.varcode.dom.mark.Mark.WrapsContent;
import io.varcode.text.TextBuffer;

/** 
 * A Mark that contains a "default" information that is to be replaced with data
 * (also, evaluates the "interior" of the mark (the text within the Mark tags) 
 * to retain certain characters (TABS, spaces, line feeds, quotes)
 */	
//example     : "/*{+name*/John/*+}*/"
//Prefix      : "/*{+"
//Postfix     : "/*+}*/"
//Name        : "name"
//BetweenText : "John"
//Default     : null

//example     : "/*{+count|*/"3"/*+}*/"
//Prefix      : "/*{+"
//Postfix     : "/*+}*/"
//Name        : "count"
//BetweenText : "\"3\""
//Default     : "\"3\""

 //when we encounter a AddReplace within a Template
 // we "check" the interior of the mark for "retained" characters
 // (tabs, spaces and line feeds)
 //
 //  String NAME = /*{+addReplace*/"a Quoted String"/*}*/;
 //                            ^               ^
 //                            |               |
 //  in the example above the name is quoted, so we retain the prefix " and postfix "
 //  when the first and last characters in the "between text" are "quotes"
 //   
 //  during processing if we have a binding Map where:
 //  bindingMap.put ( "addReplace", "eric" );
 //
public class ReplaceWithVar
	extends Mark
	implements IsNamed, BlankFiller, WrapsContent, HasVars, MayBeRequired
{	
    /** All of the Text wrapped within the mark (between the tags) */
    private final String wrappedContent;

	/** the name of the var*/
	private final String varName;
	
	/** value used if the Mark resolves to null */ 
	private final Object defaultValue;
	
	/** is this field REQUIRED to be bound when resolving  */
	private final boolean isRequired;
	
	private final Set<String> vars;
	
	public ReplaceWithVar( 
	    String text, 
	    int lineNumber, 
	    String varName,
	    String wrappedContent,
	    String defaultValue,
	    boolean isRequired )
	{
	    super( text, lineNumber );
	    this.varName = varName;
	    if( isRequired && defaultValue != null )
	    {
	        throw new VarException(
	            "ReplaceWithVarMark :" + N + text + N + 
	            "Mark cannot have a default AND be Required" );
	    }
	    this.wrappedContent = wrappedContent; //Text.replaceComment( wrappedContent );
	    this.defaultValue = defaultValue;
	    this.isRequired = isRequired;	
	    this.vars = new HashSet<String>();
	    this.vars.add( varName );
	}

	public String getVarName() 
	{				
		return varName; 
	}
		
	public String getWrappedContent()
	{
	    return this.wrappedContent;
	}
	
	public String toString()
	{
		return text; 
	}
	
	public Object derive( VarContext context )
	    throws VarException
	{ 
		Object toFill = context.getVarResolver().resolveVar( context, varName );
		if ( toFill == null )
		{
		    if( isRequired )
            {
                throw new VarRequiredButNull( varName, text, lineNumber);
            }
		    if( defaultValue != null )
		    {
		        return defaultValue.toString();
		    }
		    return null;
		}		
		return toFill;	
	}
	
    @Override
    public void fill( VarContext context, TextBuffer buffer )
    {
        buffer.append( derive( context ) );
    }
    
	public boolean isRequired()
	{
	    return this.isRequired;
	}

    public Object getDefault()
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