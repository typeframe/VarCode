package io.varcode.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.TestCase;

public class SmartBufferTest
	extends TestCase
{

	public void testPrimitiveArrayClasses()
	{
		SmartBuffer sb = new SmartBuffer();
		sb.append( new Class<?>[]
			{ boolean.class, char.class, int.class,float.class, byte.class, short.class, long.class, double.class} );		
		assertEquals( 
			"boolean, char, int, float, byte, short, long, double", sb.toString() );		
	}
	
	public void testJavaLangPackage()
	{
		SmartBuffer sb = new SmartBuffer();
		sb.append( new Class<?>[] { 
			Boolean.class, Character.class, Integer.class, Float.class, Byte.class, Short.class, Long.class, Double.class, String.class });
		
		assertEquals( 
			"Boolean, Character, Integer, Float, Byte, Short, Long, Double, String", sb.toString() );
	}
	
	public void testClassNonJavaLang() 
	{
		SmartBuffer sb = new SmartBuffer();
		sb.append( new Class<?>[] { Map.class, UUID.class} );
		assertEquals( "java.util.Map, java.util.UUID", sb.toString() );
	}
	
	public void testArray()
	{
		SmartBuffer sb = new SmartBuffer();
		sb.append( new int[0] );
		assertEquals("", sb.toString() );
		sb.clear();
		
		sb.append( new int[] {1,2,3,4,5,6} );
		assertEquals( "1, 2, 3, 4, 5, 6", sb.toString() );
		sb.clear();
		assertEquals( "", sb.toString() );
		
		sb.append( new char[] {'a','b','c'} );
		assertEquals( "a, b, c", sb.toString() );
		
	}
	
	public void testCollection()
	{
		SmartBuffer sb = new SmartBuffer();
		List<Integer>iList = new ArrayList<Integer>();
		
		sb.append( iList );
		
		assertEquals( "[]", sb.toString() );
		sb.clear();
		
		iList.add( 1 );
		sb.append( iList );
		assertEquals( "[1]", sb.toString() );
		sb.clear();
		
		iList.add( 2 );
		sb.append( iList );
		assertEquals( "[1, 2]", sb.toString() );
		
		
	}
}
