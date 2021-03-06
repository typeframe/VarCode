package io.varcode.dom.mark;

import io.varcode.context.EvalException;
import io.varcode.context.VarContext;
import io.varcode.dom.mark.AddExpressionResult;
import io.varcode.dom.mark.EvalExpression;
import junit.framework.TestCase;

public class EvalExpressionTest
	extends TestCase
{
	public void testSimple()
	{
		String expression = "3+4";
		String bindMLMark = "{((" + expression + "))}";
		
		EvalExpression ee = new EvalExpression( bindMLMark, 0, expression );
		
		assertEquals( expression, ee.getExpression() );
		assertEquals( bindMLMark, ee.getText());
		assertEquals( 0, ee.getLineNumber() );
		
		assertEquals( null, ee.derive( new VarContext() ) );
	}
	
	public void testBadExpression()
	{
		//we force a failure b/c "A" is not bound
		String expression = "A + 3 / 0";
		String bindMLMark = "{((" + expression + "))}";
		
		EvalExpression ee = new EvalExpression( bindMLMark, 0, expression );
		
		assertEquals( expression, ee.getExpression() );
		assertEquals( bindMLMark, ee.getText());
		assertEquals( 0, ee.getLineNumber() );
		
		try
		{
			ee.derive( new VarContext() );
			fail("Expected exception for bad Expression ");
		}
		catch( EvalException e )
		{
			//it should throw THIS exception (not some other exception)
			//t.printStackTrace( );
		}
	}
	
	public void testLoadFunctionAsVar()
	{
		VarContext vc = new VarContext();
		
		//we can load MULTIPLE var-functions in a single MARK
		String expression = 
			"var fun1 = function() { return 'HEY'; }" + System.lineSeparator()
		   +"var fun2 = function(a) { return 'HEY' + a;}";
		
		String bindMLMark = "{((" + expression + "))}";
		
		EvalExpression ee = new EvalExpression( bindMLMark, 0, expression );
		
		assertEquals( expression, ee.getExpression() );
		assertEquals( bindMLMark, ee.getText());
		assertEquals( 0, ee.getLineNumber() );
		
		ee.derive( vc );
				
		//ok, now lets actually CALL the function (the var fun1() I defined previously)
		expression = "fun1()";
		bindMLMark = "{+((" + expression + "))+}";
		AddExpressionResult aer = new AddExpressionResult( bindMLMark, 0, expression );
		assertEquals( "HEY", aer.derive( vc ) );
		
		expression = "fun2('eric')"; //pass in a literal to a function
		bindMLMark = "{+((" + expression + "))+}";
		aer = new AddExpressionResult( bindMLMark, 0, expression );
		assertEquals( "HEYeric", aer.derive( vc ) );
		
		vc.set( "name", "Eric" );		
		expression = "fun2(name)"; //pass in a var to a function
		bindMLMark = "{+((" + expression + "))+}";
		aer = new AddExpressionResult( bindMLMark, 0, expression );
		assertEquals( "HEYEric", aer.derive( vc ) );		
	}
}
