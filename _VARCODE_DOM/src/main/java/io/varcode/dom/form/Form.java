package io.varcode.dom.form;

import java.util.Collections;
import java.util.Set;

import io.varcode.context.VarContext;
import io.varcode.dom.mark.Mark;

/**
 * Abstracts over Static and dynamic Forms (Text)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 * 
 * @see VarForm Form containing static text wixed with variables 
 * @see StaticForm unchanging, immutable form (static String)
 * 
 */
public interface Form
{
    /** All (dependent) Var names of the Form */
    public Set<String> getAllVarNames( VarContext context );
    
    /** the (optional) name of the Form */
    public String getName();
    
    /** gets all Marks of the Form */
    public Mark[] getAllMarks();
    
    /** gets the text used to make the {@code Form} */ 
    public String getText();
    
    /** Line Number associated with the Form*/
    public int getLineNumber();
    
    /** tailor the content and return it as a String */
    public String derive( VarContext context );
    
    /** A Static Form (No variables/variability) */
    public static class StaticForm
        implements Form
    {
        public static final Set<String> NO_VARS = 
            Collections.emptySet();
        
        public final int lineNumber;        
        public final String text;
    
        public StaticForm( int lineNumber, String text )
        {
            this.lineNumber = lineNumber;
            this.text = text;
        }

        public Set<String> getAllVarNames( VarContext context )
        {
            return NO_VARS;
        }

        public int getLineNumber()
        {
            return this.lineNumber;
        }

        public String toString()
        {
            return "STATIC FORM :" + System.lineSeparator() + text;
        }

        /** Gets the form in textual form */
        public String getText()
        {
            return text;
        }

        public String getName()
        {
            return null;
        }

        @Override
        public String derive( VarContext context )
        {
            return text;
        }

        @Override
        public Mark[] getAllMarks()
        {
           return new Mark[ 0 ];
        }        
    }
}
