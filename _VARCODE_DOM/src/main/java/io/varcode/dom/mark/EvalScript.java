package io.varcode.dom.mark;

import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.EvalException;
import io.varcode.context.ResultRequiredButNull;
import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.dom.mark.Mark.Derived;
import io.varcode.dom.mark.Mark.HasScript;
import io.varcode.dom.mark.Mark.MayBeRequired;

/**
 * Calls a script with some input
 * <B>Typically used for context / input validation/ assertion </B> 
 * (will "fail early" if any of the validation routines throws an exception) 
 * 
 * RunScript should be IMMUTABLE, and IDEMPOTENT with NO SIDE EFFECTS.
 *  
 * THIS DOES NOT: 
 * <UL>
 *   <LI>"write" anything to the tailored code
 *   <LI> modify the context (any vars / forms)
 * </UL>
 * 
 * Why is RunScript Useful:
 * <UL>
 * <LI>It can signal to the outside world (so technically this isn't strictly immutable)
 * <LI>it can "FAIL", elegantly which is a good way of performing input validation
 * <LI>it can "print out" the state of a Var, the VarContext / Bindings / Etc." (debugging) 
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class EvalScript
    extends Mark
    implements HasScript, Derived, MayBeRequired
{
    /** the name of the script to eval*/
    private final String scriptName;
    
    /** input to the script*/
    private final String scriptInput;
    
    private final boolean isRequired;
    
    /*{$maxCount(fieldName,8)}*/
    /*{$maxCount(fieldName,8)*}*/ //REQUIRED
    public EvalScript( 
    	String text, 
    	int lineNumber, 
    	String scriptName, 
    	String scriptInput, 
    	boolean isRequired )
    {
        super( text, lineNumber );
        this.scriptName = scriptName;
        this.scriptInput = scriptInput;
        this.isRequired = isRequired;
    }

    @Override
    public Object derive( VarContext context )
    {
        VarScript theScript = context.resolveScript( scriptName, scriptInput);
        if( theScript != null )
        {
        	Object result = null;
            try
            {
            	//System.out.println( "Evaluating SCRIPT" );
                result = theScript.eval( context, scriptInput );               
            }
            catch( Exception e )
            {
            	if( e  instanceof VarException )
            	{
            		throw e;
            	}
                throw new EvalException( 
                    "Script \"" + scriptName + "\" for mark :" + N + text + N
                   +" on line [" + lineNumber + "] could not be evaluated", e );
            }
            if( isRequired && result == null )
            {
            	throw new ResultRequiredButNull( scriptName, scriptInput, text, lineNumber );
                   // "Required Script \"" + scriptName + "\" for mark :" + N + text + N
                  //+ " on line [" + lineNumber + "] returned null ( failed )" ); 
            }
            return result;
        }
        if( isRequired )
        {
        	throw new EvalException(
        		"No script named \"" + scriptName + "\" found for mark: " + N + text 
        		+ N + "on line [" + lineNumber + "]" );
        }
        return null;
    }

    @Override
    public String getScriptName()
    {
        return scriptName;
    }

	@Override
	public boolean isRequired() 
	{
		return isRequired;
	}

	@Override
	public Set<String> getAllVarNames( VarContext context ) 
	{
		return null;
	}

	@Override
	public String getScriptInput() 
	{
		return scriptInput;
	}
}
