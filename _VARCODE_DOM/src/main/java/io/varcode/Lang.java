package io.varcode;

/**
 * Markup source can be in multiple programming languages
 * this encapsulates the high level language conventions.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Lang
{
    JAVA      ( "Java",        ".java" ),
    JAVASCRIPT( "JavaScript",  ".js" ),
    C         ( "C",           ".c" ),
    CPP       ( "C++",         ".cpp" ),
    CSHARP    ( "C#",          ".cs" ),
    D         ( "D",           ".d" ),
    FSHARP    ( "F#",          ".fs" ),
    GO        ( "Go",          ".go" ),
    GROOVY    ( "Groovy",      ".groovy"),
    KOTLIN    ( "Kotlin",      ".kt" ),
    //LUA       ( "Lua",         ".lua" ), //need a custom parser we'll get to these later...
    OBJECTIVEC( "Objective-C", ".m" ),
    //PASCAL    ( "Pascal",      ".pas" ), //need a custom parser we'll get to these later...
    PHP       ( "PHP",         ".php" ),
    RUST      ( "Rust",        ".rs" ),
    SWIFT     ( "Swift",       ".swift" ),
    VERILOG   ( "Verilog",     ".v" );
    
    /** the common name of the language*/
    private final String name;
    
    /** the file extension used by the language for source files*/
    private final String sourceFileExtension;
    
    private Lang( String name, String sourceFileExtension)
    {
        this.name = name;
        this.sourceFileExtension = sourceFileExtension;
    }

    /**
     * return the Lang from the File Extension
     * @param fileExtension (i.e. ".java", ".rs", , ".js")
     * @return the Lang
     */
    public static Lang fromFileExtension( String fileExtension )
    {
        for( int i = 0; i < Lang.values().length; i++ )
        {
            if( Lang.values()[ i ].getSourceFileExtension().equals( fileExtension ) )
            {
                return Lang.values()[ i ];
            }
            //they MAY have passed in "js" instead of ".js" (that works too)
            if( Lang.values()[ i ].getSourceFileExtension().endsWith( fileExtension ) )
            {
                return Lang.values()[ i ];
            }
        }
        return null;
    }
    
    /**
     * Given a codeId (i.e. io.typeframe.field.BitField32.java), returns the
     * appropriate Lang
     * 
     * @param codeId (i.e. "io.typeframe.field.BitField32.java")
     * @return the Lang (or null if not recognized)
     */
    public static Lang fromCodeId( String codeId )
    {
    	 for( int i = 0; i < Lang.values().length; i++ )
         {
             if( codeId.endsWith( Lang.values()[ i ].getSourceFileExtension() ) )
             {
                 return Lang.values()[ i ];
             }
         }
         return null;
    }
    
    public String getName()
    {
        return name;
    }

    public String getSourceFileExtension()
    {
        return sourceFileExtension;
    } 
}
