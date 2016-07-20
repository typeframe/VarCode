package io.varcode.context.lib.state;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

public enum DateTime
{
    ; //singleton enum idiom
    
    public static final FormatDate DATE_FORMAT = new FormatDate();
    
    public static final TimeMillis TIME_MILLIS = new TimeMillis();
    
    /** 
     * Retrieves the current Date and
     * (optionally) using a SimpleDateFormat to format the date 
     */
    public static class FormatDate
        implements VarScript
    {
        private FormatDate() 
        { }
        
        public Object eval( VarContext context, String input )
        {            
            if( input != null && input.trim().length() > 0 )
            {
                SimpleDateFormat sdf = new SimpleDateFormat( input  );
                return sdf.format( new Date() );
            }
            return new Date();            
        }

		@Override
		public ScriptInputParser getInputParser() 
		{
			return ScriptInputParser.InputString.INSTANCE;
		}    
		
		public String toString()
		{
			return this.getClass().getName() + "." + super.toString();
		}
    }
    
    public static class TimeMillis
    	implements VarScript
    {
        private TimeMillis()
    	{ }
    	
    	public Object eval( VarContext context, String input )
        {
    		return System.currentTimeMillis();            
        }

 		@Override
 		public ScriptInputParser getInputParser() 
 		{
 			return ScriptInputParser.InputIgnored.INSTANCE;
 		} 
 		
 		public String toString()
 		{
 			return this.getClass().getName() + "." + super.toString();
 		}
    }
    
    public static class TimeNanos
		implements VarScript
	{
    	private TimeNanos()
    	{ }
	
    	public Object eval( VarContext context, String input )
    	{
    		return System.nanoTime();            
    	}

		@Override
		public ScriptInputParser getInputParser() 
		{
			return ScriptInputParser.InputIgnored.INSTANCE;
		}            	
		
		public String toString()
		{
			return this.getClass().getName() + "." + super.toString();
		}
	}
    

}
