package io.varcode.context.lib.java;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import junit.framework.TestCase;

public class TypeNameTest
	extends TestCase 
{

	public void testValidate()
	{
		try
		{
			ValidateTypeName.INSTANCE.eval( VarContext.of( ), "DOESNT EXIST" );
			fail("Expected VarException for Type that doesnt exist");
		}
		catch( VarException ve )
		{
			//expected
		}
		
	}
}
