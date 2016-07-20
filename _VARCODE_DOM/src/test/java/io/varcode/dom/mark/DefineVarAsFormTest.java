package io.varcode.dom.mark;


import io.varcode.context.VarContext;
import io.varcode.dom.codeml.CodeMLParser;
import io.varcode.dom.forml.ForMLCompiler;
import io.varcode.dom.mark.DefineVarAsForm;
import junit.framework.TestCase;

public class DefineVarAsFormTest
    extends TestCase
{
    public void testDerive()
    {
        VarContext vc = VarContext.of( "fieldCount", 3 );
        DefineVarAsForm.InstanceVar acf = 
            CodeMLParser.DefineInstanceVarAsFormMark.of(
              vc,
              "/*{{#className*:IntFrameBoxOf{+fieldCount+}#}}*/", 
              0,
              ForMLCompiler.INSTANCE );
        assertEquals( "className", acf.getVarName() );
        assertEquals( "className", acf.form.getName() );
        assertTrue( acf.isRequired );
        assertEquals( acf.lineNumber, 0 );
        assertEquals( acf.text, "/*{{#className*:IntFrameBoxOf{+fieldCount+}#}}*/" );
        //assertEquals( acf.getRequiredVars().length, 0 );
        //assertEquals( acf.getAllVars().length, 1 );
        //assertEquals( acf.getAllVars()[ 0 ].getName(), "fieldCount" );
        assertEquals( acf.form.getText(), "IntFrameBoxOf{+fieldCount+}" );
        
         
        acf.bind( vc );
        //acf.update( context );
        assertEquals(  "IntFrameBoxOf3",
            vc.get( "className" ) );        
    }
    
    /**
     * Verify that 
     */
    public void testAssignAndUse()
    {
        VarContext bc = VarContext.of( "fieldCount", "3"  );
        
        DefineVarAsForm.InstanceVar acf = 
            (DefineVarAsForm.InstanceVar)CodeMLParser.DefineInstanceVarAsFormMark.of(
                bc,
                "/*{{#className:IntFrameBoxOf{+fieldCount*+}#}}*/", 
                0, 
                ForMLCompiler.INSTANCE );
        //evaluate
        
        
        System.out.println( "INIT \"" + bc.get( "fieldCount" ) + "\"" );
        System.out.println( "RESULT \""+ acf.getForm().derive( bc ) +"\"" );
        
        
        System.out.println( "RESULT \""+ acf.derive( bc ) +"\"");
        
        //acf.updateCompileState( bc );
        //this will evaluate/derive the 
        acf.bind( bc );
        
        //acf.derive( mc );
        
        //verify that the mutable context HAS the derived className className
        assertEquals( "IntFrameBoxOf3", bc.get( "className" ));
        
        //assertEquals( mc.getAttribute( "className" ), "IntFrameBoxOf3" );
        
        
    }
}
