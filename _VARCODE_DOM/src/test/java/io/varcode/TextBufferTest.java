package io.varcode;

import io.varcode.text.SmartBuffer;
import io.varcode.text.TextBuffer.FillBuffer;
import io.varcode.text.TextBuffer.TranslateBuffer;
import io.varcode.text.TextBuffer.Translator;
import junit.framework.TestCase;

public class TextBufferTest
    extends TestCase
{

	public void testFillClasses()
	{
	}
	
    public void testFillBuffer()
    {
        FillBuffer fb = new FillBuffer();
        assertEquals( "", fb.toString() ); 
        fb.append( "a" );
        assertEquals( "a", fb.toString() );
        fb.append( "b" );
        assertEquals( "ab", fb.toString() );
        
        fb.append( null ); 
        assertEquals( "abnull", fb.toString() );        
    }
       
    public void testNullTranslateBuffer()
    {
        /** translate null into an empty string 
         * (rather than writing out "null" as text) */
        TranslateBuffer tb = 
            new TranslateBuffer( new Translator(){

                public String translate( Object source )
                {
                    if( source == null )
                    {
                        return "";
                    }
                    return source.toString();
                }
                
            }
        );
        assertEquals( "", tb.toString() );
        tb.append( null );
        assertEquals( "", tb.toString() );
        
        tb.append( "a" );
        assertEquals( "a", tb.toString() );
        tb.append( null );
        assertEquals( "a", tb.toString() );
        
        tb.append( "b" );
        assertEquals( "ab", tb.toString() );        
    }
    
    public void testUnescapeCommentBuffer()
    {
        SmartBuffer tb = new SmartBuffer();
        tb.append( null );
        assertEquals( "", tb.toString() );
        
        tb.append("BLAH");
        assertEquals( "BLAH", tb.toString() );
        
        tb.append("BLAH");
        assertEquals( "BLAHBLAH", tb.toString() );
        
        tb.clear();
        assertEquals( "", tb.toString() );
        
    }
    
    public void testUnescapeCommentBufferTags()
    {
        SmartBuffer tb = new SmartBuffer();
        tb = SmartBuffer.createInstance();
        tb.append( "/+*" );
        assertEquals( "/*", tb.toString() );
        
        tb = new SmartBuffer();
        tb.append( "*+/" );
        assertEquals( "*/", tb.toString() );
        
        tb.clear();
        
        assertEquals( "", tb.toString() );
        
        tb.append( "/+**" );        
        assertEquals( "/**", tb.toString() );
        
    }
}
