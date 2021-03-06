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
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasVars;
import io.varcode.text.TextBuffer;

/**
 * A Form of Code (one or more java statements) that is <I>conditionally</I> 
 * written to the tailored source.
 * 
 * There are (2) variants: 
 * <UL>
 *  <LI><B>On name</B> will write the block to the tailored source if the name
 *  resolves to a <B>non-null</B> value.<PRE> 
 *  / *{?(log!=null):
 *  import org.slf4j.LoggerFactory;
 *  import org.slf4j.Logger;
 *  }* /</PRE>
 *  
 *  <LI><B>On name equals</B> will write the block to the tailored source is the
 *  name resolves to a value that is is <B>equal to a target value</B>.<PRE>
 *  / *{?(log=trace):
 *  LOG.trace( "Inside Loop : \"" + methodName + "\"" ); 
 *  }* /</PRE>
 * </UL> 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
       /*{?log=trace:
       LOG.trace( "In Method " );
       }*/
public class AddIfVar
    extends Mark
    implements BlankFiller, HasVars
{
    /** the var name in the context to test*/
    public final String varName;
    
    /** expected target value ...of the <B>value</B> associated with the varName */
    public final String targetValue;
    
    /** code/text conditionally written to the tailored source*/ 
    public final String conditionalText;

    private final Set<String> vars;
    
    public AddIfVar( 
        String text, 
        int lineNumber, 
        String name,
        String targetValue,
        String conditionalText )
    {
        super( text, lineNumber );
        this.varName = name;
        this.targetValue = targetValue;
        this.conditionalText = conditionalText;        
        this.vars = new HashSet<String>();
        this.vars.add( name );
    }
   

    public String getVarName()
    {
        return varName;
    }

    public String getTargetValue()
    {
    	return targetValue;
    }
    
    public String getConditionalText()    
    {
        return conditionalText;
    }
    
    public void fill( VarContext context, TextBuffer buffer )
    {
        buffer.append( derive( context ) );
    }
    
    public Object derive( VarContext context )
    {
        Object resolved = context.getVarResolver().resolveVar( context, varName );
        if( resolved == null )
        {        	
            return null;
        }
        if( targetValue == null )
        {
            return conditionalText;                    	
        }
        if ( resolved.toString().equals( targetValue ) )
        {
            return conditionalText;            
        }
        return null;
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
