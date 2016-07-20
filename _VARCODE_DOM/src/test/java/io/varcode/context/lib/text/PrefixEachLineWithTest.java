package io.varcode.context.lib.text;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import junit.framework.TestCase;

public class PrefixEachLineWithTest
	extends TestCase
{

	public void testIndentString1Line()
	{
		VarContext vc = new VarContext();
		vc.set( "name", "ABCDEFG" );
		VarScript vs = vc.getVarScript( "prefixEachLine" );
		assertNotNull( vs );
		String val = (String)vs.eval( vc,  "name,prefix " );
		System.out.println( val );
		assertEquals( "prefix ABCDEFG", val );
	}
	
	public void testPrefix2Lines()
	{
		VarContext vc = new VarContext();
		vc.set( "name", 
				"ABCDEFG" + System.lineSeparator()
		       +"HIJKLMN" ); 
		
		assertEquals(
			"prefix ABCDEFG" + System.lineSeparator()
		   +"prefix HIJKLMN", 
			vc.getVarScript( "prefixEachLine" ).eval( 
				vc,  
				"name,prefix ") );
	}
}
