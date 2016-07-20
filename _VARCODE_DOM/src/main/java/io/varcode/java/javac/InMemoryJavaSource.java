package io.varcode.java.javac;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import io.varcode.Lang;
import io.varcode.java.JavaCodeMetadata;
import io.varcode.java.JavaNaming;

/**
 * Encapsulates a compile-able Java unit of source code (a ".java" file)
 * for integrating with the {@code SimpleJavaFileObject} to be "fed" into
 * the Javac compiler Tool at Runtime (to convert from source to a class 
 * bytecodes)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class InMemoryJavaSource
    extends SimpleJavaFileObject
    //implements CodeUnit
{    
    /** the fully qualified name of the Source created */
    public String className;
    
    /** the "actual" source code text that was tailored */
    public String code;

    public final JavaCodeMetadata javaCodeMetadata;
    
    public CharSequence getCharContent( boolean ignoreEncodingErrors ) 
    {
        return code;
    }
    
    public InMemoryJavaSource( String className, String sourceCode )
    {
        super( 
            URI.create( "string:///" + className.replace('.', '/') + Kind.SOURCE.extension ), 
            Kind.SOURCE );
        
        this.className = className;
        this.code = sourceCode;
        this.javaCodeMetadata = new JavaCodeMetadata();
    }
    
    public InMemoryJavaSource( String packageName, String className, String sourceCode )
    {
        super( 
            URI.create( 
                "string:///" + ( packageName + "." + className ).replace('.', '/') 
                + Kind.SOURCE.extension ), 
            Kind.SOURCE );
        this.className = JavaNaming.ClassName.toFullClassName( packageName, className );
        this.code = sourceCode;
        this.javaCodeMetadata = new JavaCodeMetadata();
    }
    
    public String getClassName()
    {
        return className;
    }

    public Lang getLanguage()
    {
        return Lang.JAVA;
    }

    public String getCode()
    {
        return code;
    }

    /** 
     * Gets the relative File Path that this code would reside 
     * based on the package hierarchy of the fully qualified class name.
     */
    public String getRelativeFilePath()
    {
        return JavaNaming.ClassName.toSourcePath( this.className );
    }

    public String getCodeId()
    {
        return className + ".java";
    }
    
    public String toString()
    {
        return code;
    }

    public JavaCodeMetadata getMetadata()
    {
        return this.javaCodeMetadata;
    } 
}
