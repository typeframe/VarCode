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
package io.varcode.dom;

import io.varcode.context.ExpressionEvaluator_JavaScript;
import io.varcode.text.ParseException;

/**
 * Audits Var names that occur in {@code Markup}.  
 * Used when {@code Markup} is being read/ parsed / compiled. 
 * 
 * Provides a "default" implementation BASE.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface VarNameAudit
	extends MarkupComponent
{
	/** a */ 
    public static final StandardVarName BASE = 
    	StandardVarName.INSTANCE;

    /** verifies that the name used for identifying an entity is valid */
    public String audit( String name )
        throws ParseException;

    /** 
     * validates that the String/VarName represents a Standard Identifier in 
     * most programming languages */
    public enum StandardVarName
        implements VarNameAudit
    {
        INSTANCE;

        public String audit( String name )
            throws ParseException
        { //first verify it is not empty or null             
            if( name == null )
            {
                throw new ParseException( "Null Name Not allowed" );
            }
            if( name.trim().length() == 0 )
            {
                throw new ParseException( "Trimmed Empty Name Not allowed" );
            }
            try
            {   //we validate names based on standard Java identifiers
                return validateName( name );
            }
            catch( ParseException ivc )
            {
                throw new ParseException( 
                	" \"" + name + "\" is invalid for a standard identifier",
                    ivc );
            }
        }

        public static String validateName( String identifierName )
            throws ParseException
        {
            if( ( identifierName == null ) || identifierName.length() < 1 )
            {
                throw new ParseException( "Names must be at least 1 character" );
            }
            if( !Character.isJavaIdentifierStart( identifierName.charAt( 0 ) ) )
            {
                throw new ParseException(
                    "Names must start with a letter{a-z, A-Z} " + "or '_', '$'" );
            }
            for( int i = 1; i < identifierName.length(); i++ )
            {
                if( !Character.isJavaIdentifierPart( identifierName.charAt( i ) ) )
                {
                    throw new ParseException( "Names cannot contain the character '"
                        + identifierName.charAt( i ) + "' at [" + i + "]" );
                }
            }
            if( ExpressionEvaluator_JavaScript.RESERVED_WORDS.contains( identifierName ) )
            {
                throw new ParseException( 
                	"Name \""+identifierName + "\" is a reserved word" );
            }
            return identifierName;
        }
        
        public String toString()
        {
        	return this.getClass().getName();
        }
    }
}