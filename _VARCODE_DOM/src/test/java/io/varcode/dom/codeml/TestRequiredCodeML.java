package io.varcode.dom.codeml;

import io.varcode.context.ResultRequiredButNull;
import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.dom.codeml.CodeML;
import io.varcode.tailor.Tailor;
import junit.framework.TestCase;

/**
 * Test that Required Works as Expected for BindML
 * 
 * @author eric
 */
public class TestRequiredCodeML
	extends TestCase
{
	/** 
	 * Verify that an attempt to tailor the markup will fail
	 * with a precise RequiredButNull Exception
	 * 
	 * @param mark the mark to Compile and Tailor
	 */
	private static void verifyThrows( String mark )
	{
		try
		{
			Tailor.code( CodeML.compile( mark ), new VarContext() );
			fail( "expected RequiredButNull" );
		}
		catch( VarRequiredButNull rbn )
		{	
			//expected
			System.out.println(rbn );
		}
	}

	private static void verifyThrowsNullResult( String mark )
	{
		try
		{
			Tailor.code( CodeML.compile( mark ), new VarContext() );
			fail( "expected RequiredButNull" );
		}
		catch( ResultRequiredButNull rbn )
		{	
			//expected
			//System.out.println(rbn );
		}
	}
	
	public void testTags()
	{
		verifyThrows( "{+requiredButNull*+}" ); // var is not bound
		//required                      ^
		
		verifyThrows( "/*{+requiredButNull*+}*/" ); // var is not bound
		//required                        ^
		
		verifyThrows( "/*{+requiredButNull**/replace/*+}*/" ); // var is not bound
		//required                        ^
		
		verifyThrows( "{+$script()*+}" ); // script is not bound
		//required                ^
		
		verifyThrows( "/*{+$script()*+}*/" ); // script is not bound
		//required                  ^
		
		verifyThrows( "/*{+$script(*/replace/*)*+}*/" ); // script is not bound
		//required                             ^
		
		verifyThrowsNullResult( "{+$count(notFound)*+}" ); //the var is not bound (result is null)
		//required                                 ^
		verifyThrowsNullResult( "/*{+$count(notFound)*+}*/" ); //the var is not bound (result is null)
		//required                                   ^
		
		verifyThrows( "/*{{+:{+fieldType*+} {+fieldName+}+}}*/" );
		//required                      ^
		
		verifyThrows( "/*{{+:{+fieldType+} {+fieldName*+}+}}*/" );
		//required                                    ^
		
		verifyThrowsNullResult( "/*{{+:{+fieldType+} {+fieldName+}*+}}*/" );
		//required                                                ^
		
		verifyThrows( "/*{_+:{+fieldType*+} {+fieldName+}+_}*/" );
		//required                      ^
		
		verifyThrows( "/*{_+:{+fieldType+} {+fieldName*+}+_}*/" );
		//required                                    ^
		
		
		assertEquals( "",
			Tailor.code( CodeML.compile( "/*{_+:{+fieldType+} {+fieldName+}*+_}*/" ), new VarContext() ) );

	}
}
