package io.varcode.context.lib.text;

import java.util.BitSet;

import io.varcode.Metadata;
import io.varcode.context.VarBindings;
import io.varcode.dom.Dom;
import io.varcode.dom.FillInTheBlanks;
import io.varcode.dom.FillInTheBlanks.FillTemplate;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.Mark.WrapsContent;
import io.varcode.tailor.Directive;
import io.varcode.tailor.TailorState;

/**
 * Removes ALL {@code Mark}s from the {@code Dom} 
 * (returns only text) 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum StripMarks
	implements Directive
{
	INSTANCE;
	
	public static String stripFrom( Dom markup )
	{
		FillTemplate allMarksTemplate = markup.allMarksTemplate; 
		Mark[] markActions = markup.getAllMarks();
		
		int blanksCount = allMarksTemplate.getBlanksCount();
		StringBuilder sb = new StringBuilder();
		sb.append( allMarksTemplate.getTextBeforeBlank( 0 ) );
		for( int i = 0; i < blanksCount; i++ )
		{
			if( markActions[ i ] instanceof WrapsContent )
			{
				WrapsContent wc = (WrapsContent)markActions[ i ];
				sb.append( wc.getWrappedContent() );
			}
			sb.append( allMarksTemplate.getTextAfterBlank( i ) );
		}		
		return sb.toString();
	}
	
	public static String stripAndCut( Dom dom )
	{
		FillTemplate allMarksTemplate = dom.allMarksTemplate; 
		Mark[] markActions = dom.getAllMarks();
		
		int blanksCount = allMarksTemplate.getBlanksCount();
		StringBuilder sb = new StringBuilder();
		sb.append( allMarksTemplate.getTextBeforeBlank( 0 ) );
		for( int i = 0; i < blanksCount; i++ )
		{
			if( markActions[ i ] instanceof WrapsContent )
			{				
				WrapsContent wc = (WrapsContent)markActions[ i ];
				sb.append( wc.getWrappedContent() );
			}
			sb.append( allMarksTemplate.getTextAfterBlank( i ) );
		}		
		return sb.toString();
	}
	

	@Override
	public void preProcess( TailorState tailorState ) 
	{
		String markupWithoutMarks = 
			stripFrom( tailorState.getDom() );
		
		Dom markupSansMarks = 
			new Dom( 
				tailorState.getDom().getLanguage(),	
				new FillInTheBlanks.Builder( markupWithoutMarks ).compile(),				
				new Mark[ 0 ],
		        new BitSet(), 
		        new VarBindings(),
		        new Metadata.SimpleMetadata() );		
		tailorState.setDom( markupSansMarks );
	}

	@Override
	public void postProcess( TailorState tailorState ) 
	{
		//nothing to post process
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}
