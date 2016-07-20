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
package io.varcode.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.varcode.text.ParseException;

/**
 * Conventions About Java Source Code 
 * constraints / invariants (of Naming, Packaging, Reading  etc.)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaNaming
{
    ; //singleton enum idiom    

	/**
     * Java Reserved Words
     */
    public static final String[] PRIMITIVE_TYPES =
        { "boolean", "byte", "char", "double", "float", "int", "long", "short"};
    
    /**
     * Java Reserved Words
     */
    public static final String[] RESERVED_WORDS =
        { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
          "class", "const", "continue", "default", "do", "double", "else", "enum", "extends",
          "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
          "int", "interface", "long", "native", "new", "package", "private", "protected", "public",
          "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
          "throw", "throws", "transient", "try", "void", "volatile", "while" };

    /**
     * Understands Code constraints on Java Class Names
     * methods and replace and clean up the code here
     */
    public static class ClassName
    {
        /**
         * validates that <CODE>fullyQualifiedClassName</CODE> is a valid
         * i.e. "java.util.Map"
         * 
         * @param fullyQualifiedClassName
         * @return the valid name
         * @throws ParseException if the name is not valid
         */
        public static String validateFullClassName( String fullyQualifiedClassName )
            throws ParseException
        {
            parsePackageAndClassName( fullyQualifiedClassName );
            return fullyQualifiedClassName;
        }

        public static final String toFullClassName( 
            String packageName, String className )
        {
            if( packageName == null )
            {
                return JavaNaming.ClassName.validateSimpleName( className );
            }
            String trimPackage = packageName.trim(); 
            if( trimPackage.length() == 0  )
            {
                return className;
            }
            String fullName = trimPackage + "." + className;
            
            return JavaNaming.ClassName.validateFullClassName( fullName );
        }
        
        /**
         *  Parses a Fully Qualified Class Name i.e. :<BR> 
         * "com.mycompany.myproduct.mycomponent.Component"
         * into (2) strings:
         * <OL>
         * <LI>String[0] ="com.mycompany.myproduct.mycomponent"; //pkg Name
         * <LI>String[1] ="Component"; //the "Simple" class Name
         * </OL>
         * 
         *  @param fullyQualifiedClassName
         *  @return String[] where:
         * <OL> 
         * <LI>String[0] is the package name (i.e. "java.lang")
         * <LI>String[1] is the "Simple" className (i.e. "String")
         * </OL>
         */
        public static String[] parsePackageAndClassName( String fullyQualifiedClassName )
            throws ParseException
        {
            int lastDotIndex = fullyQualifiedClassName.lastIndexOf( '.' );
            if( lastDotIndex > 0 )
            {
                String packageName = fullyQualifiedClassName.substring( 0, lastDotIndex );

                String simpleClassName = fullyQualifiedClassName.substring( lastDotIndex + 1,
                    fullyQualifiedClassName.length() );

                JavaNaming.PackageName.validate( packageName );
                validateSimpleName( simpleClassName );
                return new String[]{ packageName, simpleClassName };
            }

            validateSimpleName( fullyQualifiedClassName );
            return new String[]{ "", fullyQualifiedClassName };
        }

        public static String validateSimpleName( String simpleClassName )
            throws ParseException
        {
            if( simpleClassName == null || simpleClassName.trim().length() ==0 )
            {
                throw new ParseException( "Class name is null" );
            }
            if( Arrays.binarySearch( RESERVED_WORDS, simpleClassName ) >= 0 )
            {
                throw new ParseException( "Class name: \"" + simpleClassName + "\" reserved word" );
            }
            char[] chars = simpleClassName.toCharArray();
            if( !Character.isJavaIdentifierStart( chars[ 0 ] ) )
            {
                throw new ParseException(
                    "Class name: \"" + simpleClassName + "\" is invalid, char at [" + 0 + "] is '"
                        + chars[ 0 ] + "' invalid for Java Class name" );
            }
            for( int i = 1; i < chars.length; i++ )
            {
                if( !Character.isJavaIdentifierPart( chars[ i ] ) )
                {
                    throw new ParseException(
                        "Class name: \"" + simpleClassName + "\" is invalid, char at [" + i
                            + "] is '" + chars[ i ] + "' invalid for Java class name" );
                }
            }
            if( simpleClassName.length() > 512 )
            {
                throw new ParseException( "Class name \"" + simpleClassName
                    + "\" is > 512 characters in length;" + "Java allows this, but we don't" );
            }
            return simpleClassName;
        }

        /**
         * Converts a "raw" class name to an "actual" valid Java class name
         * @param name what I would like to name the class
         * @return a class name that is valid for Java source code 
         */
        public static String convertName( String name )
        {
            char[] chars = name.toCharArray();
            StringBuilder sb = new StringBuilder();
            if( Character.isJavaIdentifierStart( chars[ 0 ] ) )
            {
                sb.append( chars[ 0 ] );
            }
            else
            {
                //just use an underscore INSTEAD
                sb.append( '_' );
            }
            for( int i = 1; i < chars.length; i++ )
            { //look through the remaining characters after first
                if( Character.isJavaIdentifierPart( chars[ i ] ) )
                {
                    sb.append( chars[ i ] );
                }
                else
                {
                    sb.append( '_' );
                }
            }
            String validName = sb.toString();

            if( Arrays.binarySearch( RESERVED_WORDS, validName ) >= 0 )
            { //verify that we didnt try to name it as a reserved word                    
                return "_" + validName;
            }
            return validName;
        }

        public static String fromPath( String pathName )
        {
            String className = simpleClassNameFromPathName( pathName );
            validateSimpleName( className );
            return className;
        }

        /**
         * Turn the Canonical name of the class, i.e.<BR>
         * 
         * <PRE>"io.varcode.tailor.ClassTailor" </PRE>
         * 
         * ...into a "Path" to source path of the class<BR>
         * 
         * <PRE>"io\\codemark\\tailor\\ClassTailor.java" </PRE>
         * 
         * @param clazz the class
         * @return
         */
        public static String toSourcePath( java.lang.Class<?> clazz )
        {
            return toSourcePath( clazz.getCanonicalName() );
        }

        /**
         * Given a Fully Qualified Class Name:<BR>
         * "io.varcode.Code"<BR>
         * returns a String Resource Path to the Class:<BR>
         * "io\\codemark\\Code.java"<BR>
         * 
         * @param canonicalClassName
         * @return
         * @throws ParseException
         */
        public static String toSourcePath( String canonicalClassName )
            throws ParseException
        {
            String[] packageAndClass = parsePackageAndClassName( canonicalClassName );

            String packagePath = packageAndClass[ 0 ].replace( '.', File.separatorChar );
            if( packagePath.length() > 0 )
            {
                packagePath += File.separatorChar;
            }
            if( packageAndClass[ 1 ].endsWith( ".java" ) )
            {
                return packagePath + packageAndClass[ 1 ];    
            }
            return packagePath + packageAndClass[ 1 ] + ".java";
        }

        /** 
         * Given a fully qualified path to a file:
         * "C:\\dev\\apps\\source\\com\\mycompany\\myapp\\myComponent\\StructTable.java"
         * ...returns what the class name would be:
         * "StructTable"
         * 
         * @param fileName the name of a .Java File
         */
        public static String simpleClassNameFromPathName( String fileName )
        {
            if( !fileName.endsWith( ".java" ) )
            {
                throw new ParseException(
                    "The fileName \"" + fileName + "\" does not end in .java" );
            }
            // (5) for ".java" + (1) since length > index
            int lastCharInClassName = fileName.length() - ( ".java".length() + 1 );
            //rewind from BEFORE the .java until you reach an invalid character
            //String theClassName = fileName.substring( 0, lastCharInClassName );

            //point to start at the last char in the name keep rewinding 
            int charIndex = lastCharInClassName;

            while( charIndex >= 0 )
            {
                if( charIndex > 0 && Character.isJavaIdentifierPart( fileName.charAt( charIndex ) )
                    || Character.isJavaIdentifierStart( fileName.charAt( charIndex ) ) )
                {
                    charIndex--;
                }
                else
                {
                    return fileName.substring( charIndex + 1, lastCharInClassName + 1 );
                }
            }
            return fileName.substring( 0, lastCharInClassName + 1 );
        }
        
        /**
         * Given Java source code, extracts the ClassName from it
         * 
         * TODO: So I realize this is kinda a hack, but I didnt want to 
         * have to force a Lexer and parser
         * @param javaSource
         * @return
         */
        public static String extractFromSource( String javaSource )
        { //maybe we arent changing the packageName
            List<String>potentialClassName = 
                getExcerpts( javaSource, "class", "{" );

            for( int i = 0; i < potentialClassName.size(); i++ )
            {
                String candidate = potentialClassName.get( i );
                try
                {
                    return validateSimpleName( candidate.trim() );
                }
                catch( ParseException pe )
                {
                    //not ideal, but swallow this one
                }
            }
            
             potentialClassName =
                getExcerpts( javaSource, "class", "extends" );

            for( int i = 0; i < potentialClassName.size(); i++ )
            {
                String candidate = potentialClassName.get( i );
                try
                {
                    return validateSimpleName( candidate.trim() );
                }
                catch( ParseException pe )
                {
                    //not ideal, but swallow this one
                }
            }

            potentialClassName = getExcerpts( javaSource, "class", "implements" );

            for( int i = 0; i < potentialClassName.size(); i++ )
            {
                String candidate = potentialClassName.get( i );
                try
                {
                    return validateSimpleName( candidate.trim() );
                }
                catch( ParseException pe )
                {
                    //not ideal, but swallow this one
                }
            }
            
            potentialClassName = getExcerpts( javaSource, "interface", "extends" );

            for( int i = 0; i < potentialClassName.size(); i++ )
            {
                String candidate = potentialClassName.get( i );
                try
                {
                    return validateSimpleName( candidate.trim() );
                }
                catch( ParseException pe )
                {
                    //not ideal, but swallow this one
                }
            }
            potentialClassName = getExcerpts( javaSource, "enum", "{" );

            for( int i = 0; i < potentialClassName.size(); i++ )
            {
                String candidate = potentialClassName.get( i );
                try
                {
                    return validateSimpleName( candidate.trim() );
                }
                catch( ParseException pe )
                {
                    //not ideal, but swallow this one
                }
            }
            potentialClassName = getExcerpts( javaSource, "interface", "{" );

            for( int i = 0; i < potentialClassName.size(); i++ )
            {
                String candidate = potentialClassName.get( i );
                try
                {
                    return validateSimpleName( candidate.trim() );
                }
                catch( ParseException pe )
                {
                    //not ideal, but swallow this one
                }
            }
            return null;
        }
    }

    /** 
     * Constraints on Java Identifiers     
     */
    public static class IdentifierName
    {
        public static void validateName( String identifierName )
            throws ParseException
        {
            if( ( identifierName == null ) || identifierName.length() < 1 )
            {
                throw new ParseException( 
                	"Invalid identifier name : \"" + identifierName 
                	+ "\" Standard Identifiers must be at least 1 character" );
            }
            if( !Character.isJavaIdentifierStart( identifierName.charAt( 0 ) ) )
            {
                throw new ParseException(
                	"Invalid Java identifier name: \"" + identifierName 
                	+"\" Standard Identifiers must start with a letter{a-z, A-Z} " 
                	+ "or '_', '$'" );
            }
            for( int i = 1; i < identifierName.length(); i++ )
            {
                if( !Character.isJavaIdentifierPart( identifierName.charAt( i ) ) )
                {
                    throw new ParseException(
                    	"Invalid Java identifier name: \"" + identifierName 	
                    	+ "\" Standard Identifiers cannot contain the character '"
                        + identifierName.charAt( i ) + "' at [" + i + "]" );
                }
            }
            if( Arrays.binarySearch( RESERVED_WORDS, identifierName ) > 0 )
            {
                throw new ParseException( 
                	"Invalid Standard Identifier name \"" + identifierName
                    + "\" is a reserved word" );
            }
        }
    }
    
    public static class TypeName
    {
    	public static void validateName( String typeName )
            throws ParseException
        {
    		if( ( typeName == null ) || typeName.length() < 1 )
            {
    			throw new ParseException( 
                    	"Invalid type name : \"" + typeName 
                    	+ "\" Standard Identifiers must be at least 1 character" );
            }
            if( !Character.isJavaIdentifierStart( typeName.charAt( 0 ) ) )
            {
                throw new ParseException(
                  	"Invalid Java type name: \"" + typeName 
                   	+"\" types must start with a letter{a-z, A-Z} " 
                   	+ "or '_', '$'" );
            }
            for( int i = 1; i < typeName.length(); i++ )
            {
                if( !Character.isJavaIdentifierPart( typeName.charAt( i ) ) )
                {
                    throw new ParseException(
                     	"Invalid Java type name: \"" + typeName 	
                       	+ "\" types cannot contain the character '"
                        + typeName.charAt( i ) + "' at [" + i + "]" );
                }
            }
            if( Arrays.binarySearch( RESERVED_WORDS, typeName ) > 0 )
            {   //its a reserved word 
             	if( Arrays.binarySearch( PRIMITIVE_TYPES, typeName ) < 0 )
               	{	//...but its not a primitive type
               		throw new ParseException( 
               			"Invalid Java type name \"" + typeName
               			+ "\" is a reserved word" );
               	}
            }
        }	
    }

    /**
     */
    public static class PackageName
    {
        public static String validate( String packageName )
            throws ParseException
        {
            String[] split = splitToParts( packageName );
            for( int i = 0; i < split.length; i++ )
            {
                validatePart( split[ i ], packageName );
            }
            return packageName;
        }

        private static String[] splitToParts( String packageName )
        {
            int charPointer = 0;
            List<String> parts = new ArrayList<String>();
            int nextDot = packageName.indexOf( '.', charPointer );
            while( nextDot >= 0 )
            {
                String part = packageName.substring( charPointer, nextDot );
                parts.add( part );
                charPointer = nextDot + 1;
                nextDot = packageName.indexOf( '.', charPointer );
            }
            if( charPointer < packageName.length() )
            {
                String part = packageName.substring( charPointer );
                parts.add( part );
            }
            return parts.toArray( new String[ parts.size() ] );
        }

        private static String validatePart( String part, String fullName )
        {
            if( Arrays.binarySearch( RESERVED_WORDS, part ) >= 0 )
            {
                throw new ParseException( 
                	"package name part \"" + part
                    + "\" of package name : \""+ fullName + 
                    "\" is a reserved word, invalid for package name" );
            }
            if( !Character.isJavaIdentifierStart( part.charAt( 0 ) ) )
            {
                throw new ParseException( "first character \"" + part.charAt( 0 )
                    + "\" of package part \"" + part + "\" of package name \"" 
                	+ fullName + "\" is invalid");
            }
            char[] chars = part.toCharArray();
            for( int i = 1; i < chars.length; i++ )
            {
                if( !Character.isJavaIdentifierPart( chars[ i ] ) )
                {
                    throw new ParseException(
                        "character in part \"" + part + "\" at char [" + i + 
                        "] of package name : \"" + fullName + "\" is invalid " );
                }
            }
            return part;
        }

        public static String toPath( String packageName )
        { //validate that it is a valid package name
            validate( packageName );
            return packageName.replace( ".", File.separator ) + File.separator;
        }
        
        public static String extractFromSource( String javaSource )
        {
            //maybe we arent changing the packageName
            List<String> potentialPkgNames =
                getExcerpts( javaSource, "package", ";" );
            
            if( potentialPkgNames.size() > 0 )
            {
                for( int i = 0; i < potentialPkgNames.size(); i++ )
                {
                    String candidate = potentialPkgNames.get( i );
                    try
                    {
                        return JavaNaming.PackageName.validate( candidate.trim() );
                    }
                    catch( ParseException pe )
                    {
                        //not ideal, but swallow this one
                    }
                }
            }
            return null;
        }
    } //Package

    
    /**
     * This ASSUMES the source will have something like
     * "package io.varcode.data;"
     * ...where the package AND contents are on the same line
     *  
     * getExcerpts(..., "package" ";")
     * <PRE>
     * "package io.varcode.something.anotherthing;"
     *         ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^       
     * returns " io.varcode.something.anotherthing"
     * </PRE>
     * 
     * <PRE>
     * getExcerpts(..., "class" "extends");
     * getExcerpts(..., "class" "implements");
     * getExcerpts(..., "class" "{");
     * 
     * "public class Blammo {"  
     *              ^^^^^^^^
     * returns " Blammo "
     * 
     *               
     * "public abstract class AbstractClass extends BaseClass implements Serielizable"
     *                       ^^^^^^^^^^^^^^^ 
     * returns " AbstractClass "
     *  
     * @param source
     * @param targetString
     * @return
     */
    public static final List<String> getExcerpts( 
        String source, //the entire data  
        String open, //i.e. "package" 
        String close ) //i.e. ";"
    {
        int openFromIndex = source.indexOf( open, 0 );
        int closeFromIndex;
        
        List<String> excerpts = new ArrayList<String>();
        //int nextCloseCharIndex = source.indexOf( close, closeFromIndex );
        while( openFromIndex > -1 )
        {
            closeFromIndex = source.indexOf( close, openFromIndex );
            if( closeFromIndex > -1 )
            {                    
                excerpts.add( 
                    source.substring( openFromIndex + open.length(), closeFromIndex ) );
            }
            else
            {
                return excerpts;
            }
            openFromIndex = source.indexOf( open, openFromIndex + open.length() );
        }            
        return excerpts;
    }
}
