package io.varcode.dom.mark;

import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.dom.DomParseState;
import io.varcode.dom.form.Form;
import io.varcode.dom.mark.Mark.Derived;
import io.varcode.dom.mark.Mark.HasForm;
import io.varcode.dom.mark.Mark.HasVars;
import io.varcode.dom.mark.Mark.IsNamed;

/*{{#className*:IntFrameBoxOf{+fieldCount}}}*/
/*{{#FieldParams...*:IntFieldBox {+FieldName...}, }}*/
/*{{#Params...*:{+FieldName}, }}*/
/*{{#params...*:{+fieldName}, }}*/

/**
 * Derives a "Form" that is assigned to a name
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class DefineVarAsForm
    extends Mark
    implements IsNamed, HasForm, HasVars, Derived
{       
    public final boolean isRequired;      
    public final String varName;
    public final Form form;
    
    public DefineVarAsForm(
        String text, 
        int lineNumber,
        String varName,    
        Form form,        
        boolean isRequired)
    {
        super( text, lineNumber );
        this.varName = varName;
        this.form = form;    
        this.isRequired = isRequired;
    }
    

    public String getVarName()
    {
        return varName;
    }

    public Form getForm()
    {
        return form;
    }
    
    public String derive( VarContext context )
    {
        try
        {
            return form.derive( context );            
        }
        catch( Exception cme )
        {
            throw new VarException (
                "Unable to derive DefineVarAsForm \"" + varName + "\" for mark "
              + N + text + N +" on line [" + lineNumber + "]", cme );
        }        
    }
  
    @Override
    public Set<String> getAllVarNames( VarContext context )
    {
    	return form.getAllVarNames( context );
    }
    
    public static final class InstanceVar
        extends DefineVarAsForm
        implements BoundDynamically, Bind
    {
        public InstanceVar( 
            String text, 
            int lineNumber, 
            String name, 
            Form form,
            boolean isRequired )
        {
            super( text, lineNumber, name, form, isRequired );
        }
        
        public void bind( VarContext context )
            throws VarException
        {
            String derived = derive( context );
            if( derived != null && derived.length() > 0 )
            {   //we only set things if they are non-null 
            	context.set( varName, derived, VarScope.INSTANCE );
            }
        }
    }
    
    public static final class StaticVar
        extends DefineVarAsForm
        implements BoundStatically
    {
        public StaticVar(
            String text, 
            int lineNumber, 
            String name, 
            Form form )
        {
            super( text, lineNumber, name, form, true );
        }
        
        public StaticVar( 
            String text, 
            int lineNumber, 
            String name, 
            Form form,
            boolean isRequired)
        {
            super( text, lineNumber, name, form, isRequired );
        }
        
        @Override
        public void onMarkParsed( DomParseState parseState )
        {
        	 String derived = derive( parseState.getParseContext() );
             if( derived != null && derived.length() > 0 )
             {   //we only set things if they are non-null 
            	 parseState.setStaticVar( varName, derived );
             }  
        }
        
        public String toString()
        {
            return "STATIC "+ varName+" : {{"+form.getText()+"}}";
        }
    }
}
