package io.varcode.context.lib.java;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.context.EvalException;
import io.varcode.context.lib.Library;
import junit.framework.TestCase;

//TODO make a TailorDirective that loads a library
//Maybe I should figure out a way to find all classes that im

public class JavaLibTest
	extends TestCase
{
	public void testLoadLib()
	{
		VarContext vc = VarContext.load( JavaLib.INSTANCE );
		
		//can I get a handle the the library
		Library lib = (Library) vc.get( 
			JavaLib.INSTANCE.getName() + "." + JavaLib.INSTANCE.getVersion() );
		
		assertNotNull( lib );
		
		assertNotNull( vc.get("-prints" ) );
		assertNotNull( vc.get("removePrints" ) );
		assertNotNull( vc.get("removePrintStatements" ) );
		
		assertNotNull( vc.get("validateClassName" ) );
		assertNotNull( vc.get("!className" ) );
		
		assertNotNull( vc.get("validateIdentifierName" ) );
		assertNotNull( vc.get("validateIdentifier" ) );		
		assertNotNull( vc.get("!identifierName" ) );
		
		assertNotNull( vc.get("validatePackageName" ) );		
		assertNotNull( vc.get("!packageName" ) );		
	}
	
	public void testRemovePrints()
	{
		VarContext vc = VarContext.load( JavaLib.INSTANCE );
		String s = "a String with System.out.println( );";
		VarScript vs = vc.getVarScript( "-prints" );
		String res = (String)vs.eval(new VarContext(),  s );
		assertEquals("a String with ", res );
	}
	
	public void testValidateClassName()
	{
		VarContext vc = VarContext.load( JavaLib.INSTANCE )
			.set("className", "AValidClassName" );
		
		vc.getVarScript( "!className" ).eval( vc, "className" );
		
		try
		{
			vc.set("className", null );
			vc.getVarScript( "!className" ).eval( vc, "className" );
			fail( "expectedException" );
		}
		catch( EvalException se)
		{
			//expected
		}
		
		try
		{
			vc.set("className", "3assdf" );
			vc.getVarScript( "!className" ).eval( vc, "className" );
			fail( "expectedException" );
		}
		catch( EvalException se)
		{
			//expected
		}
		
		try
		{
			vc.set( "className", "asdlkfj$*" );
			vc.getVarScript( "!className" ).eval( vc, "className" );
			fail( "expectedException" );
		}
		catch( EvalException se)
		{
			//expected
		}
		try
		{
			vc.set( "className", "synchronized" );
			vc.getVarScript( "!className" ).eval( vc, "className" );
			fail( "expectedException" );
		}
		catch( EvalException se)
		{
			//expected
		}
	}
}
