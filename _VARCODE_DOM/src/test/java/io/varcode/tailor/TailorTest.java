package io.varcode.tailor;

import io.varcode.context.VarContext;
import io.varcode.context.lib.text.Indent4Spaces;
import io.varcode.dom.Dom;
import io.varcode.dom.bindml.BindML;
import junit.framework.TestCase;

public class TailorTest
	extends TestCase
{
	
	public void testTailorSimple()
	{
		Dom dom = BindML.compile( "{+name+}" );
		assertEquals( "",  Tailor.code( dom ) ); 
		assertEquals( "eric", Tailor.code( dom, "name", "eric" ) );		
	}

	public static final String N = System.lineSeparator();
	
	public void testAddDirectives()
	{
		//HERE I am using a "{$$indent$$}" Directive Mark IN THE DOM
		// to specify a Directive to indent EACH line 4 spaces
		Dom dom = BindML.compile( "{$$indent$$}{+name+}"   ); //Directive indents each line 4 spaces		
		assertEquals( "    eric",  Tailor.code( dom, "name", "eric" ) ); 
		
		dom = BindML.compile( "{$$indent$$}{+name+}"+ N + "{+name+}"   ); 		
		assertEquals( "    eric" + N + "    eric",  Tailor.code( dom, "name", "eric" ) );
		
		//HERE I am Specifying the Directive when calling Tailor 
		dom = BindML.compile( "{+name+}"+ N + "{+name+}"   ); 		
		assertEquals( "    eric" + N + "    eric",  
			Tailor.code( 
				dom, 
				VarContext.of( "name", "eric"), 
				Indent4Spaces.INSTANCE ) );
	}
	
	
	

}
