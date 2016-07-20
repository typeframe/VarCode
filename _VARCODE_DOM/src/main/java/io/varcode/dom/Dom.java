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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.varcode.Lang;
import io.varcode.Metadata;
import io.varcode.VarException;
import io.varcode.context.VarBindings;
import io.varcode.context.VarContext;
import io.varcode.dom.FillInTheBlanks.FillTemplate;
import io.varcode.dom.form.Form;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasForm;
import io.varcode.dom.mark.Mark.HasVars;
import io.varcode.dom.mark.TailorDirective;
import io.varcode.tailor.Directive;

/**
 * <A HREF="https://en.wikipedia.org/wiki/Document_Object_Model">Document Object Model</A>-
 * like model from the compiled (<CODE>BindML, CodeML</CODE>) Markup. It provides an API 
 * consisting of {@code Mark}s for manipulating the text of a Document.
 * <UL>
 *   <LI>BindML is a Markup Language for binding structured text 
 *   <CODE>"{+type+} {+name+}={+value+};"</CODE>
 *     
 *   <LI>CodeML is a Markup Language that "hides" Marks within comments of
 *       source code of languages (Java, Javascript, C, C++, C#, D, F, ...)
 *       ...(using /&#42; &#42;/) For example: 
 *       <PRE>"public class /&#42;{+className&#42;/_Clazz/&#42;+}&#42;/ {}";</PRE>
 *       NOTE:
 *       <UL>
 *       <LI>the Javac Compiler disregards the Mark-comments, and can compile and 
 *       create a class (that contains mark-comments)
 *       <LI>that the CodeMLCompiler will parse/understand the mark comments,
 *       while disregarding any code not contained in a mark. 
 *       (This allows the CodeMLCompiler/CodeMLParser to be used to parse 
 *       code in many languages while also does not interfere with the "target"
 *       language compiler ( Javac, GCC, etc.) 
 * </UL>  
 * <A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup Language</A>.
 * <BR><BR>
 * 
 * Contains the static text (Code) along with {@code Mark}s 
 * to Specialize / Tailor new source code. 
 * <PRE><CODE>
 * Dom dom = BindML.compile(
 *     "public class {+className+} {}" );
 * 
 * String tailored = Tailor.code( dom, 
 *     VarContext.of( 
 *         "className", "MyClass" ) ); // = "public class MyClass {}";
 *           
 * Dom dom = CodeML.compile( 
 *     "public class /*{+className* /className/*}* /{ }" ); 
 * 
 * String tailored = Tailor.code( dom, 
 *     VarContext.of( 
 *         "className", "MyClass" ) ); // = "public class MyClass {}";
 * 
 * </PRE>
 */
