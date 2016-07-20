package io.varcode.context.lib;

import javax.script.SimpleBindings;

import io.varcode.context.ExpressionEvaluator_JavaScript;
import io.varcode.context.lib.state.VarNameAccessRecorder;
import junit.framework.TestCase;

public class VarNameAccessRecorderTest 
	extends TestCase
{
	public void testRecordAccess()
	{
		SimpleBindings sb = new SimpleBindings();
		sb.put( "A", 100 );
		VarNameAccessRecorder pub = 
			new VarNameAccessRecorder( 
				sb );
		assertTrue( pub.getVarNamesRequested().size() == 0 ); 
		try		
		{
			ExpressionEvaluator_JavaScript.INSTANCE.evaluate( 
				pub, "100 + A" );
		}
		catch( Exception e )
		{
			//honey badger dont care
			//System.out.println( e );
			e.printStackTrace();
		}
		assertTrue( pub.getVarNamesRequested().contains( "A" ) );
		
		try		
		{
			ExpressionEvaluator_JavaScript.INSTANCE.evaluate( 
				pub, "100 + A + B" );
		}
		catch( Exception e )
		{
			//honey badger dont care
			//System.out.println( e );
			//e.printStackTrace();
		}
		assertTrue( pub.getVarNamesRequested().contains( "A" ) );
		assertTrue( pub.getVarNamesRequested().contains( "B" ) );
		
	}
}
