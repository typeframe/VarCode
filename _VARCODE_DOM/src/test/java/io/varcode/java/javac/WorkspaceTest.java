package io.varcode.java.javac;

import io.varcode.java.JavaWorkspace;
import io.varcode.java.JavaWorkspace.CompiledWorkspace;
import io.varcode.java.JavaWorkspace.SourceWorkspace;
import io.varcode.java.javac.JavacException;
import io.varcode.java.javac.JavacOptions;
import junit.framework.TestCase;

/** 
 * Tests how to use a Workspace
 * 
 */
public class WorkspaceTest
	extends TestCase
{
	
	public void testOneFile()
	{
		SourceWorkspace sws = JavaWorkspace.of( "A alone" );
		sws.addJavaSource( "A", "public class A {}" );
		CompiledWorkspace cw = sws.compile( );
		Class<?> AClass = cw.getClass( "A" );
		assertTrue( AClass != null );
	}
	
	public void testOneFileCompileFailure()
	{
		try
		{
			JavaWorkspace.of( "EXPECT COMPILER EXCEPTION" )
		    	.addJavaSource( "A", "asdfklhjasdjklf" )
		    	.compile( );
			fail( "Expected Compiler Exception" );
		}
		catch( JavacException ce )
		{
			ce.printStackTrace();
			System.out.println( ce );
		}
	}

	public static String N = System.lineSeparator();
	
	public void testTwoClasses()
	{
		CompiledWorkspace cw = 
			JavaWorkspace.of( "A AND B")
			    .addJavaSource( 
			        "A", 
			        "public class A" + N 
			       +"{" + N
			       +"    private B theB;" + N
			       +"    public A( B theB )" 
			       +"    {" + N 
			       +"        this.theB = theB;" + N
			       +"    }" + N 
			       +"}" )
			   .addJavaSource( 
			       "B",
			       "public class B" + N
			       +"{" + N
			       +"    public B( )" 
			       +"    {" + N 
			       +"    }" + N 
			       +"}" )
			   .compile( );
		assertNotNull( cw.getClass("A") );
		assertNotNull( cw.getClass("B") );
	}

	// verify that if: 
	// A is dependent on B and 
	// B is dependent on A 
	// ...it still compiles
	public void testTwoClassesCycle()
	{
		CompiledWorkspace cw = 
			JavaWorkspace.of( "A AND B")
			    .addJavaSource( 
			        "A", 
			        "public class A" + N 
			       +"{" + N
			       +"    private B theB;" + N
			       +"    public A( B theB )" + N 
			       +"    {" + N 
			       +"        this.theB = theB;" + N
			       +"    }" + N 
			       +"}" )
			   .addJavaSource( 
			       "B",
			       "public class B" + N
			       +"{" + N
			       +"    private A a;" + N
			       +"    public B( )" +N 
			       +"    {" + N 
			       +"    }" + N 
			       +"}" )
			   .compile( );
		assertNotNull( cw.getClass( "A" ) );
		assertNotNull( cw.getClass("B") );
	}
	
	public void testCompilerException()
	{
		SourceWorkspace sw = 
			JavaWorkspace.of( "EXCEPTION ON LINE 3" )
			.addJavaSource( "ExceptionLine3",
				"public class ExceptionLine3 {" + N 
			   +"    public int a = 0;" + N
			   +"    public Exception here; " + N
			   +"}" );
		
		try
		{
			sw.compile( ); 
		}
		catch( JavacException e )
		{
			//System.out.println( e );
			//verify that the exception returns a messae
			assertTrue( e.getMessage().contains( "line[ 3 ]" ) );
			assertTrue( e.getMessage().contains( "Exception here;" ) );
		}
	}
	
	public void testTwoClassesFailCompilerOption()
	{
		SourceWorkspace sw = JavaWorkspace.of( "COMPILER EXCEPTION EXPECTED")
	    .addJavaSource( 
	        "A", 
	        "public class A" + N 
	       +"{" + N
	       +"    private B theB;" + N
	       +"    public A( B theB )" + N 
	       +"    {" + N 
	       +"        this.theB = theB;" + N
	       +"    }" + N 
	       +"}" )
	   .addJavaSource( 
	       "B",
	       "import java.util.*;" + N 
	       +"public class B" + N
	       +"{" + N
	       +"    public List<String> a;" + N
	       +"    public B( )" +N 
	       +"    {" + N 
	       +"    }" + N 
	       +"}" );
		
		//this wil compile just fine if Source compatibility >= 1.5
		assertNotNull( sw.compile(JavacOptions.JavaSourceVersion.MajorVersion._1_5 ) );
				
		try
		{   //this will fail (since I have a Generic and source version 1.4 compiler option
			sw.compile( JavacOptions.JavaSourceVersion.MajorVersion._1_4 );			
			fail( "expected Compiler Exception using Generics for  Source Version 1.4" );
		}
		catch( JavacException e )
		{
			System.out.print( e );
		}		
	}
}
