package io.varcode.dom.mark;

import io.varcode.dom.DomParseState;

/**
 * Populates values on the {@code MetaData} object when the varcode source
 * is being parsed by the {@code MarkupCompiler} 
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class SetMetadata
    extends Mark 
    implements Mark.BoundStatically
{
    private final String name;
    
    private final String value;
    
    public SetMetadata( 
        String text, 
        int lineNumber,
        String name,
        String value )
    {
        super( text, lineNumber );
        this.name = name;
        this.value = value;        
    }

    @Override
    public void onMarkParsed( DomParseState parseState )
    {
        parseState.getMetadata().put( name, value );        
    }
    
    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String toString()
    {
        return "@ " + name + " = " + value; 
    }
}