package io.varcode.dom.mark;

import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.EvalException;
import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.context.VarScript;
import io.varcode.dom.ParseState;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasScript;
import io.varcode.dom.mark.Mark.MayBeRequired;
import io.varcode.dom.mark.Mark.WrapsContent;
import io.varcode.text.TextBuffer;

/**
 * Replaces "Wrapped Context" with the result of calling a Script
 * @author M. Eric DeFazio eric@varcode.io
 */

/*{+$tabsToSpaces(*/
//      a bunch of code
//      that uses tabs, but could be replaced based on the Environment 
//      Settings for what a "tab" is 
/*)}*/
public class ReplaceWithScriptResult
    extends Mark
    implements BlankFiller, WrapsContent, HasScript, MayBeRequired
{    
    /** the name associated with the script */
    private final String scriptName;
    
    /** Content wrapped between the open and close tags */
    private final String wrappedContent;
    
    private final boolean isRequired;
     
    public ReplaceWithScriptResult( 
        String text, 
        int lineNumber, 
        String scriptName, 
        String wrappedContent, 
        boolean isRequired )
    {
        super( text, lineNumber );
        this.scriptName = scriptName;
        this.wrappedContent = wrappedContent;
        this.isRequired = isRequired;
    }
    
    public String getVarName()
    {
        return scriptName;
    }
    
    public void fill( VarContext context, TextBuffer buffer )
    {
        buffer.append( derive(context ) );
    }
    
    public void fillTo( VarContext context, StringBuilder out )
        throws VarException
    {
        out.append( derive( context ) );
    }
    
    public Object derive( VarContext context )
    {
        VarScript theScript = context.getVarScript( scriptName );
        if( theScript != null )
        {
            try
            {            
                return theScript.eval( context, wrappedContent );
            }
            catch( Throwable t )
            {
                throw new EvalException( 
                    "Error evaluating mark: " + N + text + N 
                  + "with script \"" + scriptName 
                  + "\" on line [" + lineNumber + "] with content :" + N 
                  + wrappedContent + N, t );
            }
        }
        throw new VarRequiredButNull( scriptName, text, lineNumber );
    }
    
    public String getWrappedContent()
    {
        return this.wrappedContent;
    }
    
    @Override
    public void onMarkParsed( ParseState parseState )
    {
    	parseState.reserveBlank();
    }

    @Override
    public String getScriptName()
    {
        return scriptName;
    }
    
	@Override
	public Set<String> getAllVarNames( VarContext context ) 
	{
		VarScript script = context.getVarScript( scriptName );
		return script.getInputParser().getAllVarNames( this.wrappedContent );
	}
	
    @Override
    public String getScriptInput()
    {
        return this.wrappedContent;
    }

	@Override
	public boolean isRequired() 
	{
		return isRequired;
	}
}   
