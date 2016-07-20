package io.varcode.tailor;

public interface Directive
{
	/** pre process the Tailor State */
	void preProcess( TailorState tailorState);
	
	/** post process the Tailor State */
	void postProcess( TailorState tailorState );
	
	public static abstract class PostProcessor
		implements Directive
	{
		public void preProcess( TailorState tailorState )
		{  }
	}
	
	public static abstract class PreProcessor
		implements Directive
	{
		public void postProcess( TailorState tailorState )
		{  }
	}
}

