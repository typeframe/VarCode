package io.varcode.text;

/**
 * Pluggable Buffer which receives (and optionally translates) input text
 * into an expandable Buffer
 * 
 * Could be "enhanced" to provide Memory Mapped Files or File Streaming
 * implementations (current implementations write to an in memory StringBuilder
 * for simplicity)
 *  
 * TODO: DO I want to translate "null" (String) into ""?
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface TextBuffer
{
	/** Append the input to the Buffer and return the buffer
	 * Logic here should convert null into "" (empty String)
	 * and handle other things like handling arrays  
	 * (printing out the content verses a "LString..." address
	 */
    TextBuffer append( Object input );
    
    /**
     * Clears the contents of the buffer
     * @return
     */
    TextBuffer clear();
        
    /** Simple In Memory Buffer for filling with Text (into a StringBuilder) */
    public static final class FillBuffer
        implements TextBuffer
    {
        public final StringBuilder buffer;
        
        public FillBuffer()
        {
            this( new StringBuilder() );
        }
        
        public FillBuffer( StringBuilder stringBuilder )
        {
            this.buffer = stringBuilder;
        }
        
        @Override
        public TextBuffer append( Object text )
        {
            buffer.append( text );
            return this;
        }

        public String toString()
        {
        	return buffer.toString();
        }
        
        @Override
        public TextBuffer clear()
        {
            buffer.delete( 0, buffer.length() );
            return this;
        }        
    }
        
    /** Translates some input source text and returns the translated output text*/
    public interface Translator
    {
        /** given the source, translate and return the translation*/
        Object translate( Object source );
    }
    
    /** A Fill Buffer that replaces tokens on input 
     * before adding things to the Text Buffer
     */
    public static class TranslateBuffer
        implements TextBuffer
    {
        private final TextBuffer buffer;
        
        private final Translator translator;
        
        public TranslateBuffer( Translator translator )
        {
            this( translator, new FillBuffer() );
        }
        
        public TranslateBuffer( Translator translator, TextBuffer buffer )
        {
            this.buffer = buffer;
            this.translator = translator;
        }
        
        @Override
        public TextBuffer append( Object input )
        {
            this.buffer.append( translator.translate( input ) );
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
    }    
}
