package io.varcode.context;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.varcode.dom.Dom;
import io.varcode.dom.bindml.BindMLCompiler;
import io.varcode.dom.form.Form;
import io.varcode.dom.form.Form.StaticForm;
import io.varcode.dom.forml.ForMLCompiler;


/**
 * Interface for a script 
 * (some functional code that accepts an input and evaluates an output)
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface VarScript
{
	/** interpreter for the input passed to the VarScript */
	public ScriptInputParser getInputParser();
	
    /** Evaluate the script given the context and input and return the result */
    public Object eval( VarContext context, String input );
    
    public static final ScriptInputParser.ChainedInputParser CHAIN_INPUT = 
    	ScriptInputParser.ChainedInputParser.INSTANCE;
    
    public static final ScriptInputParser.InputIgnored IGNORE_INPUT = 
        ScriptInputParser.InputIgnored.INSTANCE;
        
    public static final ScriptInputParser.InputString STRING_INPUT = 
        ScriptInputParser.InputString.INSTANCE;
       
    public static final ScriptInputParser.InputVarName VAR_NAME_INPUT = 
        ScriptInputParser.InputVarName.INSTANCE;

    public static final ScriptInputParser.SmartInputParser SMART_INPUT = 
            ScriptInputParser.SmartInputParser.INSTANCE;
    
    public static abstract class ChainInputScript
    	implements VarScript
    {
    	/** interpreter for the input passed to the VarScript */
    	public final ScriptInputParser getInputParser()
    	{
    		return CHAIN_INPUT;
    	}
    }
    
    /**
     * A script taking a Var Name as it's (string) input
     * (that will be resolved at "tailor-time")
     */
    public static abstract class VarNameScript
    	implements VarScript
    {
    	/** interpreter for the input passed to the VarScript */
    	public final ScriptInputParser getInputParser()
    	{
    		return VAR_NAME_INPUT;
    	}
    }
    
    public static abstract class StringInputScript
		implements VarScript
	{
    	/** interpreter for the input passed to the VarScript */
    	public final ScriptInputParser getInputParser()
    	{
    		return STRING_INPUT;
    	}
	}
    
    public static abstract class IgnoreInputScript
    	implements VarScript
    {
    	/** interpreter for the input passed to the VarScript */
    	public final ScriptInputParser getInputParser()
    	{
    		return IGNORE_INPUT;
    	}
    }
    
    public static abstract class SmartInputScript
    	implements VarScript
    {
    	/** interpreter for the input passed to the VarScript */
    	public final ScriptInputParser getInputParser()
    	{
    		return SMART_INPUT;
    	}
    }
    
    /** 
     * Each {@code VarScript} is provided with input as a String, 
     * the {@code InputInterpreter} will interpret what the String represents
     * (sometimes the input is a String literal, sometimes it is a Var bound to the
     * context, sometimes a list of 
     * 
     * @author M. Eric DeFazio eric@varcode.io
     */
    public interface ScriptInputParser
    {	
    	public Object parse( VarContext context, String scriptInput );
    	
    	public Set<String> getAllVarNames( String input ); 
    	
    	/**
         * If I have something like this:
         * {#$quote($uuid())#}
         *          $uuid()
         *           ^^^^
         *           scriptName
         * the Input Interpreter for $quote()
         * needs to realize that it must first call the uuid() script
         * and (given the result) it will THEN call quote 
         */
        public enum ChainedInputParser
        	implements ScriptInputParser
        {
    		INSTANCE;
    		
    		public Set<String> getAllVarNames( String input ) 
    		{    			
    			return Collections.emptySet();
    		}
    		
    		@Override
    		public Object parse( VarContext context, String scriptInput ) 
    		{
    		
    			if( scriptInput != null && 
    				scriptInput.startsWith( "$" ) &&    				
    				scriptInput.endsWith( ")" ) &&
    				scriptInput.indexOf( '(' ) > 0 )
    			{
    				//I first need to 
    		        // {#id:$quote($uuid())#}
    		        //             $uuid()
    				
    				//               (
    				int openIndex = scriptInput.indexOf( '(' );
    				String scriptName = scriptInput.substring( 1, openIndex ); 
    				VarScript innerScript = context.resolveScript( scriptName, scriptInput ); //( scriptName );
    				
    				
    				String innerScriptInput = 
    					scriptInput.substring( 
    						openIndex + 1,
    						scriptInput.length() -1 );
    				
    				Object innerScriptResult = 
    					innerScript.eval( context, innerScriptInput );
    				
    				return innerScriptResult;
    			}
    			return scriptInput;    			
    		}
        }
        
       
        enum SmartInputParser
        	implements ScriptInputParser
        {
        	INSTANCE;

			@Override
			public Object parse( VarContext context, String scriptInput ) 
			{
				if( scriptInput == null || scriptInput.trim().length() == 0 )
				{
					return "";
				}
				if( scriptInput.startsWith( "$" ) 
					&& scriptInput.endsWith( ")" ) )
				{
					return ChainedInputParser.INSTANCE.parse( 
						context, scriptInput );
				}
				try
				{
					Form form =  
						ForMLCompiler.INSTANCE.compile( scriptInput );
					if( form instanceof StaticForm )
					{
						Object value = context.resolveVar( scriptInput );
						if( value != null )
						{
							return value;
						}
						return scriptInput;
					}
					return form.derive( context );							
				}
				catch( Exception e )
				{
					return scriptInput;
				}
			}

			@Override
			public Set<String> getAllVarNames( String input ) 
			{
				Dom mu = 
					BindMLCompiler.fromString( input );
				return mu.getAllVarNames( new VarContext() );
			}        	        	
        }
        
    	/** the Script ignores any input passed to it*/
    	public enum InputIgnored
    		implements ScriptInputParser
    	{ 
    		INSTANCE;
    		
    		public Set<String> getAllVarNames( String input ) 
    		{
    			return Collections.emptySet();
    		}

    		@Override
    		public Object parse( VarContext context, String scriptInput ) 
    		{
    			return null;
    		}		
    	}
    	
    	/** the Script ignores any input passed to it*/
    	public enum InputString
    		implements ScriptInputParser
    	{ 
    		INSTANCE;
    		
    		public Set<String> getAllVarNames( String input ) 
    		{
    			return Collections.emptySet();
    		}

    		@Override
    		public Object parse( VarContext context, String scriptInput ) 
    		{
    			return scriptInput;
    		}		
    	}
    	
    	/** 
    	 * the scriptInput to the {@code VarScript} 
    	 * is a String that represents a var name that is bound in the 
    	 * {@code VarContext} (Statically or at TailorTime) 
    	 */
    	public enum InputVarName
    		implements ScriptInputParser
    	{
    		INSTANCE;
    		
    		public Set<String> getAllVarNames( String input ) 
    		{			
    			if( input != null && input.trim().length() > 0 )
    			{				
    				Set<String> varNames = new HashSet<String>();
    				varNames.add( input );
    				return varNames;
    			}
    			else
    			{
    				return Collections.emptySet();
    			}
    		}

    		@Override
    		public Object parse( VarContext context, String scriptInput ) 
    		{			    			
    			return context.resolveVar( scriptInput );
    		}		
    	}
    }   
}
