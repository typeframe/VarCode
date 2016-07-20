package io.varcode.markup;

import io.varcode.markup.LangNamespaceToMarkupPath;
import io.varcode.markup.LangNamespaceToMarkupPath.FlatPathResolver;
import junit.framework.TestCase;

public class LangNamespaceToMarkupPathTest
	extends TestCase
{
	public void testPaths()
    {
        assertEquals( 
        	"com\\oracle\\jrockit\\jfr\\AClass.java",
        	LangNamespaceToMarkupPath.INSTANCE.resolvePath( 
                	"com.oracle.jrockit.jfr.AClass.java" ) );
        
        assertEquals( "AVXInstructions.c",		
        	LangNamespaceToMarkupPath.INSTANCE.resolvePath( 	
                FlatPathResolver.INSTANCE.pathTo( "AVXInstructions.c" ) ) );
        
        assertEquals( "AVXISA.anotherV.c",		
            	LangNamespaceToMarkupPath.INSTANCE.resolvePath( 	
                    FlatPathResolver.INSTANCE.pathTo( "AVXISA.anotherV.c" ) ) );
        
    }

}
