package io.varcode.dom.form;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.VarBindings;
import io.varcode.context.VarContext;
import io.varcode.context.VarRequiredButNull;
import io.varcode.context.VarScope;
import io.varcode.dom.mark.Mark;
import io.varcode.dom.mark.Mark.BlankFiller;
import io.varcode.dom.mark.Mark.IsNamed;
import io.varcode.dom.mark.Mark.MayBeRequired;
import io.varcode.text.SmartBuffer;
import io.varcode.text.TextBuffer;

/**
 * {@code Form} of text and variable "blanks" that can be populated.
 * {@code VarForm} also manages handling a {@code SeriesOfForms} when
 * the data provided (to fill in the forms) has cardinality > 1.
 * 
 * for example:<PRE>String form = "{+type*} {+fieldName*}, "</PRE>
 * 
 * <PRE>VarForm declareField = FormMarkupParser.INSTANCE.fromString( form );
 * if we pass in the (2) required* vars to derive the {@code VarForm}:
 * <PRE>VarContext vc = VarContext.of( 
 *   "type", "int", 
 *   "fieldName", "age" );</PRE>
 * 
 * when we derive() the form with the VarContext; we get:
 * <PRE>"int age"
 * 
 * NOTE: since we only have (1) instance of the VarForm, the trailing ", " 
 * from the Form Definition:
 * "{+type*} {+fieldName*}, "
 * 
 * ...is not printed (there are "rules" as to characters that are (by default)
 * Between Tokens {@see BetweenTokens})
 * 
 * ...if we pass in more than one instance of each field, we create a 
 * {@code SeriesOfForms}.
 * 
 * VarContext vc = VarContext.of( 
 *      "type", new String[] { "int", "String"}, 
 *      "fieldName", "new String[]{ "age", "name"} );
 *      
 * ...the output is:
 * "int age, String name"
 * 
 * Notice that here, we used the {@code BetweenToken} ", " only between the (2) instances
 * </PRE>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarForm
    implements Form  
{
    public final int lineNumber;
    
    public final String varName;
    
    public final FormDom formDom;
    
    public final SeriesOfForms seriesFormatter;
    
    public final Set<String>requiredVars;
    
    public VarForm( 
        int lineNumber, 
        String varName, 
        FormDom formDom, 
        SeriesOfForms seriesFormatter )
    {
        /*{+?debug:System.out.println( 
            " The FORM VarCode is \""+ form.getOriginalText()+"\"");}*/        
        this.lineNumber = lineNumber;
        this.varName = varName;
        this.formDom = formDom;
        this.seriesFormatter = seriesFormatter; 
        
        requiredVars = new HashSet<String>();
        Mark[] marks = formDom.getAllMarks();
        for( int i = 0; i < marks.length; i++ )
        {
            if( marks[ i ] instanceof MayBeRequired && marks[ i ] instanceof IsNamed )
            {
                if( ( (MayBeRequired)marks[ i ] ).isRequired() )
                {
                    requiredVars.add( ( (IsNamed)marks[ i ] ).getVarName() );
                }
            }
        }
    }

    public int getLineNumber()
    {
        return this.lineNumber;
    }

    public String toString()
    {
        return varName + ":" + N + this.formDom.toString();
    }

    /** Gets the form in textual form */
    public FormDom getFormMarkup()
    {
        return formDom;
    }

    public String getName()
    {
        return varName;
    }
    
    public Set<String> getAllVarNames( VarContext context )
    {
        Set<String>allVars = new HashSet<String>();
        allVars.addAll( formDom.getAllVarNames( context ) );
        if( varName != null )
        {
            allVars.add( varName );
        }
        return allVars;
    }

    /** NOTE: this will fill ONE, if there is a list, we need another method */     
    private String fillOne( VarContext context )
    {
        try
        {
            Mark[] ma = this.formDom.getAllMarks();
            
            Object[] fills = new Object[ ma.length ];
            for( int i = 0; i < ma.length; i++ )
            {
                BlankFiller f = (BlankFiller)ma[ i ];
                fills[ i ] = f.derive( context );
            }            
            return formDom.getFillTemplate().fill( fills );
        }
        catch( Exception e )
        {
            throw new VarException( 
                "Unable to Tailor VarForm \"" + varName + "\" on line [" 
                + lineNumber + "]", e );
        }
    }
    
    private static final String N = System.lineSeparator();
    
    /**
     * Given the context, determine the "cardinality" (or the Loop count) 
     * for the number of form instances to create:
     * 
     * For example with Form: "{+type} {+name}, " <BR>
     * 
     * if we pass in the context:<BR>
     * <PRE>
     * VarContext context = 
     *     VarContext.of(
     *           "type", new String[]{"int", "String", "float"},
     *           "name", new String[]{"A", "B", "C"} );
     *</PRE>            
     * ...then the cardinality is (3), since we have to 
     * 
     * @param context
     * @return
     */
    private final int getFormInstanceCount( VarContext context )
    {
        Set<String> varNames = getAllVarNames( context );
        
        if( varNames.size() == 0 ) //some VarForms dont have need of vars
        {
        	return 1;
        }
        int cardinality = 0;
        Iterator<String>it = varNames.iterator();
        
        for( int i = 0; i < varNames.size(); i++ )
        {
            String next = it.next(); 
            //Object value = context.get( next );
            Object value = context.getVarResolver().resolveVar( context, next );
            if( value != null ) 
            {
                if( value.getClass().isArray() )
                {
                    int length = Array.getLength( value );
                    if( length > cardinality )
                    {
                        cardinality = length;
                    }
                }
                else if( value instanceof Collection )
                {
                    Collection<?> c = (Collection<?>)value;
                    int length = c.size();
                    if( length > cardinality )
                    {
                        cardinality = length;
                    }
                }
                else
                {
                    if( cardinality == 0  )
                    {
                        cardinality = 1;
                    }
                }
            }
            else
            {
                //is the value Required and null?
                if( requiredVars.contains( next ) )
                {
                    throw new VarRequiredButNull( next,  getText() , "FORM", lineNumber );   
                }
            }
        }        
        return cardinality;
    }
    

    public void deriveTo( VarContext context, TextBuffer out )
    {   //below, we can turn this code "on" if we tailor this code
        //and set debug=true in the Context
        /*{?debug=true:System.out.println( formVarCode );}*/
        
        //find out the number of Forms
        int cardinality = getFormInstanceCount( context );
       
        /*{?debug=true:*/
        //System.out.println( "cardinality is : " + cardinality );/*}*/
        
        //now generate Each Form
        String[] eachForm = new String[ cardinality ]; 
        for( int i = 0; i < cardinality; i++ )
        {
            eachForm[ i ] = tailorAt( i, context );
            //System.out.println("[" + i + "]=\"" + eachForm[ i ] +"\"");
        }
        //	use the form separator
        out.append( seriesFormatter.format( eachForm ) );
    }    
    
    //should be private, making public for testing
    private String tailorAt( int index, VarContext context )
    {
        //System.out.println( "Tailoring at [" + index + "]" );
        //create a new bindings for populating the VarCode that is going to be 
        //at the LOOP level          
        VarBindings loopBindings = 
            context.getOrCreateBindings( VarScope.LOOP );
        
        Set<String> vNames = formDom.getAllVarNames( context );
        
        String[] varNames = vNames.toArray( new String[ 0 ] );
        
        for( int i = 0; i < varNames.length; i++ )
        {            
            //Object thisOne = context.get( varNames[ i ] );
            Object thisOne = context.getVarResolver().resolveVar( context, varNames[ i ] );
            
            //System.out.println( varNames[ i ] + "=" + thisOne );
            if( thisOne != null )
            {
                if( thisOne.getClass().isArray() )
                {   //do a range check
                    if( Array.getLength( thisOne ) > index )
                    {   //note this will OVERWRITE the last var value 
                        //from the last iteration of the loop
                        loopBindings.put( 
                            varNames[ i ], 
                            Array.get( thisOne, index ) );
                    }   
                    else
                    {   
                    	if( requiredVars.contains( varNames[ i ] ) )
                    	{
                    		throw new VarException(
                    			"Cardinality Mismatch for Required Var \"" 
                    		    + varNames[ i ] + "\" of Form " + N + getText() + N 
                    		    + "failed at [" + index + "]" );
                    	}
                    	//failed range check (it's null, but "" will suffice)
                        loopBindings.put( varNames[ i ], "" );
                    }
                }
                if( thisOne instanceof Collection )
                {
                    Collection<?> l = (Collection<?>)thisOne;
                    
                    if( l.size() > index )
                    {                    	
                        loopBindings.put( 
                        	varNames[ i ], 
                        	l.toArray( new Object[0] )[ index  ] );
                        	//Collectionsl.get( index ) ) ;
                    }
                    else
                    {
                    	if( requiredVars.contains( varNames[ i ] ) )
                    	{
                    		throw new VarException(
                    			"Cardinality Mismatch for Required Var \"" + varNames[ i ] + "\"" );
                    	}
                        loopBindings.put( varNames[ i ], "" );
                    }
                }                
            }
        }
        
        String tailoredAt = this.fillOne( context );    
        
        //ensure there is no bleed through of data 
        //(replace the "loop") bindings
        loopBindings.clear();
        
        return tailoredAt;
    }

    @Override
    public String derive( VarContext context )
    {   
    	SmartBuffer textBuffer = 
           new SmartBuffer();
        deriveTo( context, textBuffer ); 
        return textBuffer.toString();
    }

    @Override
    public String getText()
    {
        return formDom.getOriginalText() + seriesFormatter.getText();
    }

    @Override
    public Mark[] getAllMarks()
    {
        return formDom.getAllMarks();
    }
}
