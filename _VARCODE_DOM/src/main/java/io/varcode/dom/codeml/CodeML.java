package io.varcode.dom.codeml;

import java.io.InputStream;

import io.varcode.context.VarContext;
import io.varcode.dom.Dom;
import io.varcode.dom.MarkupException;
import io.varcode.dom.mark.Mark;
import io.varcode.markup.MarkupRepo.MarkupStream;

/**
 * CodeML is a Markup Language that "hides" Marks within comments of
 * source code of languages (Java, Javascript, C, C++, C#, D, F, ...)
 * ...(using /&#42; &#42;/) For example: 
 * <PRE>"public class /&#42;{+className&#42;/_Clazz/&#42;+}&#42;/ {}";</PRE>
 * NOTE:
 *    <UL>
 *       <LI>the Javac Compiler disregards the Mark-comments, and can compile and 
 *       create a class (that contains mark-comments)
 *       <LI>that the CodeMLCompiler will parse/understand the mark comments,
 *       and collect (but not parse) any code not within in a mark. 
 *       (This allows the CodeMLCompiler/CodeMLParser to be used to parse 
 *       code in many languages while also does not interfere with the "target"
 *       language compiler ( Javac, GCC, etc.)
 *    </UL>    
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum CodeML
{
	;
	
	/**
	 * Compiles a {@code Dom}  from the String Markup using CodeMLCompiler/Parser
	 * @param markup textual representation of the document
	 * @return a {@code Dom} containing the {@code Mark}s and text.
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Dom compile( String markup )
		throws MarkupException	
	{
		return CodeMLCompiler.fromString( markup );
	}

	/**
	 * Compiles a {@code Dom} from the MarkupStream using CodeMLCompiler/Parser
	 * @param markupStream an input stream and Metadata about the Stream
	 * @return a {@code Dom} containing the {@code Mark}s and text.
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Dom compile( MarkupStream markupStream )
		throws MarkupException	
	{
		return CodeMLCompiler.fromMarkupStream( markupStream );
	}

	/**
	 * 
	 * @param inputStream
	 * @return a {@code Dom} containing the {@code Mark}s and text.
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Dom compile( InputStream inputStream )
		throws MarkupException	
	{
		return CodeMLCompiler.fromInputStream( inputStream );
	}
	
	/**
	 * Parses a single Mark from the {@code MarkText} 
	 * @param markText textual representation of the {@code Mark}
	 * @return {@code Mark} Dom-based Object representation of the Mark
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Mark parseMark( String markText )
		throws MarkupException
	{
		return parseMark( new VarContext(), markText );
	}

	/**
	 * Parses a single Mark from the {@code MarkText} 
	 * @param varContext context with which to parse the Mark
	 * (NOTE: Some Marks are Statically Derived at Parse-Time and need access to
	 * {@code VarScripts}, or Variables, for instance I might have a statically
	 * defined variable CIRCUMFERENCE which is equal to "PI * R * 2",  To parse
	 * and create this Mark, it expects the VarContext to be able to resolve 
	 * the variables {"PI" ,"R"} (at Parse-Time).   
	 * @param markText textual representation of the {@code Mark}
	 * @return {@code Mark} Dom-based Object representation of the Mark
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Mark parseMark( VarContext context, String markText )
		throws MarkupException
	{
		return parseMark( context, markText, -1 );
	}

	/**
	 * Parses a single Mark from the {@code MarkText} 
	 * @param varContext context with which to parse the Mark
	 * (NOTE: Some Marks are Statically Derived at Parse-Time and need access to
	 * {@code VarScripts}, or Variables, for instance I might have a statically
	 * defined variable CIRCUMFERENCE which is equal to "PI * R * 2",  To parse
	 * and create this Mark, it expects the VarContext to be able to resolve 
	 * the variables {"PI" ,"R"} (at Parse-Time). 
	 * @param markText textual representation of the {@code Mark}
	 * @param lineNumber the lineNumber where the mark occurs within the Markup
	 * @param nameAudit verifies Mark names are valid. 
	 * @return {@code Mark} Dom-based Object representation of the Mark
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Mark parseMark(  
		VarContext vc,
		String markText, 
		int lineNumber )
		throws MarkupException
	{
		return CodeMLParser.INSTANCE.parseMark( vc, markText, lineNumber );
	}

}
