package _testsuite;


import io.varcode.LangTest;
import io.varcode.TextBufferTest;
import io.varcode.context.VarBindingsTest;
import io.varcode.context.VarContextTest;
import io.varcode.context.lib.core.SHA1ChecksumTest;
import io.varcode.context.lib.core.PrintTest;
import io.varcode.context.lib.state.SystemPropertyTest;
import io.varcode.context.lib.text.FirstCapsTest;
import io.varcode.context.lib.text.Indent4SpacesTest;
import io.varcode.context.lib.text.PrefixEachLineWithTest;
import io.varcode.context.script.ScriptEvaluator_JavaScriptTest;
import io.varcode.dom.FillInTheBlanksTest;
import io.varcode.dom.MarkupParserTest;
import io.varcode.dom.VarNameAuditTest;
import io.varcode.dom.bindml.BindMLCompilerTest;
import io.varcode.dom.bindml.BindMLParserTest;
import io.varcode.dom.codeml.CodeMLCompilerTest;
import io.varcode.dom.codeml.CodeMLParserMarkTest;
import io.varcode.dom.codeml.CodeMLParserTest;
import io.varcode.dom.codeml.CodeMLStateTest;
import io.varcode.dom.form.BetweenTokensTest;
import io.varcode.dom.form.SeparateFormsTest;
import io.varcode.dom.forml.ForMLCompilerTest;
import io.varcode.dom.forml.ForMLParserTest;
import io.varcode.dom.forml.FormTest;
import io.varcode.dom.mark.AddExpressionResultTest;
import io.varcode.dom.mark.AddFormIfVarTest;
import io.varcode.dom.mark.AddFormTest;
import io.varcode.dom.mark.AddIfConditionTest;
import io.varcode.dom.mark.AddIfVarTest;
import io.varcode.dom.mark.AddScriptResultTest;
import io.varcode.dom.mark.AddVarExpressionTest;
import io.varcode.dom.mark.AddVarInlineTest;
import io.varcode.dom.mark.AddVarTest;
import io.varcode.dom.mark.CutCommentTest;
import io.varcode.dom.mark.CutIfTest;
import io.varcode.dom.mark.CutTest;
import io.varcode.dom.mark.DefineVarAsFormTest;
import io.varcode.dom.mark.DefineVarAsScriptResultTest;
import io.varcode.dom.mark.DefineVarTest;
import io.varcode.dom.mark.EvalExpressionTest;
import io.varcode.dom.mark.EvalScriptTest;
import io.varcode.dom.mark.FormFunctionalTest;
import io.varcode.dom.mark.ReplaceWithExpressionResultTest;
import io.varcode.dom.mark.ReplaceWithFormTest;
import io.varcode.dom.mark.ReplaceWithScriptResultTest;
import io.varcode.dom.mark.ReplaceWithVarTest;
import io.varcode.dom.mark.TailorDirectiveTest;
import io.varcode.java.JavaNamingTest;
import io.varcode.java.javac.InMemoryJavaCompilerTest;
import io.varcode.markup.LangNamespaceToMarkupPathTest;
import io.varcode.tailor.TailorTest;
import io.varcode.text.SmartBufferTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTestSuite
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite( AllTestSuite.class.getName() );

        //$JUnit-BEGIN$
        //>JUNIT>
        suite.addTestSuite( BindMLParserTest.class );
        suite.addTestSuite( BindMLCompilerTest.class );
        
        suite.addTestSuite( CodeMLParserMarkTest.class );
        suite.addTestSuite( CodeMLParserTest.class );
        suite.addTestSuite( CodeMLCompilerTest.class );
        suite.addTestSuite( CodeMLStateTest.class );
        
        suite.addTestSuite( ForMLCompilerTest.class );
        suite.addTestSuite( ForMLParserTest.class );
        suite.addTestSuite( FormTest.class );
               
        suite.addTestSuite( ScriptEvaluator_JavaScriptTest.class );
        
        suite.addTestSuite( AddVarExpressionTest.class );
        suite.addTestSuite( AddExpressionResultTest.class );
        suite.addTestSuite( AddFormIfVarTest.class );
        suite.addTestSuite( AddFormTest.class );
        suite.addTestSuite( AddIfConditionTest.class );
        suite.addTestSuite( AddIfVarTest.class );
        suite.addTestSuite( AddScriptResultTest.class );
        suite.addTestSuite( AddVarInlineTest.class );
        suite.addTestSuite( AddVarTest.class );
        suite.addTestSuite( CutCommentTest.class );
        suite.addTestSuite( CutIfTest.class );
        suite.addTestSuite( CutTest.class );
        
        suite.addTestSuite( DefineVarAsFormTest.class );
        suite.addTestSuite( DefineVarAsScriptResultTest.class );
        suite.addTestSuite( DefineVarTest.class );
        suite.addTestSuite( FormFunctionalTest.class );
        suite.addTestSuite( ReplaceWithFormTest.class );
        suite.addTestSuite( ReplaceWithScriptResultTest.class );
        suite.addTestSuite( ReplaceWithVarTest.class );
        
        suite.addTestSuite( EvalExpressionTest.class );
        suite.addTestSuite( EvalScriptTest.class );
        suite.addTestSuite( TailorDirectiveTest.class );

        suite.addTestSuite( ReplaceWithExpressionResultTest.class );
        
        //core library tests
        suite.addTestSuite( SHA1ChecksumTest.class );
        suite.addTestSuite( FirstCapsTest.class );
        suite.addTestSuite( Indent4SpacesTest.class );
        suite.addTestSuite( PrefixEachLineWithTest.class );
        suite.addTestSuite( PrintTest.class );
        suite.addTestSuite( SystemPropertyTest.class );
        
        
        suite.addTestSuite( LangTest.class );
        suite.addTestSuite( TextBufferTest.class );
        suite.addTestSuite( MarkupParserTest.class );
        
        
        suite.addTestSuite( VarBindingsTest.class );
        suite.addTestSuite( VarContextTest.class );
        
        suite.addTestSuite( FillInTheBlanksTest.class );
        suite.addTestSuite( VarNameAuditTest.class );
        
        
        suite.addTestSuite( BetweenTokensTest.class );
        suite.addTestSuite( SeparateFormsTest.class );
        
        suite.addTestSuite( LangNamespaceToMarkupPathTest.class );
        suite.addTestSuite( InMemoryJavaCompilerTest.class );
        suite.addTestSuite( JavaNamingTest.class );
        
       
        suite.addTestSuite( SmartBufferTest.class );
        
        suite.addTestSuite( TailorTest.class );
        // SLOW
        //suite.addTestSuite( PathWalkTest.class );
        //<JUNIT<
        //$JUnit-END$
        return suite;
    }
}
