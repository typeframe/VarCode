/*
 * Copyright 2016 M. Eric DeFazio.
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
package io.varcode.dom.forml;

import java.util.HashMap;
import java.util.Map;

import io.varcode.context.VarContext;
import io.varcode.dom.MarkupException;
import io.varcode.dom.MarkupParser;
import io.varcode.dom.VarNameAudit;
import io.varcode.dom.mark.AddExpressionResult;
import io.varcode.dom.mark.AddIfVar;
import io.varcode.dom.mark.AddScriptResult;
import io.varcode.dom.mark.AddVar;
import io.varcode.dom.mark.Mark;

/**
 * Uses the "base" conventions for parsing varcode source 
 * and creating {@code CodeForm}s
 * 
 * <UL>
 *   <LI>AddVar {+name+} {+name|default+} {+name*+} 
 *   <LI>AddScriptResult {+$scriptName(param1)+}
 *   <LI>AddIfVar {+?(log==true):log.debug("got here");+}
 * </UL>  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ForMLParser
    implements MarkupParser
{
    public static final ForMLParser INSTANCE = 
        new ForMLParser( ); 

    public static final String N = System.lineSeparator();

    private static final Map<String, String> OPENTAG_TO_CLOSETAG = 
        new HashMap<String, String>();

    static
    { 
        OPENTAG_TO_CLOSETAG.put( "{+", "+}" ); //Add
        OPENTAG_TO_CLOSETAG.put( "{+$", "+}" ); //AddWithScript
        OPENTAG_TO_CLOSETAG.put( "{+?", "+}" ); //IfAdd
        OPENTAG_TO_CLOSETAG.put( "{+((", "))+}" ); //AddExpressionResult
    }
    
    private ForMLParser( )
    { }
    
    /** 
     * given an open tag for a Mark... 
     * find (the FIRST) close tag for the mark
     * 
     * @param openTag
     * @return
     */
    public String closeTagFor( String openTag )
        throws MarkupException
    {
        String closeTag = OPENTAG_TO_CLOSETAG.get( openTag );
        if( closeTag == null )
        {
            throw new MarkupException( 
                "Open tag \"" + openTag + "\" is not recognized",
                openTag, 
                -1 );
        }
        return closeTag;
    }

    public static final boolean charIs( String string, int index, char expect )
    {
        return  string != null  
                && string.length() > index 
                && index > -1
                && string.charAt( index ) == expect; 
    }
    
    /** 
     * 
     * TODO cant I just look for next "{+" ?? instead of looking for '{'
     * 
     * Scan the line for a valid "open tag" and return the first one in the line
     * @param line a line of text
     * @return the first Open Tag in the string, or null if no open tags 
     * are found in the String 
     */
    public String getFirstOpenTag( String line )
    {        
        int indexOfBrace = line.indexOf( '{' );        
        while( indexOfBrace > -1 && indexOfBrace < line.length() - 1 ) //a '{' is a prerequisite for an open tag
        {   
            //if add open
            // {+ } -or- {+$ }
            //if( line.charAt( indexOfBrace + 1 ) == '+' )
            if( charIs( line, indexOfBrace + 1, '+' ) )	
            { // "{+" or "{+$"                
                //if( indexOfBrace >= 1
                //    && line.charAt( indexOfBrace + 2 )== '$' )
                if( charIs( line, indexOfBrace + 2, '$' ) ) 	
                {
                    return AddScriptResultMark.OPEN_TAG; //"{+$";
                }                                
                if( charIs( line, indexOfBrace + 2, '?' ) )
                {                
                    return AddIfVarMark.OPEN_TAG; //"{+?"; //IfAdd                
                }       
                if( charIs( line, indexOfBrace + 2, '(' ) //"{+((" AddExpressionResult  
                	&&  charIs( line, indexOfBrace + 3, '(' ) )
                {
                	return AddExpressionResultMark.OPEN_TAG;
                }
                return AddVarMark.OPEN_TAG; //"{+";                
            }
            //find the next { brace 
            indexOfBrace = line.indexOf( '{', indexOfBrace + 1 );
        }
        return null;
    }


    /** 
     * Given Text parse and return the appropriate MarkAction
     * 
     * NOTE: I could make this more efficient, but it's fine 
     * <UL>
     * <LI>{+ +}
     * <LI>{+$ +}
     * <LI>{+? +}
     * <LI>{+(( ))+}
     * 
     * @param markText the text of the entire mark
     * @param the line number where the mark appears
     * @param nameAudit audits the name of the Mark for validity
     * @throws CodemarkException if the Mark is invalid
     */
    public Mark parseMark(
        VarContext parseContext,
        String markText, 
        int lineNumber )
        throws MarkupException
    {          
    	//System.out.println( parseContext );
    	VarNameAudit nameAudit = parseContext.getVarNameAudit();
    	
        if( markText.startsWith( AddScriptResultMark.OPEN_TAG ) ) // "{+$"
        {            
            return AddScriptResultMark.of( markText, lineNumber );
        }
        if( markText.startsWith( AddIfVarMark.OPEN_TAG ) ) // "{+?"
        {
            return AddIfVarMark.of( markText, lineNumber, nameAudit );
        }
        if( markText.startsWith( AddExpressionResultMark.OPEN_TAG ) ) // {+((
        {
        	return AddExpressionResultMark.of( markText, lineNumber );
        }
        if( markText.startsWith( AddVarMark.OPEN_TAG ) ) // "{+"
        {
            return AddVarMark.of( markText, lineNumber, nameAudit );
        }            

        throw new MarkupException( 
            "Unknown Form MarkAction on line [" + lineNumber + "] :" + N 
            + markText,
            markText, 
            lineNumber );
    }
     
    public static class AddExpressionResultMark
    {
    	public static final String OPEN_TAG = "{+((";
        
        public static final String CLOSE_TAG = "))+}";
        
        
        public static AddExpressionResult of( 
            String text, int lineNumber )
            throws MarkupException
        {
        	if( !text.startsWith( OPEN_TAG ) )
            {
        		throw new MarkupException(
                    "Invalid AddExpressionResult Mark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );                
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
            	throw new MarkupException(
                    "Invalid AddExpressionResult Mark : " + N 
                   + text + N + "  ... on line [" + lineNumber + "] must end with"
                   + " \"" + CLOSE_TAG + "\" ",
                   text, 
                   lineNumber );   
            }
                
            String expression = 
            	text.substring( 
                    OPEN_TAG.length(), 
                    text.length() - CLOSE_TAG.length() );
        
            return new AddExpressionResult( text, lineNumber, expression );    
        }        
    }
    
    public static class AddVarMark
    {
        public static final String OPEN_TAG = "{+";
        
        public static final String CLOSE_TAG = "+}";
        
        public static AddVar of( 
            String text, int lineNumber, VarNameAudit nameAudit )
            throws MarkupException
        {
        	if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddVarMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );                
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddVarMark : " + N 
                   + text + N + "  ... on line [" + lineNumber + "] must end with"
                   + " \"" + CLOSE_TAG + "\" ",
                   text, 
                   lineNumber );   
            }
            String name = null;
            boolean isRequired = false;
            String defaultValue = null;
            
            String tag = text.substring( 
                OPEN_TAG.length(), 
                text.indexOf( "+}" ) );
    
            String[] nameDefault = Tokenize.byChar( tag, '|' );
            if( nameDefault[ 0 ].endsWith( "*" ) )
            {
                name = nameDefault[ 0 ].substring( 0, nameDefault[ 0 ].length() -1 );
                isRequired = true;            
            }
            else
            {
                name = nameDefault[ 0 ];
                isRequired = false;
            }
            if( nameDefault.length == 2 )
            {    //they specified a default
                 defaultValue = nameDefault[ 1 ];
            }
            else
            {
                defaultValue = null;
            }            
            try
            {
                nameAudit.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException (
                   "Invalid Add  \"" + name + "\", on line [" 
                 + lineNumber + "]", 
                 text, 
                 lineNumber, 
                 e );
            }
            return new AddVar( text, lineNumber, name, isRequired, defaultValue );
        }
    }
    
    public static class AddScriptResultMark
    {
        public static final String OPEN_TAG = "{+$";  
        public static final String CLOSE_TAG = "+}"; 
        
        public static final String CONTEXT_SEPARATOR = ":";
        public static final String OPEN_PARAMETERS = "(";
        public static final String CLOSE_PARAMETERS = ")";
        
        public static AddScriptResult of( 
            String text, 
            int lineNumber )
            throws MarkupException
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResult : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );                
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResult : " + N 
                   + text + N + "  ... on line [" + lineNumber + "] must end with"
                   + " \"" + CLOSE_TAG + "\" ",
                   text, 
                   lineNumber );   
            }

            int openParamIndex = text.indexOf( '(' );
            int closeParamIndex = text.lastIndexOf( ')' );

            // "/*{+$javascript(paramName=value, a, 100)}*/"            
            String scriptName = text.substring( OPEN_TAG.length(), openParamIndex );
            // parameters = null; 
            if( openParamIndex < 0  ||  closeParamIndex < 0 )
            {
                throw new MarkupException(
                    "AddScriptResult with name \"" + scriptName 
                  + "\" on line [" + lineNumber  
                  + "] must have a '(' and ')' to demarcate parameters ",
                    text, 
                    lineNumber );
            }
            if( openParamIndex > closeParamIndex )
            {
                throw new MarkupException(
                    "AddScriptResult with name \"" + scriptName 
                  + "\" on line ["+ lineNumber 
                  + "] must have a '(' BEFORE ')' to demarcate parameters",
                  text, lineNumber);
            } 
            String paramContents = 
                text.substring( openParamIndex + 1 , closeParamIndex );
                
            boolean isRequired = false;
            // "{+script()*+}" (REQUIRED)
            if( charIs( text, closeParamIndex + 1, '*' ) )
            {
            	isRequired = true;
            }
            return new AddScriptResult( 
                text, 
                lineNumber, 
                scriptName, 
                paramContents,
                isRequired );            
        }
    }
   
    public static class AddIfVarMark
    {
        /** Opening mark for a AddIfVar Mark */
        public static final String OPEN_TAG = "{+?";
        
        /** Closing mark for a AddIfVar Mark */
        public static final String CLOSE_TAG = "+}";

        public static AddIfVar of(
            String text, int lineNumber )
        {
            return of( text, lineNumber, VarNameAudit.BASE );
        }
        public static AddIfVar of( 
            String text, int lineNumber, VarNameAudit nameAudit )
        {
            String name = null;
            String targetValue = null;
            String code = null;
            
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid IfAdd on line [" + lineNumber + "], must start "
                    + "with open mark \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid IfAdd on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid IfAdd " + N + text + " on line [" 
                    + lineNumber + "], Mark must contain ':' separating condition"
                    + "from code",  
                    text, 
                    lineNumber );
            }
            String condition = text.substring( OPEN_TAG.length(), colonIndex );
            //String nameEquals = parseNameEquals( this.text, lineNumber );
            
            int equalsIndex = condition.indexOf( "==" );
            if( equalsIndex > -1 )
            {
                name = condition.substring( 0, equalsIndex ).trim();
                
                targetValue = condition.substring( 
                    equalsIndex + 2, 
                    condition.length() );           
            }
            else
            {
                equalsIndex = condition.indexOf( "=" );
                if( equalsIndex > -1 )
                {
                    name = condition.substring( 0, equalsIndex ).trim();
                
                    targetValue = condition.substring( 
                        equalsIndex + 1, 
                        condition.length() );          
                }
                else
                {
                    name = condition.trim();
                    targetValue = null;
                }
            }           
            //basically everything AFTER : and before close MARK
            //TODO TEST THIS
            code = text.substring( 
                colonIndex + 1, 
                text.length() - CLOSE_TAG.length() );
            try
            {
                nameAudit.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid IfAdd name \"" + name 
                    + "\" on line [" + lineNumber + "]",
                    text, 
                    lineNumber );
            }
            return new AddIfVar( text, lineNumber, name, targetValue, code );
        }
    }
}
