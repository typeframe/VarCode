package io.varcode.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.varcode.VarException;

/**
 * Evaluates String expressions using Java's built in "JavaScript"
 * expression library.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum ExpressionEvaluator_JavaScript
    implements ExpressionEvaluator
{
    INSTANCE;
	  
    private AtomicBoolean isLoaded = new AtomicBoolean( false );
    
    private ScriptEngineManager scriptManager = null; 
    
    private ScriptEngine JSEngine = null;
                   
    private ExpressionEvaluator_JavaScript()
    { }
    
    /**
     * Added this Lazy load, since it was taking 3+ seconds to bootstrap 
     * Nashorn engine, and (quite frankly) we arent using the 
     * Javascript engine THAT much (so lets only load it Lazily when needed)
     * (After bootstrap everything works fine, anyways) 
     */
    private synchronized void loadLazily()
    {
        scriptManager = new ScriptEngineManager();
        JSEngine = scriptManager.getEngineByName( "JavaScript" );
        isLoaded.set( true );
    }
    
	@Override
	public Object evaluate( VarContext context, String expressionText ) 
		throws EvalException 
	{
		return evaluate( context.getScopeBindings(), expressionText );
	}
	
    @Override
    public Object evaluate( Bindings bindings, String expression )
    	throws VarException
    {
        if( ! isLoaded.get() )
        {
            loadLazily();
        }
        try
        {
            return JSEngine.eval( expression, bindings );
        }
        catch( ScriptException e )
        {
        	if( e.getCause() instanceof VarException )
        	{
        		throw (VarException)e.getCause();
        	}
        	else
        	{
        		throw new EvalException( e.getCause() );
        	}         	          
        }        
    }

	public String getName() 
	{
		return "JavaScript_ExpressionEvaluator";
	}

	public String getVersion() 
	{
		return "0.2";
	}

	public String toString()
	{
		return this.getName() + "." + getVersion();
	}
	
	public static final Set<String>RESERVED_WORDS = new HashSet<String>();
	static
	{
		String[] reserved = {"abstract","else","instanceof","super", 
		"boolean","enum","int","switch",  
		"break","export","interface","synchronized",  
		"byte","extends","let","this",  
		"case","false","long","throw",  
		"catch","final","native","throws",  
		"char","finally","new","transient",  
		"class","float","null","true",  
		"const","for","package","try",  
		"continue","function","private","typeof",  
		"debugger","goto","protected","var",  
		"default","if","public","void",  
		"delete","implements","return", "volatile",  
		"do","import","short","while",  
		"double","in","static","with",
		"alert","frames","outerHeight",
		"all","frameRate","outerWidth","anchor","function","packages","anchors","getClass","pageXOffset",
		"area","hasOwnProperty","pageYOffset",
		"Array","hidden","parent",
		"assign","history","parseFloat",
		"blur","image","parseInt",
		"button","images","password",
		"checkbox","Infinity","pkcs11",
		"clearInterval","isFinite","plugin",
		"clearTimeout","isNaN","prompt",
		"clientInformation","isPrototypeOf","propertyIsEnum",
		"close","java","prototype",
		"closed","JavaArray","radio",
		"confirm","JavaClass","reset",
		"constructor","JavaObject","screenX",
		"crypto","JavaPackage","screenY",
		"Date","innerHeight","scroll",
		"decodeURI","innerWidth","secure",
		"decodeURIComponent","layer","select",
		"defaultStatus","layers","self",
		"document","length","setInterval",
		"element","link","setTimeout",
		"elements","location","status",
		"embed","Math","String",
		"embeds","mimeTypes","submit",
		"encodeURI",
		"encodeURIComponent","NaN","text",
		"escape","navigate","textarea",
		"eval","navigator","top",
		"event","Number","toString",
		"fileUpload","Object","undefined",
		"focus","offscreenBuffering","unescape",
		"form","open","untaint",
		"forms","opener","valueOf",
		"frame","option","window",
		"onbeforeunload","ondragdrop","onkeyup","onmouseover",
		"onblur","onerror","onload","onmouseup",
		"ondragdrop","onfocus","onmousedown","onreset",
		"onclick","onkeydown","onmousemove","onsubmit",
		"oncontextmenu","onkeypress","onmouseout","onunload" };
		
		RESERVED_WORDS.addAll( Arrays.asList( reserved ) );		
	}
	
	@Override
	public boolean isReservedWord( String name ) 
	{
		return RESERVED_WORDS.contains( name );	
	}


}
