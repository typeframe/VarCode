package io.varcode.dom.mark;

import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.context.VarScript;
import io.varcode.context.EvalException;
import io.varcode.context.ResultRequiredButNull;
import io.varcode.dom.DomParseState;
import io.varcode.dom.MarkupException;
import io.varcode.dom.mark.Mark.Derived;
import io.varcode.dom.mark.Mark.HasScript;
import io.varcode.dom.mark.Mark.IsNamed;

//TODO I should ALLOW both : and = for assignment
//these is a STATIC DEFINE Marks 
//  IMMUTABLE at "runtime"
//  derived by the Parser/Compiler

/**{#classid=$uuid()}*/
/**{#genDate:$date(yyyy-MM-dd)}*/

// --------------------------------------

//these are INSTANCE DEFINE Marks 
//   they are MUTABLE and 
//   derived by the "Tailor" Runtime which means they can use any
//   var / script that is bound at "tailor/RunTime" 

/*{#now = $date(YYYYmmDD)}*/
/*{#id = $uuid()}*/
/*{#FieldName = $firstCap(fieldName)}*/ //uses the instance var fieldName to define FieldName 

//---------------------------------


/**
 * Define a "local" var for use within the {@code Context} 
 * by evaluating a function and returning a value
 * 
 * <B>Eval</B>uate a script and <B>derive</B> a named local variable in the 
 * {@code MutableContext}.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class DefineVarAsScriptResult
    extends Mark
    implements IsNamed, HasScript, Derived
{
    /** the REQUIRED name of the var to be defined */
    public final String varName;
    
    /** 
     * the name of the script to be evaluated (separated by $)
     * so $toDate(format=YYYYmmDD)
     * <OL>
     *  <LI> calls the timestamp() script
     *  <LI> calls the toDate() script with the result of the timestamp script
     * </OL>
     * Note: The Context/parameters are named and passed to each script 
     * (each script "queries" the context if necessary) in addition to  
     */
    public final String scriptName;
    
    /**
     * The input passed to the Script (its a String, but it can be interpreted as: 
     * <UL>
     *   <LI>a parameter/argument list
     *   <LI>key value pairs
     *   <LI>JSON, etc. 
     * </UL>   
     * depending on how the script implementation wants to accept the input
     */
    public final String scriptInput;
    
    public DefineVarAsScriptResult( 
        String text, 
        int lineNumber,
        String varName, 
        String scriptName, 
        String scriptInput )
    {
        super( text, lineNumber );
        this.varName = varName;
        this.scriptName = scriptName;
        this.scriptInput = scriptInput;
    }
    
    @Override
    public String getVarName()
    {
        return varName;
    }
    
    @Override
    public String getScriptName()
    {
        return this.scriptName;
    }
    
	@Override
	public String getScriptInput() 
	{
		return scriptInput;
	}
	
	@Override
	public Set<String> getAllVarNames( VarContext context ) 
	{
		VarScript script = context.getVarScript( scriptName );
		return script.getInputParser().getAllVarNames( scriptInput );
	}
	
    private static boolean isNonEmptyString( Object obj )
    {
    	if( obj instanceof String )
    	{
    		if( ((String)obj).length() == 0 )
    		{
    			return false;
    		}
    		return true;
    	}
    	return true;
    }
    
    public Object derive( VarContext context )
    {
        VarScript varScript = context.getVarScript( scriptName );
        if( varScript == null )
        {
            throw new VarException(
                "No script named \"" + scriptName + "\" for mark:" + N
              + text + N  + "on line[" + lineNumber + "]" );
        }
        try
        {
            return varScript.eval( context, scriptInput );
        }
        catch( Throwable t )
        {
            throw new EvalException(
                "Exception evaluating script \"" + scriptName + "\" for mark:" 
              + N + text + N  + "on line[" + lineNumber + "]", t );
        }       
    }
    
    public static final class InstanceVar
        extends DefineVarAsScriptResult
        implements Bind, BoundDynamically
    {
    	private final boolean isRequired;
    	
        public InstanceVar( 
            String text, 
            int lineNumber, 
            String varName, 
            String scriptName,
            String scriptInput,
            boolean isRequired )
        {
            super( text, lineNumber, varName, scriptName, scriptInput );
            this.isRequired = isRequired;
        }        
        
        //Bind the derived value to the context (as an instance)
        //provided it is non-null or the empty string
        public void bind( VarContext context )
        {
            Object derived = derive( context );
            if( derived == null && isRequired )
            {
            	throw new ResultRequiredButNull( 
            		scriptName, scriptInput, text, lineNumber );
            }
            if( derived != null && isNonEmptyString( derived ) )
            {
            	context.set( varName, derived, VarScope.INSTANCE );
            }
        }
                
        public String toString()
        {            
            return "Instance Var " + varName + " : " 
                + scriptName + "(" + scriptInput + ")"; 
        }
    }

    public static final class StaticVar
        extends DefineVarAsScriptResult
        implements BoundStatically 
    {
        public StaticVar( 
            String text, 
            int lineNumber, 
            String varName, 
            String scriptName,
            String scriptInput )
        {
            super( text, lineNumber, varName, scriptName, scriptInput );
        }
        
        @Override
        public void onMarkParsed( DomParseState parseState )
        {
            //Setting things Statically can only rely on OTHER statically defined 
            // variables and scripts that EXIST in the ParseState...
            
            // (we cant use ANY instance variables because we are currently 
            // PARSING/COMPILING the code (and those instance variables dont exist
            // until later (when we tailor)
            Object derived = null;
            
            try
            {
                derived = derive( parseState.getParseContext() );
                //System.out.println( "DERIVED AS " + derived );
                if( derived != null && isNonEmptyString( derived ) )
                {
                	parseState.setStaticVar( varName, derived );
                }                                  
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Unable to derive static var \"" + varName 
                  + "\" with script \"" + scriptName + "\" with input \"" 
                  + scriptInput + "\" for STATIC scope with mark: " + N + text + N + 
                  "on line [" + lineNumber + "]", t );
            }                                
        }
        
        public String toString()
        {            
            return "STATIC " + varName + " : " 
                + scriptName + "(" + scriptInput + ")"; 
        }
    }
}
