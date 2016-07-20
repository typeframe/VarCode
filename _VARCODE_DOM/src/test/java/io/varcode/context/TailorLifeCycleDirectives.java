package io.varcode.context;

import io.varcode.tailor.TailorState;

/** 
 * These will Userp Tailor Directives in some regards
 * You can still define directives in the marks
 * 
 * BUT this allows you to change the behavior within the Lifecycle
 * 
 */
public interface TailorLifeCycleDirectives 
{
	public enum TailorStage
	{
		INIT, //Start Timer, etc. Send Start Event
		BEFORE_DERIVING, //Strip Variables, Import Libraries, Start Monitor, Export
		BEFORE_FILLING, //Validation Routines		
		AFTER_FILLING // Formatting, Verification, Lexing, Parsing, etc.  
	}
	
	public boolean isEnabled( TailorStage stage );	
	
	public void update( TailorState tailorState );
}
