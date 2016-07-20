package io.varcode.dom.mark;

import io.varcode.VarException;
import io.varcode.context.EvalException;
import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.dom.mark.AddVarExpression;
import junit.framework.TestCase;

// {+name:((   ))+}
// {+name:((   ))*+}
// {+name:((   ))|default+}
/*{+access:((
    typeof access != 'undefined' 
    && ['public','','protected','private'].indexOf( access ) > 0 
))*+}*/

public class AddVarExpressionTest 
	extends TestCase
{	
	static final String name = "access";
	static final String EXPRESSION = 
		"['public','','protected','private'].indexOf( access ) >= 0 ";
	static final int lineNumber = 0;
	
	public void testValidMark()
	{
		String mark =
			"/*{+" + name + ":(( " + EXPRESSION + " ))"+ "+}*/";
		
		AddVarExpression ave = 
			new AddVarExpression(
			 	mark, 
			    lineNumber,
			    name, 
			    false, //boolean isRequired,
			    EXPRESSION,
			    "" );		
		assertEquals( "",ave.derive( new VarContext() ) );
		assertEquals( "public", ave.derive( VarContext.of( "access", "public" ) ) );
		assertEquals( "private", ave.derive( VarContext.of( "access", "private" ) ) );
		assertEquals( "protected", ave.derive( VarContext.of( "access", "protected" ) ) );
		assertEquals( "", ave.derive( VarContext.of("access", "" ) ) );		
	}
	
	
	 public void testRequired()
	 {
	        //NOTE: here I want the name with the first character uppercase
		 	String mark =
				"/*{+name*+}*/";
				
			AddVarExpression ave = 
				new AddVarExpression(
					 mark, 
					 lineNumber,
					 "name", 
					 true, //boolean isRequired,
					 null,
					 null );	
	        
	        assertTrue( ave.isRequired() );
	        assertEquals( ave.getVarName(),  "name" );
	        
	        assertEquals( "eric", ave.derive( VarContext.of( "name", "eric" ) ) );
	        
	        //assertEquals( "Eric", i.derive( VarContext.of( "name", "Eric" ) ) );
	        
	        try
	        {
	            ave.derive( VarContext.of( ) );
	            fail("Expected Exception for Missing Required Field ");
	        }
	        catch( VarException cme )
	        {
	            //expected
	        }
	    }
	 
	public void testRequiredButNullMark()
	{
		String mark =
			"/*{+" + name + ":(( " + EXPRESSION + " ))"+ "*+}*/";
		
		AddVarExpression ave = 
			new AddVarExpression(
			 	mark, 
			    lineNumber,
			    name, 
			    true, //boolean isRequired,
			    EXPRESSION,
			    null );		
		try
		{
			ave.derive( new VarContext() );
			fail("Expected exception for required but null");
		}
		catch( VarRequiredButNull rbn )
		{
			//expected
		}		
	}
	
	public void testBadButDefaultMark()
	{
		String mark =                                    //default "public"
			"/*{+" + name + ":(( " + EXPRESSION + " ))"+ "|public+}*/";
		
		AddVarExpression ave = 
			new AddVarExpression(
			 	mark, 
			    lineNumber,
			    name, 
			    false, //boolean isRequired,
			    EXPRESSION,
			    "public" ); //default		
		
		assertEquals( "public", ave.derive( new VarContext() ) );
		assertEquals( "public", ave.derive( VarContext.of( "access", "Q@&*(^$" ) ) );
	}

	public void testBadMark()
	{
		String mark =
			"/*{+" + name + ":(( " + EXPRESSION + " ))"+ "*+}*/";
		
		AddVarExpression ave = 
			new AddVarExpression(
			 	mark, 
			    lineNumber,
			    name, 
			    true, //boolean isRequired,
			    EXPRESSION,
			    null );		
		try
		{
			ave.derive( VarContext.of( "access", "Q@&*(^$" ) );
			fail("expected Exception for bad name");
		}
		catch( EvalException ee )
		{
			ee.printStackTrace();
		}
	}	
}
