package io.varcode.dom.bindml;


import io.varcode.Metadata;
import io.varcode.context.EvalException;
import io.varcode.context.ResultRequiredButNull;
import io.varcode.context.VarBindException;
import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.dom.Dom;
import io.varcode.dom.mark.AddExpressionResult;
import io.varcode.dom.mark.AddForm;
import io.varcode.dom.mark.AddFormIfVar;
import io.varcode.dom.mark.AddIfVar;
import io.varcode.dom.mark.AddScriptResult;
import io.varcode.dom.mark.AddVarExpression;
import io.varcode.dom.mark.Cut;
import io.varcode.dom.mark.DefineVar;
import io.varcode.dom.mark.DefineVarAsExpressionResult;
import io.varcode.dom.mark.DefineVarAsForm;
import io.varcode.dom.mark.DefineVarAsScriptResult;
import io.varcode.dom.mark.EvalExpression;
import io.varcode.dom.mark.EvalScript;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.SetMetadata;
import io.varcode.dom.mark.TailorDirective;
import io.varcode.tailor.Tailor;
import io.varcode.tailor.TailorState;
import junit.framework.TestCase;

public class BindMLCompilerTest 
	extends TestCase
{
	private static Mark getOnlyMark( Dom markup )
	{
		return markup.getAllMarks()[ 0 ];
	}
	
	/** 
	 * these are essentially "functional tests"
	 * verifying we can parse to dom, then taior code and get what we expect
	 * when we tailor the result 
	 */
	public void testAddMarkEval()
	{
		                        //mark      expected  key /value pairs
		//assertDeriveMarkEquals( "{+name+}", "Eric", "name", "Eric" );
		assertDeriveMarkEquals( "{+name*+}", "Eric", "name", "Eric" );		
		assertDeriveMarkEquals( "{+name|default+}", "Eric", "name", "Eric" );
		assertDeriveMarkEquals( "{+name|default+}", "default" );
		assertDeriveMarkEquals( "{+(( 3 + 5 ))+}", "8" );
		
		try
		{
			assertDeriveMarkEquals( "{+name:(( name.length() > 2 ))+}", "theValue", "name", "a" );
			fail( "Expected exception for var not valid " ); 
		}
		catch( VarBindException be )
		{
			//expected
		}
		
		assertDeriveMarkEquals( "{+name:(( name.length() > 2 ))+}", "theValue", "name", "theValue" );
		
		assertDeriveMarkEquals( "{+name:(( name.length() > 2 ))|defaultValue+}", "defaultValue" );
		
		
		try
		{
			assertDeriveMarkEquals( "{+(( A + B | 0 ))+}", "" );
			fail("Expected Exception for not bound A and B vars");
		}
		catch( EvalException ee )
		{ /*expected*/ }		
		try
		{
			assertDeriveMarkEquals( "{+(( A + B | 0 ))+}", "", "A", "1" );
			fail("Expected Exception for not bound B vars");
		}
		catch( EvalException ee )
		{ /*expected*/ }
		
		// FYI we do this |0 'cause it tricks the JS Engine JIT to convert numbers to ints
		// since all numbers in JS are decimal
		assertDeriveMarkEquals( "{+(( A + B | 0  ))+}", "3", "A", 1, "B", 2 );
		
		
		assertDeriveMarkEquals( "{+$count(a)+}", "" );
		assertDeriveMarkEquals( "{+$count(a)+}", "0", "a", new String[0] );
		assertDeriveMarkEquals( "{+$count(a)+}", "1", "a", new String[] {"s"} );
		assertDeriveMarkEquals( "{+$count(a)+}", "1", "a", "s" );
		try
		{
			assertDeriveMarkEquals( "{+$count(a)*+}", "" );
			fail( "expected Exception" );
		}
		catch( ResultRequiredButNull ee )
		{ /*expected*/ }

		
		assertDeriveMarkEquals( "{+?a:writeThis+}", "" );
		assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "anythingNonNull" );
		assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "" );
		
		assertDeriveMarkEquals( "{+?a:writeThis+}", "" );
		assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "anythingNonNull" );
		assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "" );
		
		
		//fprints nothing if neither value
		assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+};+}}", "" );
		
		
		assertDeriveMarkEquals( "{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", "" );
		
		assertDeriveMarkEquals( 
			"{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", 
			"airman", 
			"name", "airman" );
		
		assertDeriveMarkEquals( 
			"{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", 
			"sargent", 
			"name", "sargent" );
		
		assertDeriveMarkEquals( 
			"{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", 
			"airman first class", 
			"name", "airman first class" );
		
		try
		{
			assertDeriveMarkEquals( 
				"{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", 
				"", 
				"name", "havent got one" );
			fail( "Expected Exception" );
		}
		catch( EvalException ee )
		{
			//expected
		}
		
		
		assertDeriveMarkEquals( 
				"{{+:{+fieldType+} {+fieldName+};+}}", 
				"int x;", 
				"fieldType", int.class, "fieldName", "x" );
		
		assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+}; +}}", 
				"int x; String y; ", 
				"fieldType", new Object[]{int.class, "String"}, 
				"fieldName", new Object[]{"x", "y"} );
		
		try
		{
			assertDeriveMarkEquals( "{{+:{+fieldType*+} {+fieldName+};+}}", "" );
			fail( "expected Exception" );
		}
		catch( VarRequiredButNull rbn )
		{ /*expected*/ }
		
		try
		{
			assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName*+};+}}", "" );
			fail( "expected Exception" );
		}
		catch( VarRequiredButNull rbn )
		{ /*expected*/ }
		try
		{
			assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+};*+}}", "" );
			fail( "expected Exception" );
		}
		catch( ResultRequiredButNull rr )
		{ /*expected*/ }
		//assertTrue( getOnlyMark( BindML.compile( "{{+?a==1: implements {+impl+}+}}" ) ) instanceof AddFormIfVar );
		
	}
	
	public void assertDeriveMarkEquals( String mark, String expected, Object...keyValuePairs )
	{
		assertEquals( expected, Tailor.code( BindML.compile( mark ), keyValuePairs ) ); 
	}
	
	public void assertVarContextUpdate( 
		String mark, String varName, Object expected, Object...keyValuePairs )
	{
		VarContext vc = VarContext.of( keyValuePairs );
		Tailor.code( BindML.compile( mark ), vc );
		assertEquals( expected, vc.get( varName ) );
	}
	
	public void testDeriveMarkFunctional()
	{
		assertDeriveMarkEquals( "{- some text -}", "" );
		
		//  mark,  varName, expected, keyValuePairs )
		assertVarContextUpdate( "{#a=1#}", "a", "1" );
		assertVarContextUpdate( "{#a:$count(a)#}", "a", null );
		assertVarContextUpdate( "{#a:$count(a)#}", "a", 0, "a", new String[0] );
		assertVarContextUpdate( "{#a:$count(a)#}", "a", 1, "a", "A" );
		
		assertVarContextUpdate( "{##a=1##}", "a", "1" );
		
		assertVarContextUpdate( "{##a=1##}", "a", "1" );
		
		
		assertVarContextUpdate( "{##A:3##}{##B:5##}{##a:(( A + B ))##}", "a", "35");
		
		
		assertVarContextUpdate( "{##a:(( 3 + 5 | 0 ))##}", "a", 8 );
		assertVarContextUpdate( "{##a=(( 3 + 5 | 0 ))##}", "a", 8 );
		
		VarContext vc = new VarContext();
		TailorState ts = Tailor.tailor( BindML.compile( "{@meta:data@}" ), vc );
		
		Metadata md = (Metadata)ts.getContext().get( VarContext.METADATA_NAME ); 
		assertEquals( "data", md.get( "meta" ) );
		/*
		assertVarContextUpdate( "{##a:$^(abc)##}", "a", null );
		assertVarContextUpdate( "{##a:$^(abc)##}", "a", 0, "a", new String[0] );
		assertVarContextUpdate( "{##a:$count(a)##}", "a", 1, "a", "A" );
		*/
		/*
		assertTrue( BindMLParser.parseMark("{#a=1#}" ) instanceof DefineVar.InstanceVar );  //DefineInstanceVar, DefineVarAsScriptResult
		assertTrue( BindMLParser.parseMark("{#a:$count(a)#}" ) instanceof DefineVarAsScriptResult.InstanceVar );  //DefineVarAsScriptResult
		assertTrue( BindMLParser.parseMark("{#c:(( Math.sqrt( a * a + b * b ) ))#}" ) instanceof DefineVarAsExpressionResult.InstanceVar );  //TailorDirective
		assertTrue( BindMLParser.parseMark("{#c=(( Math.sqrt( a * a + b * b ) ))#}" ) instanceof DefineVarAsExpressionResult.InstanceVar );  //TailorDirective
		
		assertTrue( BindMLParser.parseMark("{$$removeEmptyLines()$$}" ) instanceof TailorDirective );  //TailorDirective
		
		assertTrue( BindMLParser.parseMark("{##a=1##}" ) instanceof DefineVar.StaticVar);  //DefineStaticVar, DefineStaticVarAsScriptResult		
		assertTrue( BindMLParser.parseMark("{##a:$count(blah)##}" ) instanceof DefineVarAsScriptResult.StaticVar );  //DefineStaticVar, DefineStaticVarAsScriptResult
		assertTrue( BindMLParser.parseMark("{##c:(( Math.sqrt( a * a + b * b ) ))##}" ) instanceof DefineVarAsExpressionResult.StaticVar );
		assertTrue( BindMLParser.parseMark("{##c=(( Math.sqrt( a * a + b * b ) ))##}" ) instanceof DefineVarAsExpressionResult.StaticVar );
		
		
		assertTrue( BindMLParser.parseMark("{@meta:data@}" ) instanceof SetMetadata );
		assertTrue( BindMLParser.parseMark("{$script()$}" ) instanceof EvalScript );
		assertTrue( BindMLParser.parseMark("{(( a + b ))}" ) instanceof EvalExpression );
		*/
	}
	
	public void testCompileAllMarks()
	{
		assertTrue( getOnlyMark( BindML.compile( "{+name+}" ) ) instanceof AddVarExpression );
		assertTrue( getOnlyMark( BindML.compile( "{+name*+}" ) ) instanceof AddVarExpression );
		assertTrue( getOnlyMark( BindML.compile( "{+name|default+}" ) ) instanceof AddVarExpression );
		assertTrue( getOnlyMark( BindML.compile( "{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}" ) ) instanceof AddVarExpression );
		
		assertTrue( getOnlyMark( BindML.compile( "{+((3 + 5))+}" ) ) instanceof AddExpressionResult );
		
		assertTrue( getOnlyMark( BindML.compile( "{#c:((3 + 5))#}" ) ) instanceof DefineVarAsExpressionResult.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{#c=((3 + 5))#}" ) ) instanceof DefineVarAsExpressionResult.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{##c:((3 + 5))##}" ) ) instanceof DefineVarAsExpressionResult.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{##c=((3 + 5))##}" ) ) instanceof DefineVarAsExpressionResult.StaticVar );
		
		
		assertTrue( getOnlyMark( BindML.compile( "{+$script()+}" ) ) instanceof AddScriptResult );
		assertTrue( getOnlyMark( BindML.compile( "{+$script()*+}" ) ) instanceof AddScriptResult );
		
		assertTrue( getOnlyMark( BindML.compile( "{+$script(params)+}" ) ) instanceof AddScriptResult );
		assertTrue( getOnlyMark( BindML.compile( "{+$script(params)*+}" ) ) instanceof AddScriptResult );
		
		assertTrue( getOnlyMark( BindML.compile( "{+?variable:addThis+}" ) ) instanceof AddIfVar );
		assertTrue( getOnlyMark( BindML.compile( "{- some text -}" ) ) instanceof Cut );
		
		assertTrue( getOnlyMark( BindML.compile( "{#a=1#}" ) ) instanceof DefineVar.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{#a:1#}" ) ) instanceof DefineVar.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{#a:$count(a)#}" ) ) instanceof DefineVarAsScriptResult.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{#a=$count(a)#}" ) ) instanceof DefineVarAsScriptResult.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{$$removeEmptyLines()$$}" ) ) instanceof TailorDirective );
		assertTrue( getOnlyMark( BindML.compile( "{##a=1##}" ) ) instanceof DefineVar.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{##a:1##}" ) ) instanceof DefineVar.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{##a:$count(blah)##}" ) ) instanceof DefineVarAsScriptResult.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{##a=$count(blah)##}" ) ) instanceof DefineVarAsScriptResult.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{@meta:data@}" ) ) instanceof SetMetadata );
		assertTrue( getOnlyMark( BindML.compile( "{@meta=data@}" ) ) instanceof SetMetadata );
		
		assertTrue( getOnlyMark( BindML.compile( "{$script()$}" ) ) instanceof EvalScript );
		assertTrue( getOnlyMark( BindML.compile( "{(( print(3) ))}" ) ) instanceof EvalExpression );
		
		assertTrue( getOnlyMark( BindML.compile( "{{+:{+fieldType+} {+fieldName+}+}}" ) ) instanceof AddForm );
		assertTrue( getOnlyMark( BindML.compile( "{{+?a==1: implements {+impl+}+}}" ) ) instanceof AddFormIfVar );
		assertTrue( getOnlyMark( BindML.compile( "{_+:{+fieldType+} {+fieldName+}+_}" ) ) instanceof AddForm );
		assertTrue( getOnlyMark( BindML.compile( "{_+?a==1: implements {+impl+}+_}" ) ) instanceof AddFormIfVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{{#assgn:{+fieldName+} = {+fieldValue+};#}}" ) ) instanceof DefineVarAsForm.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{{##assgn:{+fieldName+} = {+fieldValue+};##}}" ) ) instanceof DefineVarAsForm.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{_#assgn:{+fieldName+} = {+fieldValue+};#_}" ) ) instanceof DefineVarAsForm.InstanceVar );

		assertTrue( getOnlyMark( BindML.compile( "{{#assgn={+fieldName+} = {+fieldValue+};#}}") ) instanceof DefineVarAsForm.InstanceVar );   
		assertTrue( getOnlyMark( BindML.compile( "{{##assgn={+fieldName+} = {+fieldValue+};##}}") ) instanceof DefineVarAsForm.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{_#assgn:{+fieldName+} = {+fieldValue+};#_}") ) instanceof DefineVarAsForm.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{_##assgn:{+fieldName+} = {+fieldValue+};##_}") ) instanceof DefineVarAsForm.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{_#assgn={+fieldName+} = {+fieldValue+};#_}") ) instanceof DefineVarAsForm.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{_##assgn={+fieldName+} = {+fieldValue+};##_}") ) instanceof DefineVarAsForm.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{{##className:IntFormOf{+count+}##}}") ) instanceof DefineVarAsForm.StaticVar );         	
		assertTrue( getOnlyMark( BindML.compile( "{_##className:IntFormOf{+count+}##_}") ) instanceof DefineVarAsForm.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{{##className=IntFormOf{+count+}##}}") ) instanceof DefineVarAsForm.StaticVar );        	
		assertTrue( getOnlyMark( BindML.compile( "{_##className=IntFormOf{+count+}##_}") ) instanceof DefineVarAsForm.StaticVar );		
	}
	
}