public class Dom 
    implements FillDom
{	
    public static final String MARKUP_STREAM = 
    	Metadata.PropertyLabel.of( Metadata.MARKUP, "inputstream" ); //"markup.inputstream";
    
    /** Which markup Language is the Stream contain ("BindML"? "CodeML"?, ...) */
    public static final String MARKUP_LANGUAGE = 
    	Metadata.PropertyLabel.of( Metadata.MARKUP, "language" ); //"markup.language";
    
    public static final String MARKUP_ID = 
    	Metadata.PropertyLabel.of( Metadata.MARKUP, "id" ); //"markup.id";
    
    public static final String LANG = 
    	Metadata.PropertyLabel.of( "lang" ); //"lang";
    
    /** The Time (in milliseconds) that the DOM was compiled from MarkupStream */
    public static final String DOM_COMPILE_TIMESTAMP = 
    	Metadata.PropertyLabel.of( "dom", "compile", "timestamp" ); //"dom.compile.timestamp";
    
	/** the Language of the Code (nullable) */
	private final Lang language;
	
    /** Metadata about the Var Source (file) that originated this instance */
    private final Metadata metadata;
    
	/** ALL {@code MarkAction}s on the document */
	private final Mark[] allMarks;  
	
	/** 
	 * Static Text and "blanks" where ALL {@code MarkActions}s occur
	 * (Useful if we want to "derive" the original markup text  
	 * {@code MarkAction}s) 
	 */ 	 
	public final FillTemplate allMarksTemplate;
	
    /**
     * The character locations of *ALL* {@code Mark}s within the {@code Dom}. 
     * (even {@code MarkAction}s that are not written to the tailored code)
     * 
     * These {@code Mark}s: 
     * <UL>
     *   <LI>{@code CutComment} 
     *   <LI>{@code CutJavaDocComment}
     *   <LI>{@code CutCode}
     *   <LI>{@code DefineVar} 
     * </UL>
     * 
     * ...Are not "Bound" into the "Tailored" document, 
     * (and not Modeled within a {@code FillInTheBlanks.FillTemplate} ) 
     * we keep track of the character index (within the varcode) 
     * where these {@code Mark}s occurred.
     */
    private final BitSet allMarkIndicies;
     
	/**
	 * All Text and {@code MarkAction}s that Fill Blanks (with text)
	 * as they appear in the var source to be populated when tailoring 
	 * i.e.
	 * <PRE>
	 * I _____________________, do solemnly swear to tell the truth. 
	 *        (fullName)
	 * </PRE>    
	 * 
	 * ...where "fullName" is the {@code BindMark}s' name".<BR> 
	 * 
	 * NOTE: MAY CONTAIN DUPLICATE NAMES, for instance, given the sequence:
	 * <PRE>
	 * _________, is the number of the counting, again the number is _______.
	 *   count                                                        count
	 * </PRE>
	 * the name "count" appears (2) times at [0] and at [1].
	 */
	private final Mark.BlankFiller[] allBlankFillers;
	
	/** static text and "blanks" corresponding to the {@code FillAction}s */
	private final FillTemplate allBlankFillersTemplate;
	
	/** Statically bound Vars, Forms, Scripts for this Markup */
	private final VarBindings staticBindings;
	
	/**
	 * Creates a {@code Dom} containing {@code Mark}s and text 
	 * 
	 * <UL>
	 *  <LI>{@code AddVar}
	 *  <LI>{@code AddScriptResult}
     *  <LI>{@code ReplaceWithScriptResult}
     *  <LI>{@code IfAdd}
     *  <LI>{@code IfAddWithForm}
	 *  <LI>{@code Replace}
	 *  <LI>{@code ReplaceWithForm}
	 * </UL>   
	 * 
	 * (Other {@code Mark}s like {@code Cut}, {@code CutComment} , 
	 * {@code CutJavaDoc} contain information that is not included
	 * in the tailored source. 
	 * 
	 * @param fillTemplate {@code FillInTheBlanks.FillOrder} static text and 
	 * blanks in the document
	 * @param allMarksSequence the marks that occur within the document (in order)
	 * @param allMarkLocations the set bits marking the character index of Marks within the text
	 * @param staticBindings statically defined vars, forms, scripts for the Markup
	 * @param metadata metadata about the 
	 */
	public Dom(
		Lang language, 	
	    FillInTheBlanks.FillTemplate fillTemplate, 
	    Mark[] allMarksSequence,
	    BitSet allMarkLocations, 
	    VarBindings staticBindings,
	    Metadata metadata )
	{
		this.language = language;
		this.allBlankFillersTemplate = fillTemplate;
		this.allMarks = allMarksSequence;
		this.allMarkIndicies = allMarkLocations;
		this.metadata = metadata;
		this.staticBindings = staticBindings;
		this.allMarksTemplate = 
		    FillTemplate.of( 
		    		allBlankFillersTemplate.getStaticText(), 
		            allMarkIndicies );
		             
		List<BlankFiller> embeddedMarkSequence = new ArrayList<BlankFiller>();
		for( int i = 0; i < allMarksSequence.length; i++ )
		{
			if( ( allMarksSequence[ i ] instanceof BlankFiller ) )
			{
				embeddedMarkSequence.add( (BlankFiller)allMarksSequence[ i ] );
			}
		}
		this.allBlankFillers = embeddedMarkSequence.toArray( 
		    new Mark.BlankFiller[ embeddedMarkSequence.size() ] );
	}

	/** get Metadata about the source */
	public Metadata getMetadata()
	{
	    return metadata;
	}
	
	@Override
    public Mark[] getAllMarks()
	{
	    return allMarks;
	}
	
	@Override
    public BlankFiller[] getBlankFillers()
	{
	    return allBlankFillers;
	}
	
	@Override
    public int getBlanksCount()
	{	    
	    return allBlankFillersTemplate.getBlanksCount();
	}
	
	/**
	 * Contains any statically defined values for the Markup:
	 * <UL>
	 *   <LI>/ *{##name:value##}* /
	 *   <LI>/ *{{##dateFormat:{+year+}-{+month+}-{+day+}##}}* /
	 * </UL>     
	 * @return
	 */
	public VarBindings getStaticBindings()
	{
		return this.staticBindings;
	}
	
	public Form[] getForms()
	{
	    List<Form> theForms = new ArrayList<Form>(); 
        for( int i = 0; i < this.allMarks.length; i++ )
        {
            if( allMarks[ i ] instanceof Mark.HasForm)
            {
                HasForm rf = (HasForm)allMarks[ i ];
                
                theForms.add( rf.getForm() );
            }
        }
        return theForms.toArray( new Form[ 0 ] );
	}
	
	/* (non-Javadoc)
     * @see io.varcode.VarCodeMark#getAllMarkIndexes()
     */
	@Override
    public BitSet getAllMarkIndicies()
	{
	    return this.allMarkIndicies;
	}
	
	public Set<String> getAllVarNames( VarContext context )
	{   //set to keep track of duplicates
		Set<String> varNames = new HashSet<String>();
		
		for( int i = 0; i < allMarks.length; i++ )
		{
		    if( allMarks[ i ] instanceof Mark.HasVars )
		    {
		        HasVars hv = (HasVars)allMarks[ i ];
		        varNames.addAll( hv.getAllVarNames( context ) );
		    }
		}
		return varNames;
	}
	    
	/* (non-Javadoc)
     * @see io.varcode.VarCodeMark#getFillBlanks()
     */
	@Override
    public FillTemplate getFillTemplate()
	{
	    return allBlankFillersTemplate;
	}
	
    /**
     * Gets the "Original" Markup Text (including the {@code Mark}s).
     * That was parsed to create the {@code Dom}
     * 
     * @return A Markup
     */
    public String getMarkupText()
    {
         String[] markFills = new String[ this.allMarkIndicies.cardinality() ];
         for( int i = 0; i < markFills.length; i++ )
         {
             markFills[ i ] = allMarks[ i ].getText();
         }
         return this.allMarksTemplate.fill( (Object[])markFills );         
    }
    
	public String toString()
	{
	    return getMarkupText() + System.lineSeparator()
	      + "/*{- SUMMARY " + System.lineSeparator() 
	      + "  MARKS  : (" + allMarks.length + ")" + System.lineSeparator()          
          + "  BLANKS : (" + allBlankFillers.length+")" + System.lineSeparator()
	      + "-}*/";
	}

	public Lang getLanguage()
	{
		return language;
	}
	
	/**
	 * Looks through all of the {@code Mark}s to find any {@code TailorDirective}s
	 * 
	 * @param context the context containing the {@code Directive} implementations
	 * for {@code TailorDirective} Marks referencing them by name.
	 *  
	 * @return all Directives
	 */
	public Directive[] getDirectives( VarContext context )
	{
	    List<Directive> directives = 
	        new ArrayList<Directive>();
	    
	    for( int i = 0; i < allMarks.length; i++ )
	    {   
	    	if( allMarks[ i ] instanceof TailorDirective )
	        {
	    		TailorDirective directiveMark = (TailorDirective)allMarks[ i ];	
	    		Directive d = context.getDirective( directiveMark.getName() );
	    		if( d == null )
	    		{
	    			throw new VarException( 
	    			   "Could not find Directive by name \"" + directiveMark.getName() + "\"" 
	    		      +" for Mark : " + System.lineSeparator() + 
	    		      directiveMark.getText() + System.lineSeparator() +
	    		      "on line [" + directiveMark.getLineNumber() + "]"  );
	    		}
	    		directives.add( d );
	        }
	    }
	    return directives.toArray( new Directive[ 0 ] );
	}
}