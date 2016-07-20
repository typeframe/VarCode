package io.varcode.dom;

import java.util.BitSet;
import java.util.Set;

import io.varcode.context.VarContext;
import io.varcode.dom.FillInTheBlanks.FillTemplate;
import io.varcode.dom.form.VarForm;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.Mark.BlankFiller;

/**
 * A Compiled DOM (Document Object Model) containing {@code Mark}s
 * which provides an API to to perform logic and late Binding
 * (Fills) within the Document. (Similar to a W3C HTML DOM is manipulated by
 * code with JQuery)
 * 
 * <UL>
 *  <LI>Compile-Time: 
 *      <UL>
 *       <LI>read in the text "markup" (line-by-line)  
 *       <LI>parse {@code Mark} (objects) into the Dom 
 *       <LI>reserve "blanks" for any {@code Mark.BlankFiller}s (to be filled at "Tailor-Time")
 *       <LI>derive / define (static) vars for Marks using : 
 *          <UL>
 *            <LI>{@code VarScript}s            
 *            <LI>{@code TailorDirective}s
 *            <LI>{@code VarForm}s
 *           </UL> 
 *        <LI>populate any {@code Metadata} for any {@code Mark}s            
 *      </UL> 
 *  <LI>Tailor-Time (Runtime):
 *    <UL>
 *      <LI>receive a {@code VarContext} containing any vars /components/ scripts needed for "tailoring"
 *      <LI>Copy all statically defined vars (from the {@code Dom} to the {@code VarContext})
 *      <LI>Pre-process all {@code TailorDirective}s
 *      <LI>Derive / Define all (instance) vars
 *      <LI>Fill in all reserved "blanks" with the Derived vars / forms
 *      <LI>Post Process all {@code TailorDirective}s    
 *    </UL>  
 * </UL>
 * @see Mark
 * @see VarContext
 * @see VarForm
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface FillDom
{
    /** gets of the {@code MarkAction}s embedded in the code */
    Mark[] getAllMarks();

    /** gets the names of all of the vars */
    Set<String> getAllVarNames( VarContext context );

    /** {@code Mark.BlankFiller}s (fill text into the {@Dom} at Tailor-Time */
    BlankFiller[] getBlankFillers();

    /** the number of var fills within the Marked code */
    int getBlanksCount();

    /** get the indices for all {@Mark}s within markup */
    BitSet getAllMarkIndicies();

    /** 
     * char indexes of "Blanks" within static text associated with 
     * {@code FillAction} Marks. 
     */
    FillTemplate getFillTemplate();
}