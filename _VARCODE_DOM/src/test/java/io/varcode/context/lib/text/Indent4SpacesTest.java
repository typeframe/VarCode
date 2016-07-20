package io.varcode.context.lib.text;

import io.varcode.context.VarContext;
import junit.framework.TestCase;

public class Indent4SpacesTest
	extends TestCase
{

	public void testIndentString1Line()
	{
		VarContext vc = new VarContext();
		
		assertEquals("    ABCDEFG", 
			vc.getVarScript( "indent" ).eval( vc,  "ABCDEFG" ) );
	}
	
	public void testIndentString2Lines()
	{
		VarContext vc = new VarContext();
		
		assertEquals(
			"    ABCDEFG" + System.lineSeparator()
		   +"    HIJKLMN", 
			vc.getVarScript( "indent" ).eval( 
				vc,  
				"ABCDEFG" + System.lineSeparator()
			   +"HIJKLMN" ) );
	}
	
	/** Call indent in a chained way (indent(indent(x)) */
	public void testChained()
	{
		VarContext vc = new VarContext();
		
		assertEquals(
			"        ABCDEFG" + System.lineSeparator()
		   +"        HIJKLMN", 
			vc.getVarScript( "indent" ).eval( 
				vc,  
				"$indent(ABCDEFG" + System.lineSeparator()
			   +"HIJKLMN)" ) );
	}
	
}
