package io.varcode.text;

import java.lang.reflect.Array;

/**
 * A TextBuffer that is designed to appropriately serialize
 * Objects (and nulls) with the idea that the target is Java Code
 * 
 * The SmartBuffer tries to write out "the right thing"
 * to the buffer... for instance
 * <UL>
 *   <LI>If the input text has escaped comment tags sequences ("/+*", "*+/") 
 *       it will convert them to "/*" "* /" 
 *   <LI>If the input is null, will print "" (empty string) not null
 *   <LI>If the input is a Java primitive Class( int.class, short.class, long.class...)
 *      will print "int"
 *   <LI>If the input is a Class that is in the "java.lang" package, it will print the "Simple name"
 *       i.e. if the input is String.class, will print "String", not "java.lang.String"
 *   <LI>        
 */
public class SmartBuffer
	implements TextBuffer
{
	public static SmartBuffer createInstance()
    {
		return new SmartBuffer();
    }
	
    private final TranslateBuffer buffer;
    
    public SmartBuffer()
    {
    	this.buffer =                             
    		new TranslateBuffer(
    			JavaSimpleClassTranslator.INSTANCE, 
                    new TranslateBuffer( CommentTagTranslator.INSTANCE ) );
    }
    
    @Override
    public TextBuffer append( Object input )
    {
    	if( input == null )
        {
    		return this;
    	}
        if( input.getClass().isArray() )
        {
        	int len = Array.getLength( input );
        	
        	for( int i = 0; i < len; i++ )
        	{
        		if( i > 0 )
        		{
        			buffer.append( ", " );
        		}
        		buffer.append( Array.get( input, i ) ); 
        	}
        	return this;
        }
        buffer.append( input );
        return this;
    }

    public String toString()
    {
    	return buffer.toString();
    }
        
    @Override
    public TextBuffer clear()
    {
    	this.buffer.clear();
        return this;
    }
        
    /**
     * TODO this implementation is SUBOPTIOMAL to say the least
     * fix later
     *
     */
    public enum CommentTagTranslator
    	implements Translator
    {
    	INSTANCE;
    	
        private static boolean isCharAt( String source, int index, char expected )
        {
        	if( source == null )
            {
        		return false;
            }
            if( source.length() <= index ||  index < 0 )
            {
            	return false;
            }
            return source.charAt( index ) == expected;
        }
            
        @Override
        public String translate( Object source )
        {
        	if( source == null )
            {
        		return "";
            }
            int sourceIndex = 0;
            String theString = source.toString();
            int indexOfStar = theString.indexOf( '*' );
            StringBuilder sb = new StringBuilder();
                
            while( indexOfStar >= 0 && sourceIndex < theString.length() )
            {
            	if( isCharAt( theString, indexOfStar - 1, '+' )
                    && isCharAt( theString, indexOfStar - 2, '/' ) ) //translate "/+*" to "/*"
                {
            		sb.append( theString.substring( sourceIndex, indexOfStar - 2 ) );
                    sb.append( "/*" );
                    sourceIndex = indexOfStar + 1;
                }
                else if( isCharAt( theString, indexOfStar + 1, '+' )
                    && isCharAt( theString, indexOfStar + 2, '/' ) ) //translate "*+/" to "*/"
                {
                	sb.append( theString.substring( sourceIndex, indexOfStar ) );
                    sb.append( "*/" );
                    sourceIndex = indexOfStar + 3;
                }
                else
                {
                	sb.append( theString.substring( sourceIndex, indexOfStar + 1 ) );
                    sourceIndex = indexOfStar + 1;
                }
                indexOfStar = theString.indexOf( '*', sourceIndex );
            }
            sb.append( theString.substring( sourceIndex ) );
            return sb.toString();
        }            
    }
    
    /**
     * Translates a Java Class to be Serialized to be just a Simple name
     * 
     * For Example:
     * <UL>
     *   <LI> JavaSimpleClassTranslator.INSTANCE.translate( int.class ); // = int
     *   <LI> JavaSimpleClassTranslator.INSTANCE.translate( String.class ); // = String
     *   <LI> JavaSimpleClassTranslator.INSTANCE.translate( java.util.HashMap.class ); // = HashMap
     *   <LI> JavaSimpleClassTranslator.INSTANCE.translate( io.varcode.Lang.class ); // = Lang
     * </UL>  
     */
    public enum JavaSimpleClassTranslator
        implements Translator
    {
        INSTANCE;
        
        @Override
        public Object translate( Object source )
        {
            if( source == null )
            {
                return "";
            }
            if( source instanceof Class )
            {
                Class<?> clazz = (Class<?>)source;
                
                if( !clazz.isPrimitive() 
                	&& clazz.getPackage().getName().equals( "java.lang" ) )
                {
                	return clazz.getSimpleName();
                }
                return clazz.getCanonicalName();
            }
            return source;
        }        
    }       
}