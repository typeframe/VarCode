package io.varcode.context.lib.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import io.varcode.context.VarContext;
import junit.framework.TestCase;

public class ImportClassesFromTest
	extends TestCase
{

	public void testNoInput()
	{
		Class<?>[] classes = 
			(Class<?>[]) ImportClassesFrom.INSTANCE.eval(VarContext.of(), "" );
		assertTrue( classes.length == 0 );
	}
	
	public void testOneNullInput()
	{
		Class<?>[] classes = 
			(Class<?>[]) ImportClassesFrom.INSTANCE.eval( VarContext.of(), "name" );
		assertTrue( classes.length == 0 );
	}
	
	public void testOneInput()
	{
		Class<?>[] classes = 
			(Class<?>[]) ImportClassesFrom.INSTANCE.eval( VarContext.of( "name", 1 ), "name" );
		assertTrue( classes.length == 0 );
	}
	public void testPrimitivesJavaLangInput()
	{
		Class<?>[] classes = 
			(Class<?>[]) ImportClassesFrom.INSTANCE.eval( 
				VarContext.of( 
					"int", 1,
					"float", 1.2f,
					"double", Math.PI,
					"short" , (short)123,
					"byte", (byte)12,
					"long", 123123876123987L ), "int,float,double,short,byte,long" );
		assertTrue( classes.length == 0 );
	}
	
	public void testPrimitiveArraysJavaLangInput()
	{
		Class<?>[] classes = 
			(Class<?>[]) ImportClassesFrom.INSTANCE.eval( 
				VarContext.of( 
					"int", new int[]{1,1,2,3,4},
					"float", new float[] {1.2f, 2.4f, 4.5f},
					"double", new double[] {12.3d, 65.764d},
					"short" , new short[] { (short)123, (short)Short.MAX_VALUE},
					"byte", new byte[]{ (byte)12, (byte)1 },
					"long", new long[]{ 123123876123987L, 12L} ), 
				"int,float,double,short,byte,long" );
		assertTrue( classes.length == 0 );
	}
	
	/** If the var Value is a Class, add a Class */
	public void testAClass()
	{
		Class<?>[] classes = 
			(Class<?>[]) ImportClassesFrom.INSTANCE.eval( 
				VarContext.of( 
					"map", java.util.HashMap.class ), 
				"map" );
		
		assertTrue( classes.length == 1 );
		assertEquals( java.util.HashMap.class, classes[ 0 ] );		
	}
	
	public void testArrayOfClasses()
	{
		/*{$importClassesFrom(map,arr)$}*/
		Class<?>[] classes = 
			(Class<?>[])ImportClassesFrom.INSTANCE.eval( 
				VarContext.of( 
					"map", java.util.HashMap.class,
				    "arr", new Class<?>[] { 
						List.class, HashSet.class, UUID.class, HashMap.class } ),  
					"map,arr" );
		
		assertTrue( classes.length == 4 );
		//assertEquals( java.util.HashMap.class, classes[ 0 ] );		
	}
	
}
