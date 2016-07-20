package io.varcode.dom.mark;

import io.varcode.context.VarContext;
import io.varcode.dom.mark.ReplaceWithExpressionResult;
import junit.framework.TestCase;

public class ReplaceWithExpressionResultTest
    extends TestCase
{
	
    public void testSimple()
    {
        String codeMLMark = "/*{+((3+4))*/something/*+}*/";        
        ReplaceWithExpressionResult rwe = 
        	new ReplaceWithExpressionResult( codeMLMark, 
        	        23, 
        	        "3+4", 
        	        "something" );
        
        assertEquals( "3+4", rwe.getExpression() );
        assertEquals( "something", rwe.getWrappedContent() );
        
        assertEquals( 7, rwe.derive( new VarContext() ) );        
    }
    
}
