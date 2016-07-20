package io.varcode.context.lib.math;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

public enum RandomValue
	implements VarScript
{
	INSTANCE;
	
	private final XORShift32 randomInt;
	
	private final XORShift31 randomPositiveInt;
	
	private final XORShift64 randomLong;
	
	private final XORShift63 randomPositiveLong;
	
	private RandomValue()
	{
		randomInt = new XORShift32( (int) System.nanoTime() );
		randomPositiveInt = new XORShift31( randomInt );
		
		randomLong = new XORShift64( System.nanoTime() );
		randomPositiveLong = new XORShift63( randomLong );
	}
	
	@Override
	public ScriptInputParser getInputParser() 
	{
		return VarScript.STRING_INPUT;
	}

	@Override
	public Object eval( VarContext context, String input ) 
	{
		if( input.equals( "+int" ) )
		{
			return randomPositiveInt.nextInt();
		}
		if( input.equals( "int" ) )
		{
			return randomInt.nextInt();
		}		
		if( input.equals( "+long" ) )
		{
			return randomPositiveLong.nextLong();
		}
		if( input.equals( "long" ) )
		{
			return randomLong.nextLong();
		}		
		if( input.startsWith( "[" ) && input.endsWith( "]" ) )
		{
			int commaIndex = input.indexOf( ',' );
			if( commaIndex < 0 )
			{
				return randomPositiveInt.nextInt();
			}
			try
			{
				long min = Long.parseLong( input.substring( 1, commaIndex ) );
				long max = Long.parseLong( input.substring( commaIndex + 1, input.length() -1 ) );
				return randomPositiveLong.nextLong( max - min ) + min;
			}
			catch( Exception e )
			{
				return randomPositiveInt.nextInt();
			}
		}
		return randomPositiveInt.nextInt();
	}

	/** Creates a 31 bit (positive int) pseudo random */
	public static class XORShift31
	{
		private final XORShift32 internal;
		
		public XORShift31( XORShift32 internal ) 
		{
			this.internal = internal;
		}
		
		public final int nextInt()
		{
		    return internal.nextInt() >>> 1;
		}	
		
		public int nextInt( int max ) 
		{
		    return nextInt() % max; 
		}
	}
	
	/** Fast 32 bit pseudo-random int */
	public static class XORShift32
	{
		int x;
		
		public XORShift32( int seed ) 
		{
			x = seed == 0 ? 0xdeadbeef : seed;
		}
		
		public final int nextInt()
		{
		    x ^= ( x <<  13 );
		    x ^= ( x >>> 17 );
		    x ^= ( x <<  15 );
		    return x;
		}			
	}
	
	/** Fast 64 bit pseudo-random long */
	public static class XORShift64 
	{
		long x;
	
		public XORShift64( long seed ) 
		{
			x = seed == 0 ? 0xdeadbeef : seed;
		}
		
		long nextLong() 
		{
			x ^= ( x << 21 );
			x ^= ( x >>> 35 );
			x ^= ( x << 4 );
			return x;
		}
	}
	
	/** Fast 63 bit (positive long) pseudo-random long */
	public static class XORShift63 
	{
		private final XORShift64 internal;
	
		public XORShift63( XORShift64 internal ) 
		{
			this.internal = internal;
		}
		
		long nextLong() 
		{
			return internal.nextLong() >>> 1;
		}
		
		public long nextLong( long max ) 
		{
		    return nextLong() % max; 
		}
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}
