package io.varcode.context;

import junit.framework.TestCase;

public class VarScopeBindingsTest
    extends TestCase
{

    /** 
     * verify that the "lowest scope" wins... we 
     * 
     */
    public void testOverride()
    {
        ScopeBindings vsb = new ScopeBindings();
        assertEquals( null, vsb.get( "name" ) );
        
        vsb.put( "name", "eric", VarScope.GLOBAL );
        
        assertEquals( "eric", vsb.get( "name" ) );
        
        assertEquals( "eric", vsb.get( "name", VarScope.GLOBAL ) );
        
        vsb.put( "name", "donnie", VarScope.CORE );
        
        assertEquals( "donnie", vsb.get( "name" ) );
        assertEquals( "donnie", vsb.get( "name", VarScope.CORE ) );
        
        assertEquals( "eric", vsb.get( "name", VarScope.GLOBAL ) );
        
        
        vsb.put( "name", "roger", VarScope.LOOP );
        
        assertEquals( "roger", vsb.get( "name" ) );
        assertEquals( "roger", vsb.get( "name", VarScope.LOOP ) );
        assertEquals( "donnie", vsb.get( "name", VarScope.CORE ) );
        
        assertEquals( "eric", vsb.get( "name", VarScope.GLOBAL ) );        
    }
    
}
