package io.varcode.java;

import io.varcode.markup.DirectoryRepo;
import io.varcode.markup.MarkupPath;
import io.varcode.markup.MarkupRepo;

/**
 * Where/How to FIND the Java Markup Source Code 
 * to Compile into a Dom.
 * 
 * This provides some "conventional" places where the source code might be
 * (as it relates to System Properties, specifically "user.dir" and "markup.dir")
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaMarkupPath
    implements MarkupRepo
{
    INSTANCE;
    
    public static final DirectoryRepo MARKUP_DIRECTORY = 
        new DirectoryRepo( System.getProperty( "user.dir" ) + "/markup/" );
    
    public static final DirectoryRepo SRC_DIRECTORY = 
        new DirectoryRepo( System.getProperty( "user.dir" ) + "/src/" );
    
    public static final DirectoryRepo SRC_MAIN_JAVA_DIRECTORY = 
       new DirectoryRepo( System.getProperty( "user.dir" ) + "/src/main/java/" );
    
    public static final DirectoryRepo TEST_DIRECTORY = 
        new DirectoryRepo( System.getProperty( "user.dir" ) + "/test/" );
    
    public static final DirectoryRepo SRC_TEST_JAVA_DIRECTORY = 
       new DirectoryRepo( System.getProperty( "user.dir" ) + "/src/test/java/" );
    
    /**
     * Where to look for Java Source that Corresponds to a specific class 
     */
    public static final MarkupPath SOURCE_PATH = 
       new MarkupPath( 
           MARKUP_DIRECTORY, 
           SRC_DIRECTORY, 
           SRC_MAIN_JAVA_DIRECTORY, 
           TEST_DIRECTORY,
           SRC_TEST_JAVA_DIRECTORY );

    @Override
    public MarkupStream markupStream( String markupId )
    {
        String markupDir = System.getProperty( "markup.dir" );
        if( markupDir != null )
        {
            DirectoryRepo dr = new DirectoryRepo( markupDir );
            MarkupStream ms = dr.markupStream( markupId );
            if( ms != null )
            {
                return ms;
            }
        }
        return SOURCE_PATH.markupStream( markupId );
    }

    /** 
     * given the Class looks in the "usual places" on the Path
     * to return the Source markup Stream 
     * @param localClass the local Class
     * @return the markupStream
     */
    public MarkupStream markupStream( Class<?> localClass )
    {
    	 return markupStream( localClass.getCanonicalName() + ".java" );
    }
    
    @Override
    public String describe()
    {
        return SOURCE_PATH.describe();
    } 
}
