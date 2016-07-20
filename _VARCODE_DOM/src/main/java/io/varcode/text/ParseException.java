package io.varcode.text;

import io.varcode.VarException;

/**
 * Encountered an error parsing Text/Strings 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ParseException
    extends VarException
{
    private static final long serialVersionUID = 5977136945829189773L;

    public ParseException( String message, Throwable throwable )
    {
        super( message, throwable );
    }

    public ParseException( String message )
    {
        super( message );
    }

    public ParseException( Throwable throwable )
    {
        super( throwable );
    }    
}
