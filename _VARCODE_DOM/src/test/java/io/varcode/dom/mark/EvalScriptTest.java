package io.varcode.dom.mark;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScript;
import io.varcode.dom.codeml.CodeMLParser;
import io.varcode.dom.mark.EvalScript;
import junit.framework.TestCase;

public class EvalScriptTest
    extends TestCase
{

    public void testScript()
    {
        EvalScript sm = 
            (EvalScript) CodeMLParser.parseMark( "/*{$scriptName(input)$}*/" );
        
        assertEquals("scriptName", sm.getScriptName());
        assertEquals("input", sm.getScriptInput() );
        
        VarContext vc = VarContext.of( 
            "scriptName", 
            new VarScript()
            {
				public ScriptInputParser getInputParser() 
				{
					return VarScript.IGNORE_INPUT;
				}

				public Object eval( VarContext context, String input ) 
				{
					return input;
				}            	
            });
        
        sm.derive( vc );
        
        
        vc = VarContext.of( 
            "scriptName", 
            new VarScript()
            {
				public ScriptInputParser getInputParser() 
				{
					return VarScript.IGNORE_INPUT;
				}

				public Object eval( VarContext context, String input ) 
				{
					throw new VarException("Throw me");
				}            	
            });
        
        try
        {
            sm.derive( vc );
            fail("expected exception"); 
        }
        catch( Exception e )
        {
            System.out.println( e );
        }        
    }
    
//    public void testValidate()
//    {
//        EvalScript sm = 
//            (EvalScript) CodeMLParser.parseMark( "/*{$min1Max8(input)$}*/" );
//        
//         assertEquals("min1Max8", sm.getScriptName());
//         assertEquals("input", sm.getScriptInput() );
//         
//         VarContext vc = 
//             VarContext.of( 
//                 "min1Max8", new ValidateMinMaxCount( 1, 8 ),
//                 "input", "A" );
//                    
//         sm.derive( vc );
//         
//         vc = VarContext.of( 
//             "min1Max8", new ValidateMinMaxCount( 1, 8 ) );
//         try
//         {
//             sm.derive( vc );
//             fail("Expected exception");
//         }
//         catch( Exception e )
//         {
//             //expected
//             System.out.println( e );             
//         }
//         
//         vc = VarContext.of( 
//             "min1Max8", new ValidateMinMaxCount( 1, 8 ),
//             "input", new int[]{1,2,3,4,5,6,7,8,9});
//         
//         try
//         {
//             sm.derive( vc );
//             fail("Expected exception");
//         }
//         catch( Exception e )
//         {
//             System.out.println( e );
//         }
//         
//         
//    }
}
