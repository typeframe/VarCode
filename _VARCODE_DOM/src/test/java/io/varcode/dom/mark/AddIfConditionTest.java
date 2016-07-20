package io.varcode.dom.mark;

import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.dom.mark.AddIf;
import junit.framework.TestCase;

public class AddIfConditionTest
    extends TestCase
{
    
    public void testConditionEvalFailure()
    {
        AddIf aic = 
            new AddIf(
                "/*{+?(( blahde this )):A IS GREATER}*/",
                -1, 
                "blahde this",
                "A IS GREATER" );
        
        try
        {
            aic.derive( VarContext.of(  ) );
            fail("expected Exception");
        }
        catch( Exception e )
        {
            //expected
        }
    }
    public void testSimpleEval()
    {
        AddIf aic = 
              new AddIf(
                  "/*{+?(( a > b )):A IS GREATER}*/",
                  -1, 
                  "a > b",
                  "A IS GREATER" );
        Object res = aic.derive( VarContext.ofScope( 
            VarScope.INSTANCE, "a", 100, "b", 50 ) );
        
        assertEquals("A IS GREATER", res );
        //System.out.println( res );
        
        VarContext vc = new VarContext();
        vc.getOrCreateBindings( VarScope.INSTANCE ).put( "a", 100 );
        vc.getOrCreateBindings( VarScope.LOOP ).put( "b", 10 );
        
        res = aic.derive( vc );
        
        assertEquals("A IS GREATER", res );
        
        //verify its "" if the expression is not true
        vc.getOrCreateBindings( VarScope.INSTANCE ).put( "a", 2 );
        vc.getOrCreateBindings( VarScope.LOOP ).put( "b", 10 );
        
        res = aic.derive( vc );
        
        assertEquals(null, res );
    }
    
    public void testConditionMultiScope()
    {
    	String expression = "a > b && a % 2 == 0";
    	String conditionalText = "A IS GREATER and EVEN";
    	
        AddIf aic = new AddIf(
             "/*{+?(("+expression+")):" + conditionalText + "+}*/",
             -1, 
             expression,
             conditionalText );
        
        Object res = aic.derive( VarContext.ofScope( 
             VarScope.LOOP, "a", 100, "b", 50 ) );
        
        assertEquals( "A IS GREATER and EVEN", res );
        
        res = aic.derive( VarContext.ofScope( 
            VarScope.LOOP, "a", 101, "b", 50 ) );
        
        assertEquals( null, res );
        
        res = aic.derive( VarContext.ofScope( 
            VarScope.LOOP, "a", 101, "b", 150 ) );
        
        assertEquals( null, res );
        
        
    }

    public void testNotAllVarsBound()
    {
        AddIf aic = new AddIf(
            "/*{+?(( a > b && a % 2 == 0 )):A IS GREATER and EVEN}*/",
            -1, 
            "a > b && a % 2 == 0",
            "A IS GREATER and EVEN" );
        
        try
        {
            aic.derive( VarContext.ofScope( 
                VarScope.LOOP, "a", 101 ) );
            fail("Expected Exception");
        }
        catch(Exception e)
        {
            //expected
        }        
    }
    
    //HEre (instead of a null check) we want to check if
    // a variable "a" is 
    public void testVarUnbound()
    {
        //if (typeof yourvar != 'undefined')
        AddIf aic = new AddIf(
            "/*{+?(( typeof a != 'undefined' )):a IS DEFINED}*/",
            -1, 
            "typeof a != 'undefined'",
            "a IS DEFINED" );
        
        Object res = aic.derive( new VarContext() );        
        assertEquals( null, res );
        
        res = aic.derive( VarContext.of("a", "ANYTHING") );        
        assertEquals( "a IS DEFINED", res );
    }
    
    public void testNullCheck()
    {
        AddIf aic = new AddIf(
            "/*{+?(( a != null )):A IS NOT NULL}*/",
            -1, 
            "a != null",
            "A IS NOT NULL" );
        
        Object res = aic.derive( 
            VarContext.ofScope( VarScope.INSTANCE, "a", "Anything" ) );
        assertEquals( "A IS NOT NULL", res );
        
        res = aic.derive( 
            VarContext.ofScope( VarScope.INSTANCE, "a", null ) );
        assertEquals( null, res );
        try
        {
            aic.derive( new VarContext( ) );
        }
        catch(Exception e)
        {
            //expected
        }
               
    }
    
    public void testDocumentWrite()
    {
        AddIf aic = new AddIf(
            "/*{+?(( document.write(2 + 3) )):A IS GREATER and EVEN}*/",
            -1, 
            "document.write(2 + 3)",
            "A IS GREATER and EVEN" );
         try
         {
             aic.derive( VarContext.ofScope( 
                 VarScope.LOOP, "a", 100, "b", 20 ) );
             fail("Expected exception");
         }
         catch( Exception e )
         {
             //expected
         }        
    }
    
    public void testConsoleLog()
    {
        AddIf aic = new AddIf(
            "/*{+?(( console.log(2 + 3) )):A IS GREATER and EVEN}*/",
            -1, 
            "console.log(2 + 3)",
            "A IS GREATER and EVEN" );
         
        try
        {
            aic.derive( VarContext.ofScope( 
                VarScope.LOOP, "a", 100, "b", 20 ) );
            fail( "Expected Exception" );
        }
        catch( Exception e )
        {
            //expected
        }
        
        //assertEquals( "", res );
    }
    
    //verify that I can change the value of parameters in the context
    //but the changes are NOT reflected in the bindings
    public void testReassignment()
    {
        AddIf aic = new AddIf(
            "/*{+?(( a--;a >= 100 )):A IS GREATER and EVEN}*/",
            -1, 
            "a--; a >= 100",
            "A > 100" );
        
        VarContext vc = VarContext.ofScope( 
            VarScope.LOOP, "a", 100, "b", 20 );
        Object res = aic.derive( vc );
        
        assertEquals( vc.get( "a" ), 100);
        
        assertEquals( null, res );
        
    }
    
    
}
