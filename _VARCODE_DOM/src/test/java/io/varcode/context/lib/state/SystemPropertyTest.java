package io.varcode.context.lib.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.varcode.context.VarContext;
import junit.framework.TestCase;

public class SystemPropertyTest
	extends TestCase
{
	private static final Logger LOG = 
        LoggerFactory.getLogger( SystemPropertyTest.class );
    
	public void testSysProp()
	{
		VarContext vc = new VarContext();
		String sys = System.getProperty( "user.dir" );
		Object res = vc.getVarScript( "sysProp" ).eval( vc,  "user.dir" ) ;
		assertEquals( sys, res );
		LOG.info( res.toString() );
	}

}
