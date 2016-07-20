package io.varcode.dom.mark;

import java.util.HashSet;
import java.util.Set;

import io.varcode.VarException;
import io.varcode.context.VarContext;
import io.varcode.context.VarScope;
import io.varcode.dom.DomParseState;
import io.varcode.dom.mark.Mark.HasVars;
import io.varcode.dom.mark.Mark.IsNamed;


//these are SCOPE instance variables defined at "Tailor time" MUTABLE
/*{#dayFormat:dd}*/
/*{#monthFormat:MM}*/
/*{#yearFormat:yyyy}*/
/*{{#dateFormat:{+yearFormat}-{+monthFormat}-{dayFormat} }}*/
/*{#versionDate:$date({+dateFormat})}*/

/*{#dateFormat:yyyy-MM-dd}*/ //<-- Define  a DateFormat Action
/*{#today:$date({+dateFormat})}*/ //<-- define today as DefineVarAsScriptResult
                                  // the result of calling $date(...)
                                  // using the defined dateFormat

/**
 * Defines the value of a Var as a literal 
 * (either a {@code StaticVar} or {@code InstanceVar} )  
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class DefineVar
    extends Mark
    implements IsNamed, HasVars
{   
    public final String varName;
    
    public final String value;
    
    public final Set<String> vars = new HashSet<String>();
    
    public DefineVar( 
        String text, 
        int lineNumber, 
        String varName, 
        String value )
    {
        super( text, lineNumber );
        this.varName = varName;
        this.value = value;
        this.vars.add( varName );
    }
    
    public String getValue()
    {
        return value;
    }

    public String getVarName()
    {
        return varName;
    }

    @Override
    public Set<String> getAllVarNames( VarContext context )
    {
        return vars;
    }

    public static final class InstanceVar
        extends DefineVar
        implements BoundDynamically
    {
        public InstanceVar( 
            String text, 
            int lineNumber, 
            String name, 
            String value )
        {
            super( text, lineNumber, name, value );
        }
        
        @Override
        public Object derive( VarContext tailorContext )
        {
            return tailorContext.get( varName );
        }
        
        public void bind( VarContext tailorContext )
            throws VarException
        {
            tailorContext.set( varName, value, VarScope.INSTANCE );
        }
    }
    
    public static final class StaticVar
        extends DefineVar
        implements BoundStatically
    {
        public StaticVar( 
            String text, 
            int lineNumber, 
            String name, 
            String value )
        {
            super( text, lineNumber, name, value );            
        }
    
        @Override
        public void onMarkParsed( DomParseState parseState )
        {
        	parseState.setStaticVar( varName, value ); 
        }
        
        public String toString()
        {
            return "STATIC " + varName + " = " + value;
        }
    }   
}
