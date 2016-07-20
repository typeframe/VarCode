package io.varcode.context.lib.text;

import io.varcode.context.lib.text.Quote;
import junit.framework.TestCase;

public class QuoteTest
	extends TestCase
{
	public static final String N = System.lineSeparator();
	
	
	public void testQuote()
	{
		
		String s = (String)Quote.doDoubleQuote(" 	Hello " +  N  + "There" );
		//System.out.println( s );
		
		System.out.println( s );
		assertEquals( " \tHello \r\nThere", s );
		
		System.out.println( s );
		
	}

}
