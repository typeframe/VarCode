package io.varcode.context.lib.core;

import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.dom.bindml.BindML;
import io.varcode.dom.mark.EvalScript;
import junit.framework.TestCase;

public class PrintTest
	extends TestCase
{
	public void testPrint()
	{
		
		EvalScript rs = (EvalScript) BindML.parseMark( "{$print(*)*$}" );
		rs.derive( VarContext.of( ) );
		
		rs = (EvalScript) BindML.parseMark( "{$print()*$}" );
		rs.derive( VarContext.of( ) );
		
		rs.derive( VarContext.ofScope( VarScope.LOOP, "A", 1 ) );		
	}

}
