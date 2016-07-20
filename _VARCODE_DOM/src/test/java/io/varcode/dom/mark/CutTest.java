package io.varcode.dom.mark;

import io.varcode.dom.codeml.CodeMLParser;
import io.varcode.dom.mark.Cut;
import junit.framework.TestCase;

public class CutTest
    extends TestCase
{
    public void testSimple()
    {
        Cut cc = CodeMLParser.CutMark.of( "/*{-*/ some code /*-}*/", 10 );
        assertEquals( "/*{-*/ some code /*-}*/", cc.getText() );
        assertEquals( 10, cc.getLineNumber() );
        assertEquals( " some code ", cc.getBetweenText() );
    }
    
}
