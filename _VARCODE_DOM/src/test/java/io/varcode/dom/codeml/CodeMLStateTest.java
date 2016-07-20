package io.varcode.dom.codeml;

import io.varcode.Lang;
import io.varcode.Metadata;
import io.varcode.context.VarContext;
import io.varcode.dom.Dom;
import io.varcode.dom.codeml.CodeMLParseState;
import io.varcode.tailor.Tailor;
import junit.framework.TestCase;

public class CodeMLStateTest
    extends TestCase
{
	/**
	 * Verify that if I assign a Static Var
	 */
	public void testAssignStatic()
	{
		CodeMLParseState cms = new CodeMLParseState();
		
		//cms.completeMark( "/*{#$removeEmptyLines()}*/" , 0 );
		cms.completeMark( "/*{##a=1##}*/" , 0 );
		
		assertEquals( "1", cms.parseContext.get( "a" ) );
		
	}
    public void testTextOnly()
    {
        CodeMLParseState cmb = new CodeMLParseState(
            //CodeMLParser.INSTANCE,            
            new VarContext(),
            //ForMLCompiler.INSTANCE,
            new Metadata.SimpleMetadata() );
        
        cmb.addText( "A" );
        Dom cm = cmb.compile( Lang.JAVA );
        //System.out.println( cm.toString() );
        
        assertEquals( "A", cmb.compile( Lang.JAVA ).getMarkupText() ); //( cm, VarContext.of(  ) ) );
        
        //there are no marks
        assertTrue( cm.getAllMarkIndicies().cardinality() == 0 );
        assertTrue( cm.getAllVarNames( VarContext.of( ) ).size() == 0 );
        assertTrue( cm.getAllMarks().length == 0 );
        assertTrue( cm.getBlanksCount() == 0 );
        //assertTrue( cm.getForm( "any" ) == null );
        assertTrue( cm.getForms().length == 0 );
        //assertTrue( cm.getMarksByName( "any" ).length == 0 );
        //assertTrue( cm.getUniqueBindMarks().length == 0 );
    }

    public void testOneMarkOnly()
    {
        CodeMLParseState cmb = 
            new CodeMLParseState(  
                new VarContext(),
                new Metadata.SimpleMetadata() );        
        cmb.completeMark( "{+Name+}", 1 );
        Dom cm = cmb.compile( Lang.JAVA );
        //System.out.println( cm.toString() );
        
        assertEquals( "", Tailor.code( cm, VarContext.of(  ) ) );
        assertEquals( "one", Tailor.code( cm, VarContext.of( "Name", "one" ) ) );
        //assertTrue( BaseTailor.doAlter( cm, Pairs.of( "name", "one" ) ).equals( "One" ) );
        
        //there is one mark
        assertTrue( cm.getAllMarks().length == 1 );
        
        //it is a bound mark
        assertTrue( cm.getBlanksCount() == 1 );
        assertTrue( cm.getAllVarNames( VarContext.of() ).size() == 1 );
        assertTrue( cm.getAllVarNames( VarContext.of() ).contains( "Name" ) );
        
        assertTrue( cm.getAllMarkIndicies().cardinality() == 1 );
        //it is at [0]
        assertTrue( cm.getAllMarkIndicies().get( 0 ) == true );
        
        
        //assertTrue( cm.getForm( "any" ) == null );
        assertTrue( cm.getForms().length == 0 );
        assertEquals( cm.getAllVarNames( VarContext.of() ).size(), 1 );
        assertTrue( cm.getAllVarNames( VarContext.of() ).contains( "Name") );
        
        //assertTrue( cm.getMarksByName( "any" ).length == 0 );
        //assertTrue( cm.getMarksByName( "Name" ).length == 1 );
        //assertTrue( cm.getUniqueBindMarks().length == 1 );
    }
    
}
