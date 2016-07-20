package io.varcode.dom.mark;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.dom.forml.ForMLParser;
import io.varcode.dom.mark.AddScriptResult;
import junit.framework.TestCase;

public class AddScriptResultTest
    extends TestCase
{
	public void testAddScriptResult()
	{
		 AddScriptResult asr = 
		     ForMLParser.AddScriptResultMark.of( 
		         "{+$quote($count(a))+}", 0 );
		assertEquals( "\"1\"", asr.derive( VarContext.of( "a", "1" ) ) );		
	}

    /**
     * Verify that if the Script Fails we get the (wrapped) exception back
     */
    public void testScriptFails()
    {
        AddScriptResult fac = 
            ForMLParser.AddScriptResultMark.of( 
                 "{+$fail(name=this   is  tab separated   )+}", 
                 0 );
        try
        {
            fac.derive( VarContext.of( "fail", new VarScript()
            {                
                public Object eval( VarContext context, String input )
                {
                    throw new RuntimeException( "I ONLY FAIL" );
                }
                
				@Override
				public ScriptInputParser getInputParser() 
				{
					return ScriptInputParser.InputIgnored.INSTANCE;
				} 
            } ) );
            fail( "Expected Exception" );
        }
        catch( Exception e )
        {
            //expected
        }
    }
    
    public void testSimple()
    {
        AddScriptResult fac = 
            ForMLParser.AddScriptResultMark.of( 
                "{+$tab(name=this   is  tab separated   )+}", 
                0 );
        assertEquals( "tab", fac.getScriptName() );
        System.out.println(  fac.getScriptInput() );
        assertEquals( "name=this   is  tab separated   ", 
            fac.getScriptInput() );
        assertEquals( fac.getScriptInput(), "name=this   is  tab separated   ");
        
    }
}
