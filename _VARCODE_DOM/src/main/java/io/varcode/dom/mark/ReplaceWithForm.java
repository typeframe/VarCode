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
import io.varcode.context.VarContext;
import io.varcode.dom.ParseState;
import io.varcode.dom.form.Form;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasForm;
import io.varcode.dom.mark.Mark.HasVars;
import io.varcode.dom.mark.Mark.MayBeRequired;
import io.varcode.dom.mark.Mark.WrapsContent;
import io.varcode.text.TextBuffer;

/**
 * Replace the contents of this Mark, supplying a "Form" for the replacement
 * 
 * Communicates the "Form" that the Data to be replaced takes
 * 
 * A Mark that contains a "default" information that is to be replaced with data
 * (also, evaluates the "interior" of the mark (the text within the Mark tags) 
 * to retain certain characters (TABS, spaces, line feeds, quotes)
 */	
 //when we encounter a ReplaceMark within a Template
 // we "check" the interior of the mark for "retained" characters
 // (tabs, spaces and line feeds)
 //
 //     /*{{+:
 //     public final _IntField {+fieldName} = _IntField.INSTANCE;*/ <-- this is the Form of EACH

 //     public final _IntField A = _IntField.INSTANCE; 
 //     public final _IntField B = _IntField.INSTANCE; 
 //     public final _IntField C = _IntField.INSTANCE;
 //     /*}}*/
 //       
public class ReplaceWithForm
	extends Mark
	implements BlankFiller, HasForm, WrapsContent, HasVars, MayBeRequired
{	
	/** the (OPTIONAL) name of the var*/
	private final String varName;
	
	/** 
	 * the pattern for each element:
	 * for instance an "amounts" Mark that represents Dollars and Cents
	 * {{+amounts:
	 * ${+dollars}.{+cents}, 
	 * }}
	 * ...
	 * which creates a pattern:
	 * "${+dollars}.{+cents}, "
	 * 
	 * we can "derive" the "amount" mark from "dollars" and "cents"
	 * <PRE>
     *  //here we bind the patterns to the 
     * String theAmount = 
     *     myVarCode.getForm( "amounts" )
     *     .bind( "dollars", 5, 
     *            "cents", 23 ); //"$5.23"
     * 
	 *  //here we inline the dollars and cents
	 * String theAmount = 
	 *     myVarCode.getForm( "amounts" ).fill( 5, 23 ); //"$5.23"
	 * </PRE>    
	 */ 
	private final Form form;	
	
	/** is this field REQUIRED to be bound when resolving  */
    private final boolean isRequired;
    
    /** the code/content wrapped within the tag*/ 
    private final String wrappedContent;
	 
	public ReplaceWithForm( 
	    String markText, 
	    int lineNumber,
	    String name,
	    Form form,
	    boolean isRequired,
	    String wrappedContent )
	{
		super( markText, lineNumber );
		this.varName = name;
		this.form = form;
		this.isRequired = isRequired;
		this.wrappedContent = wrappedContent;
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

	public boolean isRequired()
	{
	    return isRequired;
	}
	
	public void fillTo( VarContext context, StringBuilder out )
	    throws VarException
    {
	    out.append( derive( context ) );
	}
	 
	public void fill( VarContext context, TextBuffer buffer )
	{
	    buffer.append( derive( context ) );
	}
	
    public Form getForm()
    {
        return form;
    }

    /** Derives the Mark given the binding */
    public String derive( VarContext context )
    {
        try
        {        	
            return form.derive( context );            
        }
        catch( Exception e )
        {
            throw new VarException (
                "Unable to derive ReplaceWithForm \"" + varName + "\" for mark: " 
               + N + text + N + " on line [" + lineNumber + "]", e );
        }             
    }
    
    @Override
    public void onMarkParsed( ParseState parseState )
    {
        parseState.reserveBlank();
    }

    @Override
    public Set<String> getAllVarNames( VarContext context )
    {
        return form.getAllVarNames( context );
    }
}		    