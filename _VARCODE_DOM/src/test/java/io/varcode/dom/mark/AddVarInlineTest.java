package io.varcode.dom.mark;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.dom.VarNameAudit;
import io.varcode.dom.codeml.CodeMLParser;
import junit.framework.TestCase;

public class AddVarInlineTest
    extends TestCase
{
   
    
    public void testRequried()
    {
        String mark = "{+name*+}";
        AddVarExpression i = CodeMLParser.AddVarInlineMark.of( mark, 0, VarNameAudit.BASE );
        
        assertTrue(  i.isRequired() );
        assertTrue( "a".equals(  i.derive( VarContext.of( "name", "a" ) ) ) );
        assertTrue( i.getVarName().equals( "name" ) );
        
        try
        {
            i.derive( VarContext.of(  ) );
            fail ("expected exception");
        }
        catch( VarException cme )
        {
            //expected
        }
    }
    
}
