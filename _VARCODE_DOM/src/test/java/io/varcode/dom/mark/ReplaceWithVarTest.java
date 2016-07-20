package io.varcode.dom.mark;

import io.varcode.context.VarContext;
import io.varcode.dom.VarNameAudit;
import io.varcode.dom.codeml.CodeMLParser;
import io.varcode.dom.mark.ReplaceWithVar;
import junit.framework.TestCase;

public class ReplaceWithVarTest
    extends TestCase
{
    public void testRequired()
    {
        String mark = "/*{+a**/something/*+}*/";
        
        ReplaceWithVar r = CodeMLParser.ReplaceWithVarMark.of( 
            mark, 0, VarNameAudit.BASE );
        
        assertTrue( r.isRequired() );
        assertTrue( r.getVarName().equals( "a" ) );
        
    }

    public void testWithDefault()
    {
        String mark = "/*{+a|*/\"something\"/*+}*/";
        
        ReplaceWithVar r = CodeMLParser.ReplaceWithVarMark.of( 
            mark, 0, VarNameAudit.BASE );
        
        assertFalse( r.isRequired() );
        assertEquals("\"something\"", r.getDefault() );
        
        assertTrue( r.getVarName().equals( "a" ) );
        
        assertEquals( r.getDefault(), r.derive( new VarContext() ) );
        
        assertEquals( 1, r.derive( VarContext.of( "a", 1 ) ) );
        
    }
    
}
