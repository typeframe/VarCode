package io.varcode.dom.form;

import io.varcode.dom.form.BetweenTokens;
import io.varcode.dom.form.BetweenTokens.BaseBetweenTokens;
import junit.framework.TestCase;

public class BetweenTokensTest
    extends TestCase
{
    public static final BaseBetweenTokens b = 
        BetweenTokens.BaseBetweenTokens.INSTANCE;
    
    public void testBetween()
    {        
    	//logical short curcuit
        assertEquals( "||", b.endsWithToken( "||" ) ); // a || b || c       
        assertEquals( "&&", b.endsWithToken( "&&" )); // a && b && c
        
        //bitwise
        assertEquals( "|", b.endsWithToken( "|" ) ); // a | b | c
        assertEquals( "&", b.endsWithToken(  "&" )); // a & b & c
        assertEquals( "^", b.endsWithToken(  "^" )); // a ^ b ^ c
        
        //parameterized
        assertEquals( ",", b.endsWithToken( "," ) ); // a, b, c
        
        
        assertEquals( "+", b.endsWithToken( "+" )); // a + b + c
        assertEquals( "-", b.endsWithToken( "-" )); // a - b - c            
        assertEquals( "*", b.endsWithToken( "*" )); // a * b * c
        //assertEquals( "/", b.endsWithToken(  "/" )); // a / b / c
        //assertEquals( "%", b.endsWithToken(  "%" )); // a % b % c                              
        
         
       
        //assertEquals( "=", b.endsWithToken(  "=" ));
        //assertEquals( ">>", b.endsWithToken( ">>" ));
        //assertEquals( ">>>", b.endsWithToken(  ">>>" ));
        //assertEquals( "<<", b.endsWithToken( "<<" ));
      
        //assertEquals( "==", b.endsWithToken(  "==" ));
    
        //assertEquals( "+=", b.endsWithToken(  "+=" ));
        //assertEquals( "/=", b.endsWithToken( "/=" ));
        //assertEquals( "*=", b.endsWithToken( "*=" ));
        //assertEquals( "-=", b.endsWithToken( "-=" ));
        //assertEquals( "%=", b.endsWithToken( "%=" ));
    
        //assertEquals( "<<=", b.endsWithToken( "<<=" ));
        //assertEquals( ">>=", b.endsWithToken( ">>=" ));
        //assertEquals( "&=", b.endsWithToken( "&=" ));
        //assertEquals( "^=", b.endsWithToken( "^=" ));
        //assertEquals( "|=", b.endsWithToken( "|=" ));
            
        //assertEquals( "!=", b.endsWithToken( "!=" ));
        //assertEquals( ">", b.endsWithToken( ">" ));
        //assertEquals( "<", b.endsWithToken( "<" ));
        //assertEquals( ">=", b.endsWithToken( ">=" ));
        //assertEquals( "<=", b.endsWithToken( "<=" ));
         
             
    }
    
    
    public void testTail()
    {
        assertEquals( null, b.endsWithToken( "" ) );
        assertEquals( null, b.endsWithToken( null ) );
        //assertEquals( "+=", b.endsWithToken( "much text here and+=      " ) );
        //assertEquals( ">>>", b.endsWithToken( "much text here and>>>    " ) );
        
        //assertEquals( ">>", b.endsWithToken( "much text here and> >>        " ) );
        assertEquals( "&", b.endsWithToken( "much text here and& &" ) );
    }
    
    public void testNotBetween()
    {
        assertNull( b.endsWithToken( "<< | ! )" ) );
    }
}
