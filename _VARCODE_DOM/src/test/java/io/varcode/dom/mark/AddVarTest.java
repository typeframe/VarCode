package io.varcode.dom.mark;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.dom.VarNameAudit;
import io.varcode.dom.bindml.BindMLParser;
import io.varcode.dom.codeml.CodeMLParser;
import junit.framework.TestCase;

public class AddVarTest
    extends TestCase
{

	public void testEmptyStringDefault()
	{
		AddVarExpression i = CodeMLParser.AddVarExpressionMark.of( 
	         "/*{+name|+}*/", 
	         0, 
	         VarNameAudit.BASE );
		assertEquals("", i.derive(VarContext.of( ) ) ) ;
		
		AddVarExpression d = BindMLParser.AddVarExpressionMark.of( 
		     "{+name|+}", 
		     0, 
		     VarNameAudit.BASE );
		assertEquals("", d.derive(VarContext.of( ) ) ) ;
	}
    public void testRequired()
    {
        //NOTE: here I want the name with the first character uppercase
        AddVarExpression i = CodeMLParser.AddVarExpressionMark.of( 
            "/*{+name*+}*/", 
            0, 
            VarNameAudit.BASE );
        
        assertTrue( i.isRequired() );
        assertEquals( i.getVarName(),  "name" );
        
        assertEquals( "eric", i.derive( VarContext.of( "name", "eric" ) ) );
        
        //assertEquals( "Eric", i.derive( VarContext.of( "name", "Eric" ) ) );
        
        try
        {
            i.derive( VarContext.of( ) );
            fail("Expected Exception for Missing Required Field ");
        }
        catch( VarException cme )
        {
            //expected
        }
    }
    
    
}
