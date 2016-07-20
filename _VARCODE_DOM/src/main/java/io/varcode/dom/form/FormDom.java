/*
 * Copyright 2015 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.varcode.dom.form;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.varcode.context.VarContext;
import io.varcode.dom.FillInTheBlanks;
import io.varcode.dom.FillInTheBlanks.FillTemplate;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.HasForm;
import io.varcode.dom.FillDom;

/**
 * Variable Form's {@code Dom}
 *   
 * "Compiled" object instance for the {@code Markup} 
 * <A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup Language</A>.
 * <BR><BR>
 *    
 * Contains the static text (Code) along with {@code MarkAction}s 
 * to Specialize / Tailor new source code. 
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class FormDom
    implements FillDom
{
    /** ALL {@code MarkAction}s on the document */
    private final Mark[] actions;

    /** text and "blanks" where ALL {@code MarkAction}s occur */
    private final FillTemplate allMarkFormBlanks;

    /**
     * The character locations of *ALL* marks within the document. 
     * (even {@code Mark}s that are not bound to the tailored Template)
     * 
     * These {@code Mark}s: 
     * <UL>
     *   <LI>{@code CutComment} 
     *   <LI>{@code CutJavaDocComment}
     *   <LI>{@code CutCode}
     * </UL>
     * 
     * ...Are not "Bound" into the "Tailored" document, 
     * (and not Modeled within a {@code FillInTheBlanks.FillOrder} ) 
     * we keep track of the character index (within the varcode) 
     * where these {@code Mark}s occurred.
     */
    private final BitSet allMarkIndicies;

    /**
     * The sequence of {@code BindMark}s as they appear in the Template 
     * to be populated when tailoring 
     * i.e.
     * <PRE>
     * I _____________________, do solemnly swear to tell the truth. 
     *        (fullName)
     * </PRE>    
     * 
     * ...where "fullName" is the {@code BindMark}s' name".<BR> 
     * 
     * NOTE: MAY CONTAIN DUPLICATE NAMES, for instance, given the sequence:
     * <PRE>
     * _________, is the number of the counting, again the number is _______.
     *   count                                                        count
     * </PRE>
     * the name "count" appears (2) times at [0] and at [1].
     */
    private final Mark.BlankFiller[] fillActions;

    /** text and "blanks" where bindMarks occur and context params are bound */
    private final FillInTheBlanks.FillTemplate fillBlanks;

    /**
     * Creates a {@code VarCode} associating the {@code fillBlanksDoc} with the 
     * {@code orderedBindings} that are {@code MarkAction.Named}.
     * 
     * NOTE: Only {@code MarkAction}s that are {@code Embed} are associated with 
     * the blanks in the document
     * <UL>
     *  <LI>{@code Add}
     *  <LI>{@code AddScriptResult}
     *  <LI>{@code ReplaceWithScriptResult}
     *  <LI>{@code IfAdd}
     *  <LI>{@code IfAddWithForm}
     *  <LI>{@code Replace}
     *  <LI>{@code ReplaceWithForm}
     * </UL>   
     * 
     * (Other {@code MarkActions}s like {@code Cut}, {@code CutComment} , 
     * {@code CutJavaDoc} contain information that is not included
     * in the tailored source. 
     * 
     * @param fillTemplate {@code FillInTheBlanks.FillOrder} static text and 
     * blanks in the document
     * @param allMarksSequence the marks that occur within the document (in order)
     */
    public FormDom( 
        FillInTheBlanks.FillTemplate fillTemplate, 
        Mark[] allMarksSequence,
        BitSet allMarkLocations )
    {
        this.fillBlanks = fillTemplate;
        this.actions = allMarksSequence;
        this.allMarkIndicies = allMarkLocations;
        this.allMarkFormBlanks =
            FillTemplate.of( fillBlanks.getStaticText(), allMarkIndicies );

        List<BlankFiller> embeddedMarkSequence = new ArrayList<BlankFiller>();
        for( int i = 0; i < allMarksSequence.length; i++ )
        {
            if( ( allMarksSequence[ i ] instanceof BlankFiller ) )
            {
                embeddedMarkSequence.add( (BlankFiller)allMarksSequence[ i ] );
            }
        }
        this.fillActions = embeddedMarkSequence
            .toArray( new Mark.BlankFiller[ embeddedMarkSequence.size() ] );
    }

    @Override
    public Mark[] getAllMarks()
    {
        return actions;
    }

    @Override
    public BlankFiller[] getBlankFillers()
    {
        return fillActions;
    }

    /* (non-Javadoc)
     * @see io.varcode.VarCodeMark#getBlanksCount()
     */
    @Override
    public int getBlanksCount()
    {
        return fillBlanks.getBlanksCount();
    }

    public Form[] getForms()
    {
        List<Form> theForms = new ArrayList<Form>();
        for( int i = 0; i < this.actions.length; i++ )
        {
            if( actions[ i ] instanceof Mark.HasForm )
            {
                HasForm rf = (HasForm)actions[ i ];

                theForms.add( rf.getForm() );
            }
        }
        return theForms.toArray( new Form[ 0 ] );
    }

    /* (non-Javadoc)
     * @see io.varcode.VarCodeMark#getAllMarkIndexes()
     */
    @Override
    public BitSet getAllMarkIndicies()
    {
        return this.allMarkIndicies;
    }

    /* (non-Javadoc)
     * @see io.varcode.VarCodeMark#getFillBlanks()
     */
    @Override
    public FillTemplate getFillTemplate()
    {
        return fillBlanks;
    }

    /**
     * Gets the "Original" Text (including the {@code Mark}s).
     * @return A String representing the 
     */
    public String getOriginalText()
    {
        String[] markFills = new String[ this.allMarkIndicies.cardinality() ];
        for( int i = 0; i < markFills.length; i++ )
        {
            markFills[ i ] = actions[ i ].getText();
        }
        return this.allMarkFormBlanks.fill( (Object[])markFills );
    }

    public String toString()
    {
        return getOriginalText() + System.lineSeparator() 
            + "/**{- "+ System.lineSeparator()
            + "  FORM MARKUP SUMMARY " + System.lineSeparator()
            + "    MARKS  : (" + actions.length + ")" + System.lineSeparator() 
            + "    BLANKS : (" + fillActions.length + ")" + System.lineSeparator()
            + "-}*/";
    }

    
    @Override
    public Set<String> getAllVarNames( VarContext context )
    {
        Set<String> varNames = new HashSet<String>();

        for( int i = 0; i < actions.length; i++ )
        {
            if( actions[ i ] instanceof Mark.HasVars )
            {
            	Mark.HasVars hv = (Mark.HasVars)actions[ i ];                
                varNames.addAll( hv.getAllVarNames( context ) );                                
            }
        }
        return varNames;
    }
}