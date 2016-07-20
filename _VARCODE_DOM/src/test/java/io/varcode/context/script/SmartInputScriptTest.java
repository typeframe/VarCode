package io.varcode.context.script;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript.SmartInputScript;
import junit.framework.TestCase;

public class SmartInputScriptTest
	extends TestCase
{
	public static class MySmartInputScript
		extends SmartInputScript
	{
		@Override
		public Object eval( VarContext context, String input ) 
		{
			return this.getInputParser().parse( context, input );
		}
		
	}

	public void testWithParameterList()
	{
		MySmartInputScript msic = new MySmartInputScript();
		assertEquals( "EXPECTED", 
			msic.eval( 
				VarContext.of( 
					"A", "EXPECTED" ), "{+A+}" ) );
		
		assertEquals( 
			"EXPECTED, B, LASTONE", 
				msic.eval( 
					VarContext.of( 
						"A", "EXPECTED", 
						"C", "LASTONE" ), 
			"{+A+}, B, {+C+}" ) );
		
		assertEquals( 
			"EXPECTED, B, 1", 
				msic.eval( 
					VarContext.of( 
						"A", "EXPECTED"), 
			"{+A+}, B, {+$#(A)+}" ) );
	}
	
	public void testWithChainedScript()
	{
		MySmartInputScript msic = new MySmartInputScript();
		assertEquals( "\"1\"", 
			msic.eval( 
				VarContext.of( "A", 1 ), 
			"{+$quote($count(a))" ) );
	}
}
