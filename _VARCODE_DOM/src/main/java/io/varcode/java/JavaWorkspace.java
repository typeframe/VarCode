package io.varcode.java;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import io.varcode.VarException;
import io.varcode.java.javac.InMemoryJavaClass;
import io.varcode.java.javac.InMemoryJavaClassLoader;
import io.varcode.java.javac.InMemoryJavaSource;
import io.varcode.java.javac.InMemoryJavaWorkspace;
import io.varcode.java.javac.InMemoryJavac;
import io.varcode.java.javac.JavacException;
import io.varcode.java.javac.JavacOptions;

/**
 * A Workspace contains 1...N "tailored" java Files which are
 * constructed / compiled together (there may be dependencies between the 
 * Java Source Code being created)
 * 
 * For example:
 * <PRE>
 *   I have a {@code Markup} "io.typeframe.type._StrictType.java" that 
 *   will be tailored into (3) cases (of _StrictType):
 *   <UL>
 *     <LI>MaritalStatusType (MST)( maps SINGLE , MARRIED, WIDOWED, DIVORCED to 0b00, 0b01, 0b10, 0b11 )  
 *     <LI>AgeType (AT)( accepts a number between 1 and 127)
 *     <LI>GenderType (GT)(accepts MALE or FEMALE )
 *   </UL>  
 *   I have another {@code Markup} "io.typeframe.row._BitField" 
 *   to be tailored into (3) cases( of _BitField) 
 *   <UL>
 *     <LI>MaritalStatusField (MSF) of MaritalStatusType mapped to 
 *        the first (2) bits [0,1] in a 64-bit frame
 *     <LI>AgeField (AF) of AgeType mapped to (7) bits [3,4,5,6,7,8,9] of a 64-bit frame
 *     <LI>GenderField (GF)of GenderType mapped to (1) bit [10] of a 64-bit frame.
 *   </UL>     
 *   I have another {@code Markup} "io.typeframe.row._RowFrame64"
 *   to be tailored into (1) case (of _RowFrame64) called:
 *    "PersonDetailRow"
 *       which references all the Tailored _BitFields {MaritalStatusType, AgeType, GenderType}
 *       and all the Fields {MaritalStatusField, AgeField, GenderField}.
 *   
 *   So the dependencies are:
 *   PersonDetailRow -> {MaritalStatusType, AgeType, GenderType, MaritalStatusField, AgeField, GenderField}
 *   MaritalStatusField -> MaritalStatusType
 *   AgeField -> AgeType
 *   GenderField -> GenderType
 *   
 *   to "compile" this workspace we pass in the 
 * </PRE>
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaWorkspace 
{
	;
	
	public static SourceWorkspace of( String name )
	{
		return new SourceWorkspace( name );
	}
	
	public static class SourceWorkspace
	{
		/** Name of the workspace*/
		public String name;
		
		/** Java source files of the Workspace */
		private final List<InMemoryJavaSource> javaSourceFiles = 
			new ArrayList<InMemoryJavaSource>();
		
		
		private final List<InMemoryJavaClass> tailorClassTargets = 
			new ArrayList<InMemoryJavaClass>();	
		
		/**
		 * 
		 * @param className the fully qualified class name:
		 * (i.e. "java.util.HashMap") 
		 * @param sourceCode the text of the source code
		 */
		public SourceWorkspace addJavaSource( String className, String sourceCode )
		{
			InMemoryJavaSource code = 
				new InMemoryJavaSource( className, sourceCode );
			addJavaSource( code );
			return this;
		}
		
		/** adds a java source code to the Workspace */
		public SourceWorkspace addJavaSource( InMemoryJavaSource javaSourceCode )
		{
			javaSourceFiles.add( javaSourceCode );
			addTailorClassTargetFor( javaSourceCode.className );
			return this;
		}
		
		private void addTailorClassTargetFor( String className )
		{
			try	 
    		{
				tailorClassTargets.add(
					new InMemoryJavaClass( className ) );
    		}
			catch( IllegalArgumentException e ) 
    		{
    			throw new VarException( 
    				"Could not create (In Memory) Java Class for \"" 
    				+ className + "\"", e );
    		} 
    		catch( URISyntaxException e ) 
    		{
    			throw new VarException( 
    				"Could not create (In Memory) Java Class for \"" 
    				+ className + "\"", e );
    		}
		}
		public SourceWorkspace( 
			String workspaceName, 
			InMemoryJavaSource...javaSource )
		{
			this.name = workspaceName;
			     
	    	for( int i = 0; i < javaSource.length; i++ )
			{
	    		addJavaSource( javaSource[ i ] );								
			}	    	
		}
		
		/** 
		 * Compile the Workspace using the (optional) CompilerOptions
		 * and return the CompiledWorkspace
		 * 
		 * @param compilerOptions options passed to the JavacCompiler Tool (i.e. -
		 * to compile with Java version 1.6 pass in:
		 * CompilerOption.JavaSourceVersion.MajorVersion._1_6
		 * 
		 * @return the Compiled Workspace
		 */
		public CompiledWorkspace compile(
			JavacOptions.CompilerOption...compilerOptions )
		{
			InMemoryJavaClassLoader classLoader = 
		    	new InMemoryJavaClassLoader();	    	
		    
			StandardJavaFileManager baseFileManager = null; 
	    	try
	    	{
	    		baseFileManager = 
	    			InMemoryJavac.INSTANCE.JAVAC.getStandardFileManager( 
	    				null, //use default DiagnosticListener
	    				null, //use default Locale
	    				null );//use default CharSet 
	    	}
	    	catch( Exception e )
	    	{
	    		throw new JavacException (
	    			"JDK version 1.6 or greater (NOT a JRE) MUST BE used to compile "
	    		  + "Java  at Runtime, you are currently using \"" 
	    		  + System.getProperty( "java.version" ) 
	    		  + "\" make sure you have a JDK (NOT A JRE) running" ); 
	    	}
	    	
			InMemoryJavaWorkspace fileManager =  
		        new InMemoryJavaWorkspace(
		            baseFileManager, 
		            tailorClassTargets, 
		            classLoader );
		        
			Iterable<String>options = JavacOptions.optionsFrom( compilerOptions );
			
			DiagnosticCollector<JavaFileObject> diagnostics = 
			    new DiagnosticCollector<JavaFileObject>();
			
			CompilationTask task = InMemoryJavac.INSTANCE.JAVAC.getTask(
				null, //use System.err if the tool fails 
				fileManager, 
				diagnostics, 
				options, 
				null, // NO classes to be processed for Annotations (at this time) 
		 		javaSourceFiles );
			
			boolean compiledNoErrors = task.call();
			
	        if( !compiledNoErrors )
	        { 
	        	throw new JavacException( 
	        		name, 
	        		javaSourceFiles,  
	        		diagnostics );
	        }
	        	        
	        Map<String, Class<?>> loadedClasses = 
	        	new HashMap<String, Class<?>>();
	        
	        for( int i = 0; i < javaSourceFiles.size(); i++ )
	        {
	        	Class<?> tailoredClass = 
	        		classLoader.findClass( javaSourceFiles.get( i ).getClassName() );
	        	
	        	loadedClasses.put( javaSourceFiles.get( i ).getClassName(), tailoredClass );
	        }
	        	        
	        try
            {
        		fileManager.close();
            }
            catch( IOException ioe )
            {
            	//LOG.warn( "Error closing BaseFileManager", ioe);            	
            }
	        return new CompiledWorkspace( loadedClasses );       
		}	
	}
	
	public static class CompiledWorkspace
	{
		private final Map<String, Class<?>> classNameToClass;
		
		public CompiledWorkspace( Map<String, Class<?>>classNameToClass )
		{
			this.classNameToClass = classNameToClass;
		}
		
		public Set<String> getClassNames()
		{
			return classNameToClass.keySet();
		}
		
		public Class<?> getClass( String name )
		{
			return classNameToClass.get( name );
		}
	}
}
