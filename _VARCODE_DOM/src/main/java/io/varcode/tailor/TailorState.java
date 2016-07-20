package io.varcode.tailor;

import io.varcode.context.VarContext;
import io.varcode.dom.Dom;
import io.varcode.text.TextBuffer;

/**
 * The State maintained when tailoring the {@code Dom} via {@code Mark}s
 * and {@code VarContext})  
 * 
 *  @author M. Eric DeFazio eric@varcode.io
 */
public class TailorState
{
	/** the immutable {@code Dom} containing 
	 * {@code Mark}s and {@code FillInTheBlanks.FillTemplate} */
    private Dom dom;
     
    /** 
     * Bound input vars (name/value pairs), components and a mutable workspaces
     * for deriving / binding instance data used for filling in the
     * used whiled tailoring the {@code Dom}, contains:
     * <UL>
     * <LI>input data for deriving and filling in the {@code FillInTheBlanks.FillTemplate}
     * <LI>functionality ({@code ExpressionEvaluator}, {@code VarNameAudit} 
     * <LI>bound {@code VarScript}s 
     * <LI>{@code Metadata}
     * </UL> 
     */
    private VarContext tailorContext;
     
    /** 
     * Where the tailored document is written to
     *  
     */
    private TextBuffer textBuffer; 
    
    /**
     * Directives CAN be specified within the {@code Dom} AND/OR
     * in this fashion, (directives in the Dom and these in the 
     * TailorState will be evaluated.
     * 
     */
    private Directive[] directives; 
    
    public TailorState( 
        Dom dom, VarContext tailorContext, TextBuffer out, Directive...directives )
    {
        this.dom = dom;
        this.tailorContext = tailorContext;
        this.textBuffer = out;
        this.directives = directives;
    }

    public Directive[] getDirectives()
    {
    	return this.directives;
    }
    
    public void setDirectives( Directive[] directives )
    {
    	this.directives = directives;
    }
    
    public Dom getDom() 
	{
		return dom;
	}

	public void setDom( Dom dom ) 
	{
		this.dom = dom;
	}

	public VarContext getContext() 
	{
		return tailorContext;
	}
	
	public void setVarContext( VarContext varContext ) 
	{
		this.tailorContext = varContext;
	}

	public TextBuffer getTextBuffer() 
	{
		return textBuffer;
	}

	public void setTextBuffer( TextBuffer textBuffer ) 
	{
		this.textBuffer = textBuffer;
	}        
	
	public Directive[] getAllDirectives( ) 
	{
		//TailorDirective[] domDirectives = 
        //	tailorState.getDom().getTailorDirectives();
        Directive[] domDirectives = 
        	getDom().getDirectives( getContext() );
        
        //need to merge the DomDirectives to the TailorDirectives
        Directive[] tailorDirectives = getDirectives();
        
        Directive[] allDirectives = new Directive[ domDirectives.length + tailorDirectives.length ];
        System.arraycopy( domDirectives, 0, allDirectives, 0, domDirectives.length );
        System.arraycopy( tailorDirectives, 0, allDirectives, domDirectives.length, tailorDirectives.length );
        
		return allDirectives;
	}
}	