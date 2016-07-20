package io.varcode.markup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.varcode.Lang;
import io.varcode.VarException;

/**
 * Given the SourceId of the source, determine the Path to the source file.
 * (NOTE: not all Languages Support packages / namespaces / modules
 * so (for instance) we might have to find a source file (for C):
 * <BLOCKQUOTE>"someCProgram"</BLOCKQUOTE>  
 * and we'd search for a file named "someCProgram.c"
 *  
 * Alternatively given a NameSpace (C#) Package (Java) or Fully qualified Class 
 * Name determine the path (i.e. directory hierarchy) to the resource like:
 * 
 * <BLOCKQUOTE>"com.mycompany.myproject.mycomponent.BlahComponent"</BLOCKQUOTE>
 * ...and (if we wish to "resolve" the source file) we need to find it within the
 * 
 * <BLOCKQUOTE>"com/mycompany/myproject/mycomponent"</BLOCKQUOTE>
 * directory. In a file called:
 * <BLOCKQUOTE>"BlahComponent.java"</BLOCKQUOTE>
 *   
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum LangNamespaceToMarkupPath    
{
    INSTANCE;
    
    private final Map<Lang, PathResolver>LANG_RESOLVER_MAP; 
    
    private LangNamespaceToMarkupPath()    
    {
        LANG_RESOLVER_MAP = new HashMap<Lang, PathResolver>();
        LANG_RESOLVER_MAP.put( Lang.JAVA, DotPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.CSHARP, DotPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.FSHARP, DotPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.D, DotPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.GROOVY, DotPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.KOTLIN, DotPathResolver.INSTANCE );
        
        //not hierarchial
        LANG_RESOLVER_MAP.put( Lang.RUST, FlatPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.OBJECTIVEC, FlatPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.C, FlatPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.CPP, FlatPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.JAVASCRIPT, FlatPathResolver.INSTANCE );
        LANG_RESOLVER_MAP.put( Lang.PHP, FlatPathResolver.INSTANCE ); 
                //use Acme\TestPackage\Service\FooGenerator; /slashPathResolver
    }
    
    /**
     * Resolves the Hierarchical Directory "path" to the sourceId:
     * for example:<BR>
     * <PRE>"java.util.Map.java"</PRE>
     * should be at path:<BR>
     * <PRE>"java/util/Map.java"</PRE>
     * ...and
     * <PRE>"someCProgram.c"</PRE>
     * doesnt have a heirarchial path, but should be in a file 
     * <PRE>"someCProgram.c"</PRE>
     * 
     * @param markupId identifies the Markup file to be loaded from the repo
     * @return the relative path to the markup file resource 
     */
    public String resolvePath( String markupId )
    {
        int lastDot = markupId.lastIndexOf( '.' );
        String fileExtension = markupId.substring( lastDot );
        Lang theLang = Lang.fromFileExtension( fileExtension );
        if( theLang == null )
        {
            throw new VarException(
                "Could not determine Lang from markupId with extension \""
              + fileExtension + "\"" );
        }
        PathResolver pathResolver = LANG_RESOLVER_MAP.get( theLang );
        if( pathResolver == null )
        {
            throw new VarException(
                "No PathResolver registered for Lang \"" + theLang +"\"" );
        }
        String path = pathResolver.pathTo( 
            markupId.substring( 0, markupId.length() - fileExtension.length() ) )
          + fileExtension;
        return path;                     
    }

    /** Rust modules uses "::" */
    

    public enum FlatPathResolver
        implements PathResolver
    {
        INSTANCE;

        @Override
        public String pathTo( String markupId )
        {
            return markupId;
        }        
    }
    
    public enum DotPathResolver
        implements PathResolver
    {
        INSTANCE;

        @Override
        public String pathTo( String markupId )
        {
            return markupId.replace( 
                '.', 
                File.separatorChar );
        }        
    }
    
    public interface PathResolver
    {
        public String pathTo( String markupId );
    }
    
    
}
