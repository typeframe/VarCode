package io.varcode.dom;

import io.varcode.Metadata;
import io.varcode.context.VarContext;

/**
 * Parse State as Markup is being read/Parsed/Compiled into the {@code Dom} 
 * 
 * State that allows immutable variables and scripts to be 
 * assigned at parse/compile-time.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface DomParseState
	extends ParseState
{
	/** sets the value of a Static var to a Value at "compile-time" */
    void setStaticVar( String varName, Object value );
    	
    /** ParseState Metadata (about the markup/Dom) */
    Metadata getMetadata(); 
        
    /** 
     * Statically defined vars that have been encountered during 
     * Parsing/Compilation of the {@code Markup} to the {@code Dom}.
     * 
     * <I>(This allows static vars that are defined to reference/use/access 
     * previously defined static vars that have been defined when the 
     * Markup is being read/ parsed/ compiled into the Dom.</I>   
     */
     VarContext getParseContext();
     
     
}