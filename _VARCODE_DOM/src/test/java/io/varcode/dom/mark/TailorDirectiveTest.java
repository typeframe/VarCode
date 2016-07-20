package io.varcode.dom.mark;

import io.varcode.context.VarContext;
import io.varcode.context.lib.text.RemoveEmptyLines;
import io.varcode.dom.Dom;
import io.varcode.dom.codeml.CodeMLCompiler;
import io.varcode.tailor.Tailor;
import junit.framework.TestCase;

public class TailorDirectiveTest
	extends TestCase
{

	private final String N = System.lineSeparator();
	
	public void testTailorPostProcessor()
	{
		Dom markup = CodeMLCompiler.fromString( 
			"/*{$$removeEmptyLines()$$}*/" 
		    + N + "A" + N + N + N + N + N + N + "Z" );
		
		VarContext vc = VarContext.of( 
			"removeEmptyLines", RemoveEmptyLines.INSTANCE );
		
		
		System.out.println( Tailor.code(  markup, vc ) );
		
	}
}
