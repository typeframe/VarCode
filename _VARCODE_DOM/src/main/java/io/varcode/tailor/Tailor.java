package io.varcode.tailor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.varcode.Metadata;
import io.varcode.Metadata.SimpleMetadata;
import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.dom.Dom;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.BoundDynamically;
import io.varcode.text.SmartBuffer;

/**
 * Specialize the {@code Dom} using functionality and data bound to 
 * the {@code VarContext} to build "tailored" documents. 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Tailor
{
    ; //singleton enum idiom
    
	/*{-?((removeLog==true)):*/
	private static final Logger LOG = 
        LoggerFactory.getLogger( Tailor.class );
	/*-}*/
	    
	public static String code(
    	Dom dom, Object...keyValuePairs )
    {
		return code( dom, VarContext.of( keyValuePairs ) );
    }
	
    public static String code( Dom dom, VarContext context, Directive...directives )
    {
    	TailorState tailorState = new TailorState( 
    		dom, 
    		context, 
    		SmartBuffer.createInstance(), 
    		directives );
    	
    	tailor( tailorState );
    	return tailorState.getTextBuffer().toString();
    }
    
    public static TailorState tailor( Dom dom, VarContext context )
    {
    	TailorState tailorState = new TailorState( 
    		dom, 
    		context, 
    		SmartBuffer.createInstance() );
    	return tailor( tailorState );
    }
    
    /**
     * Tailors the {@code VarCode} using the {@code Context} 
     * to the output {@code CharSequence}
     * @param tailorState the state used for compiling the Markup
     * and tailoring the source
     * @return the updated TailorState
     */ 
    public static TailorState tailor( 
        TailorState tailorState )
        throws VarException 
    {
    	LOG.trace( "1) Initialize metadata" );
    	Metadata metadata = (Metadata)tailorState.getContext().getMetadata();
    	if( metadata == null )
    	{
    		metadata = new SimpleMetadata();
    		tailorState.getContext().set( VarContext.METADATA_NAME, metadata );
    	}
    	metadata.merge( tailorState.getDom().getMetadata() );

    	LOG.trace( "2) Initialize static vars" );
        tailorState.getContext()
        	.getOrCreateBindings( VarScope.STATIC )
        	.putAll( tailorState.getDom().getStaticBindings() );
        
        LOG.trace( "3) Pre-process directives" );
        Directive[] allDirectives = tailorState.getAllDirectives( );   
        
        if( allDirectives.length > 0 )
        {
        	//if( LOG.isTraceEnabled() ) { LOG.trace( "   Pre-processing (" + allDirectives.length + ") directives " ); }
        	for( int i = 0; i < allDirectives.length; i++ )
        	{
        		if( LOG.isTraceEnabled() ) { LOG.trace( "   pre-process [" + i + "]: " + allDirectives[ i ] ); }
        		allDirectives[ i ].preProcess( tailorState );
        	}
        }
        
        LOG.trace( "4) Derive instance vars" );
        Mark[] marks =  tailorState.getDom().getAllMarks(); 
        for( int i = 0; i < marks.length; i++ )
        {   //derive and bind all the dynamically defined Vars in the VarContext
            if( marks[ i ] instanceof Mark.BoundDynamically )
            {
            	if( LOG.isTraceEnabled() ) { LOG.trace( "  derive: \"" + marks[ i ].text + "\"" ); }
                BoundDynamically dynamicBound = (BoundDynamically)marks[ i ];
                dynamicBound.bind( tailorState.getContext() ); //this will derive the var, then update the context
            }
            //it might be derived but not bound (i.e. input validation scripts)
            else if ( marks[ i ] instanceof Mark.Derived 
                && !( marks[ i ] instanceof BlankFiller ) ) //was Bind
            {
            	if( LOG.isTraceEnabled() ) { LOG.trace( "  derive: \"" + marks[ i ].text + "\"" ); }
                Mark.Derived dd = (Mark.Derived) marks[ i ];
                dd.derive( tailorState.getContext() );
            }
        }        
        LOG.trace( "5) Fill-in template" );
        BlankFiller[] blankFillers = tailorState.getDom().getBlankFillers();
        Object[] fillSequence = new Object[ blankFillers.length ];
        for( int i = 0; i < blankFillers.length; i++ )
        {
        	if( LOG.isTraceEnabled() ) { LOG.trace( "   fill[" + i + "]: \"" + blankFillers[ i ].toString() +"\"" ); }
            fillSequence[ i ] = blankFillers[ i ].derive( tailorState.getContext() );
        }
        tailorState.getDom().getFillTemplate().fill( tailorState.getTextBuffer(), fillSequence );
        
        //5) Post Processing All Directives
        
        //NOTE: one or more Directives COULD have added/removed Directives in the {@code TailorState}
        // so we ask the {@code TailorState} to "get" all the {@code Directives} AGAIN instead
        // of relying on the predefined {@code allDirectives} from step 2)
        LOG.trace( "6) Post-process directives" );
        allDirectives = tailorState.getAllDirectives( );
        if( allDirectives.length > 0 )
        {
        	//if( LOG.isTraceEnabled() ) { LOG.trace( "Post-Processing (" + allDirectives.length + ") Directives " ); }
        	for( int i = 0; i < allDirectives.length; i++ )
        	{
        		if( LOG.isTraceEnabled() ) { LOG.trace( "   post-process[" + i + "]: " + allDirectives[ i ] ); }
        		allDirectives[ i ].postProcess( tailorState );
        	}
        }             
        return tailorState;
    }
}
