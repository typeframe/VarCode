package io.varcode.context;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

public class VarContextTest
    extends TestCase
{
    /**
     * 
     * 
     * make sure I can call all the base Scripts 
     * @throws java.text.ParseException */
    public void testBaseScripts() 
    {
        VarContext bc = VarContext.of( 
            "name", "eric", 
            "allCaps", "ALLCAPS",
            "someNum", 1,
            "arr", new String[]{ "A","B", "C" }  );
        
        //VarScript e = bc.getVarScript( "!className" );
        //assertTrue( e != null ); 
        //assertEquals( "eric", e.eval( bc, "name" ) );
        //try
       // {
       //     e.eval( bc, "someNum" );
       //     fail( "expected exception for invalid class Name " );
       // }
        //catch( Exception ex )
       // {
            //expected
        //}
        
        
        VarScript e = bc.getVarScript( "firstCap" );
        assertEquals ("Eric", e.eval( bc, "name" ) );
        
        e = bc.getVarScript( "^" );
        assertEquals ("Eric", e.eval( bc, "name" ) );
        
        e = bc.getVarScript( "cap" );
        assertEquals ( "ERIC", e.eval( bc, "name" ) );
        
        e = bc.getVarScript( "^^" );
        assertEquals ("ERIC", e.eval( bc, "name" ) );
        
        e = bc.getVarScript( "lower" );
        assertEquals ("allcaps", e.eval( bc, "allCaps" ) );
        
        e = bc.getVarScript( "quote" );
        assertEquals ("\"eric\"", e.eval( bc, "name" ) );
        
        e = bc.getVarScript( "count" );
        assertEquals ( 1, e.eval( bc, "name" ) );
        assertEquals ( null, e.eval( bc, "unknown" ) );
        assertEquals ( 3, e.eval( bc, "arr" ) );
        
        e = bc.getVarScript( "#" );
        assertEquals ( 1, e.eval( bc, "name" ) );
        assertEquals ( null, e.eval( bc, "unknown" ) );
        assertEquals ( 3, e.eval( bc, "arr" ) );
        
        e = bc.getVarScript( "indexCount" );
        int[] indexCount = (int[]) e.eval( bc, "name" );
        assertTrue(indexCount.length == 1);
        assertTrue(indexCount[0] == 0);
        
        //assertEquals ( new int[]{0}, e.eval( bc, "name" ) );
        assertEquals ( null, e.eval( bc, "unknown" ) );
        
        indexCount = (int[]) e.eval( bc, "arr" );
        assertTrue(indexCount.length == 3);
        assertTrue(indexCount[0] == 0);
        assertTrue(indexCount[1] == 1);
        assertTrue(indexCount[2] == 2);
        
        e = bc.getVarScript( "[#]" ); //indexCount
        indexCount = (int[]) e.eval( bc, "name" );
        assertTrue(indexCount.length == 1);
        assertTrue(indexCount[0] == 0);
        
        //assertEquals ( new int[]{0}, e.eval( bc, "name" ) );
        assertEquals ( null, e.eval( bc, "unknown" ) );
        
        indexCount = (int[]) e.eval( bc, "arr" );
        assertTrue(indexCount.length == 3);
        assertTrue(indexCount[0] == 0);
        assertTrue(indexCount[1] == 1);
        assertTrue(indexCount[2] == 2);
        
        e = bc.getVarScript( "date" );
        assertTrue( e != null );
        String dateFormat = "yyyy-MM-dd";
        String aDate = (String) e.eval( bc, dateFormat );        
        SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
        
        try
        {
            sdf.parse( aDate );
        }
        catch( java.text.ParseException pe )
        {
            fail("expected date to be parseable");
        }
        
        //vb.put( "indexCount", Element.COUNT_INDEX );
        //vb.put( "countIndex", Element.COUNT_INDEX );
        
        //vb.put( "date", DateTime.DATE_TIME );    
        
    }
    /** This is how I'd do a VarCase*/
    public void testVarCase()
    {
        VarContext vc = VarContext.of(
            "log", "trace",
            "className", "MyMarks",
            "uuid", new VarScript( )
            {
                public Object eval( VarContext context, String input )
                {
                    return UUID.randomUUID();
                }

				@Override
				public ScriptInputParser getInputParser() 
				{
					return ScriptInputParser.InputIgnored.INSTANCE;
				}                
            },
            "name", new String[]{ "a","b" } );
        assertTrue( vc.getVarScript( "uuid" ) instanceof VarScript );
        //System.out.println( vc.getAttribute( "uuid" ) );
        //System.out.println( vc.getEval( "uuid" ) );
    }
    
    public void testContextNoAttributes()
    {
        VarContext vc = new VarContext();
        assertTrue( vc.get( "name" ) == null );
        for( int i = 0; i < VarScope.values().length; i++ )
        {
            assertTrue( vc.get( "name", VarScope.values()[ i ] ) == null );
            assertTrue( vc.get( "name", VarScope.values()[ i ].getValue() ) == null );
            /*
            if( VarScope.values()[ i ] != VarScope.CORE_LIBRARY 
            	&& VarScope.values()[ i ] != VarScope.CORE	)
            {   //there should be NO vars accept for the CORE and CORE LIBRARY
                assertEquals( null, vc.getBindings( VarScope.values()[ i ].getValue() ) );
            }
            */
            //assertTrue( vc.getBindings( VarScope.values()[ i ] )== null);
        }
        assertTrue( vc.getScopeOf( "name" ) == -1 );
        
        
        List<Integer>scopes = vc.getScopes();
        
        for( int i = 0; i < scopes.size(); i++ )
        {
            assertTrue( VarScope.getAllScopeValues().contains( scopes.get( i ) ) );
        }        
    }
    
    public void testPopulateVarContext()
    {
        VarContext vc = new VarContext();
        vc.set( "first", "A", VarScope.INSTANCE );
        assertEquals( "A", vc.get( "first" ) );
        assertEquals( "A", vc.get( "first", VarScope.INSTANCE ) );
        
        assertEquals( null, vc.get( "first", VarScope.LOOP ) );
        assertEquals( null, vc.get( "first", VarScope.GLOBAL ) );
        
        assertEquals( null, vc.get( "first", VarScope.LOOP.getValue() ) );
        assertEquals( null, vc.get( "first", VarScope.GLOBAL.getValue() ) );
        assertEquals( null, vc.get( "first", 1 ) );
        
        assertEquals( VarScope.INSTANCE.getValue(), vc.getScopeOf( "first" ) );
        
        assertEquals( null, vc.getBindings( VarScope.GLOBAL ) );
        
        VarBindings instanceB = vc.getOrCreateBindings( VarScope.INSTANCE );
        assertTrue( instanceB.containsKey( "first" ) );
        
        assertEquals ( instanceB, vc.getBindings( VarScope.INSTANCE.getValue() ) );
        
        assertEquals( null, vc.clear( "first", VarScope.CORE.getValue() ) );
        assertEquals( "A", vc.clear( "first", VarScope.INSTANCE.getValue() ) );
        
        vc.set( "first", "LOOP SCOPE", VarScope.LOOP );
        vc.set( "first", "GLOBAL", VarScope.GLOBAL );
        
        //verify that LOWER SCOPES TAKE PRECEDENCE OVER HIGHER SCOPES
        assertEquals( "LOOP SCOPE", vc.get( "first" ) );
        vc.getBindings( VarScope.LOOP ).clear();
        
        //verify that when I remove a variable at the lower scope then query the 
        //higher scope takes over
        assertEquals( "GLOBAL", vc.get( "first" ) );
        
        VarBindings proj = vc.getOrCreateBindings( VarScope.PROJECT );
        
        assertEquals( null, proj.get( "first" ) );
        proj.put( "first", "PROJECT" );      
    }
    
    public void testVarScripts()
    {
        VarContext vc = new VarContext();
        VarBindings core = vc.getOrCreateBindings( VarScope.CORE );
        core.put( "empty", new VarScript()
        {
            public Object eval( 
                VarContext context, String input )
            {
                return "";
            }
            
			@Override
			public ScriptInputParser getInputParser() 
			{
				return ScriptInputParser.InputIgnored.INSTANCE;
			} 
        });
        
        //verify that I can get the transform  out of the map
        VarScript ct = (VarScript)vc.get( "$empty" );
        VarScript mt = vc.getVarScript( "empty" );
        assertEquals( ct, mt );
        assertEquals( "", ct.eval( vc, "this is some source to blank" ) );        
    }
    
    public void testScripts()
    {
        VarContext vc = new VarContext();
        VarBindings core = vc.getOrCreateBindings( VarScope.CORE );
        core.put( "nine", 
            new VarScript()
            {
                @Override
                public Object eval( VarContext context, String input )
                {
                    return "9";
                }                 
                
				@Override
				public ScriptInputParser getInputParser() 
				{
					return ScriptInputParser.InputIgnored.INSTANCE;
				} 
            });
        VarScript fn = vc.getVarScript( "nine" );
        assertEquals( "9", fn.eval( vc, "" ) );        
    }
    
}
