package io.varcode.context;

/**
 * Exception when binding data to Vars when Tailoring a {@code Dom}
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarBindException 
	extends EvalException 
{
	private static final long serialVersionUID = 5755043763680702571L;

	public VarBindException( String message, Throwable throwable ) 
	{
		super( message, throwable );
	}

	public VarBindException( String message ) 
	{
		super( message );
	}
	
	public VarBindException( Throwable throwable ) 
	{
		super( throwable );
	}
}
