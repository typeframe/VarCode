package io.varcode.java.javac;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

/**
 * A FileManager that "fronts"/"spoofs" a normal file System
 * and instead reads Java Classes from stored/in-memory byte arrays. 
 * 
 * (Any classes not found in the "extended" File Manager are "forwarded"
 * to the "parent" fileManager {@see ForwardingJavaFileManager} 
 * 
 * The idea is to give the illusion that Tailored classes 
 * (stored in memory as a bytes) are treated the same as  
 * ".class" files Loaded from the File System or on the classpath. 
 * 
 * This DOES incur extra memory cost (of keeping the Class in memory)
 * but does not require classes to be created and written to disk before
 * being Loaded by the ClassLaoder {@see TailoredClassLoader} 
 * 
 * This is adapted from: 
 * <A HREF="https://github.com/trung/InMemoryJavaCompiler">InMemoryJavaCompiler</A>
 * Created by trung on 5/3/15.
 */
public class InMemoryJavaWorkspace 
    extends ForwardingJavaFileManager<JavaFileManager> 
{
	/** Map from a Class name to the In Memory (ByteCode) Class*/
    private final Map<String, InMemoryJavaClass> classNameToCompiledCode = 
    	new HashMap<String, InMemoryJavaClass>();
    
    private final InMemoryJavaClassLoader inMemoryClassLoader;

    /**
     * Creates a new instance of ForwardingJavaFileManager.
     *
     * @param fileManager delegate to this file manager
     * @param tailoredClassLoader
     */
    public InMemoryJavaWorkspace(
        JavaFileManager fileManager, 
        List<InMemoryJavaClass> tailoredClasses, 
        InMemoryJavaClassLoader tailoredClassLoader) 
    {
        super( fileManager );
        this.inMemoryClassLoader = tailoredClassLoader;
        for( int i = 0; i < tailoredClasses.size(); i++)
        {
        	classNameToCompiledCode.put(
        		tailoredClasses.get( i ).getName(), tailoredClasses.get( i ) );
        	this.inMemoryClassLoader.introduce( tailoredClasses.get( i ) );
        }        
    }
    
    public Map<String, InMemoryJavaClass> getClassNameToClass()
    {
    	return classNameToCompiledCode;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
        JavaFileManager.Location location, 
        String className, 
        JavaFileObject.Kind kind, 
        FileObject sibling ) 
        throws IOException 
    {
    	return classNameToCompiledCode.get( className );
    }

    @Override
    public ClassLoader getClassLoader( JavaFileManager.Location location ) 
    {
        return inMemoryClassLoader;
    }
}