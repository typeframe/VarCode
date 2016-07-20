package io.varcode.context.script;

import io.varcode.context.VarBindings;
import io.varcode.context.EvalException;
import io.varcode.context.ExpressionEvaluator;
import io.varcode.context.ExpressionEvaluator_JavaScript;
import io.varcode.context.ScopeBindings;
import junit.framework.TestCase;

public class ScriptEvaluator_JavaScriptTest
    extends TestCase
{

    public void testEvalNoInput()
    {
        ExpressionEvaluator scriptEval = ExpressionEvaluator_JavaScript.INSTANCE;
        Object res = scriptEval.evaluate( 
            new ScopeBindings(), "3 + 4" );
        assertEquals( res, 7 );
        
        try
        {
            scriptEval.evaluate( new ScopeBindings(), "sdklfjasdoifu is read" );
            fail("expected Exception for Bad Script");
        }
        catch( EvalException se )
        {
            //expected
        }        
    }
    
    public void testEvalBinding()
    {
        ExpressionEvaluator scriptEval = ExpressionEvaluator_JavaScript.INSTANCE;
        VarBindings vb = new VarBindings();
        vb.put( "A", 100 );
        vb.put( "B", 500 );
        Object res = 
        	scriptEval.evaluate( vb, "(A + B) | 0" ); //use | 0 to cast float to int
        assertEquals( 600, res ); 
    }
}
