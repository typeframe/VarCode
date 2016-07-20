package io.varcode.dom.mark;

import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.dom.DomParseState;
import io.varcode.dom.ParseState;
import io.varcode.dom.form.Form;
import io.varcode.text.TextBuffer;

/** 
 * "Action" associated with a {@code Mark} within Markup file / {@Dom}.
 * 
 * {@code Mark}s provide ways of manipulating the {@code Dom} to 
 * produce Text
 * 
 * {@code Mark}s are like a <A HREF="https://en.wikipedia.org/wiki/Scriptlet">scriptlet</A> 
 * tag in a JSPs, but they DO NOT ENFORCE Syntax. ( 
 * <P>
 * --We use the term "Mark" to imply more of a 
 * <A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup</A>
 * nature of the ( Marks, may "Wrap" and translate code embedded within).
 * </P>
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class Mark
{
    protected static final String N = System.lineSeparator();
    
    /** text representation the {@code Mark} i.e. "{+name+}" */
    public final String text;
    
    /** the line number within the {@code Dom}*/ 
    public final int lineNumber;
    
    public Mark( String text, int lineNumber )
    {
        this.text = text;
        this.lineNumber = lineNumber;
    }

    public String getText()
    {
        return text;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }
	
    /** 
     * When the {@code Markup} is being parsed/compiled, and this mark fully 
     * realized the Mark will need to read and/or update the {@code Markup.MarkupState} 
     */
    public interface ParseStateAware
    {
        /** reads and/or updates the {@code ParseState} during Parsing / Compilation */ 
        public void onMarkParsed( ParseState parseState );
    }
    
    /** Derives value(s) and updates the context with new key-value binding(s) */
    public interface Bind
    {
        public void bind( VarContext context )
           throws VarException;                 
    }
    
    /** Gets the names of all Vars that are Referenced*/
    public interface HasVars
    {
        public Set<String> getAllVarNames( VarContext context );
    }
    
    /** A Mark whose "value" is derived / evaluated using the {@code Context} */
    public interface Derived
    {       
        /** Derive the result given the {@code context} and return it*/
        public Object derive( VarContext context )
            throws VarException;        
    }
    
    /**
     * Mark that is Derives a static Var value at "Compile Time".
     * 
     * NOTE: DerivedDynamically has the SAME API, but this Interface
     * signifies WHICH COMPONENT (the Compiler) does the derivation 
     * and WHEN (at Compile-Time) the Derivation operation is performed.
     * 
     * This is important from a "Dependency Management Perspective"
     * 
     * for example:
     * <P>
     * If I wanted to create an immutable/Larval static Object instance that 
     * contained a finite list of elements organized by using a 
     * Minimal Perfect Hash function, I might require the 
     * Compiler/CompileContext to pass all the elements into 
     * a Minimal Perfect Hash Function Generator, (which generates the code
     * for a minimal perfect hash function for these specific elements) 
     * (when I compile the Entity)
     * I can then arrange the elements by the order of the function, write the 
     * function inside the class and (when I tailor the code) The Tailor
     * doesnt need to have access to the Minimal Perfect Hash Function Generator
     * (I derived the code statically by the compiler)   
     * </P>
     * 
     * So, to recap, We <I>generated</I> a Minimal Perfect Hash Function at
     * <I>compile-time</I> and used the generated code for the MPH Function
     * at <I>Tailor-Time</I> to create "tailored" source code.
     * 
     * If the "tailored" source code is compiled and run, IT DOESNT NED TO KNOW
     * Anything about Minimal Perfect Has Code Generators (and it doesnt have to 
     * include any dependencies that understand generating Minimal Perfect Hash 
     * Functions) as the precalculated functions are already <B>"baked" into the 
     * source code.</B>
     */
    public interface BoundStatically
    {     
    	public void onMarkParsed( DomParseState parseState );
    }
    
    /**
     * Mark that is Derives a Var value in the Context at "Tailor Time" 
     * 
     * NOTE: DerivedStatically has the SAME API, but this Interface
     * signifies WHICH COMPONENT (Tailor) does the derivation and WHEN 
     * (During "Tailor" Time) 
     */
    public interface BoundDynamically
        extends Derived, Bind
    {        
    }
    
    /** 
     * Mark that is derived from a {@code Form} that can be bound
     * 
     * @see ReplaceWithForm
     * @see AddFormIfVar
     */
    public interface HasForm
        extends Derived
    {        
        /** the Form (either a {@code VarForm} of {@code StaticForm} for the Content*/
        public Form getForm();        
    }
    
    /** Does the mark have or use and expression? */
    public interface HasExpression
    {
    	public String getExpression();
    }
    
    /** Does this mark action use a bound {@code VarScript}?
     * 
     * @see AddScriptResult
     * @see DefineVarAsScriptResult
     * @see ReplaceWithScriptResult
     */ 
    public interface HasScript
    	extends HasVars
    {
    	/** gets the name bound to the script */
        public String getScriptName();
        
        /** gets the input to the Script */
        public String getScriptInput();        
    }
    
    /**
     * Marks that intend on removing content between tags
     * 
     * @see Cut
     * @see CutComment
     * @see CutJavaDoc
     */
    public interface RemovesText
    {
    	/**
    	 * gets the content that is wrapped within the tags
    	 * That is to be removed when tailoring the result
    	 */
        public String getBetweenText();
    }
    
    /** 
     * Some Marks wrap code, meaning the "tag" will surround some content
     * that will be cut, or replaced when the markup is tailored.
     * for example:<PRE><CODE>
     * /&#42;{+replace&#42;/code/&#42;+}&#42;/
     * </PRE></CODE>
     * 
     * above, "code" is the wrapped content (within the {+replace} tag).
     * 
     * @see ReplaceWithVar
     * @see ReplaceWithForm
     * @see ReplaceWithScriptResult
     */
    public interface WrapsContent
    {
        /** 
         * Gets the content that is wrapped within the tags
         * 
         * Sometimes this content can be used as input to a transformation 
         * script 
         */
        public String getWrappedContent();
    }
    
    /** 
     * Fills text into a predefined blank when Tailoring {@code Markup}.<BR><BR>
     * 
     * it is MarkupStateAware, because (while parsing the Marks from the source)
     * it needs to know where (within the Tailored output code) it needs to start 
     * writing content/filling in the blank). 
     */
    public interface BlankFiller
        extends ParseStateAware, Derived
    {   
        /** Populate a "blank" with derived value */
        public void fill( VarContext context, TextBuffer buffer );        
    }
    
    /** 
     * Some mark types have the ability to be "required" meaning a non-null
     * value must be supplied for Tailoring to take place (otherwise failure)
     */
    public interface MayBeRequired
    {    	
        public boolean isRequired();        
    }
    
    public interface IsNamed
    {
        public String getVarName();
    }
}
