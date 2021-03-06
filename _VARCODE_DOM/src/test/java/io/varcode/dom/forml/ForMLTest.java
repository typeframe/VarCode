package io.varcode.dom.forml;

import static io.varcode.dom.forml.ForML.Markup.*;

import junit.framework.TestCase;

public class ForMLTest
	extends TestCase
{

	public void testGenMarks()
	{
		assertEquals( "{+((3+4))+}", 
			addExpressionResult( "3+4" ) );
		
		assertEquals( "{+?log:import org.slf4j.Logger;+}", 
			addIfVar( "log", "import org.slf4j.Logger;" ) );
		
		assertEquals( "{+?log==true:import org.slf4j.Logger;+}", 
			addIfVar( "log", "true", "import org.slf4j.Logger;" ) );
		
		assertEquals( "{+?log==true:import org.slf4j.Logger;+}", 
			addIfVar( "log==true", "import org.slf4j.Logger;" ) );
		
		assertEquals( "{+$count()+}", 
			addScriptResult( "count" ) );
		
		assertEquals( "{+$count()*+}", 
			addScriptResult( "count", true ) );
		
		assertEquals( "{+$count()*+}", 
			addScriptResult( "count()", true ) );
		
		assertEquals( "{+$count(a)+}", 
			addScriptResult( "count(a)" ) );
		
		assertEquals( "{+$count(a)+}", 
			addScriptResult( "count", "a" ) );
		
		assertEquals( "{+$count(a)+}", 
			addScriptResult( "count", "a", false ) );
		
		assertEquals( "{+$count(a)*+}", 
			addScriptResult( "count", "a", true ) );
		
	}
}
