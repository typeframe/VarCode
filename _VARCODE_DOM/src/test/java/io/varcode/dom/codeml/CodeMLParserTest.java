package io.varcode.dom.codeml;

import io.varcode.dom.mark.AddExpressionResult;
import io.varcode.dom.mark.AddForm;
import io.varcode.dom.mark.AddFormIfVar;
import io.varcode.dom.mark.AddIfVar;
import io.varcode.dom.mark.AddScriptResult;
import io.varcode.dom.mark.AddVarExpression;
import io.varcode.dom.mark.Cut;
import io.varcode.dom.mark.CutComment;
import io.varcode.dom.mark.CutIfExpression;
import io.varcode.dom.mark.CutJavaDoc;
import io.varcode.dom.mark.DefineVar;
import io.varcode.dom.mark.DefineVarAsExpressionResult;
import io.varcode.dom.mark.DefineVarAsForm;
import io.varcode.dom.mark.DefineVarAsScriptResult;
import io.varcode.dom.mark.EvalExpression;
import io.varcode.dom.mark.EvalScript;
import io.varcode.dom.mark.ReplaceWithExpressionResult;
import io.varcode.dom.mark.ReplaceWithVar;
import io.varcode.dom.mark.SetMetadata;
import io.varcode.dom.mark.TailorDirective;
import junit.framework.TestCase;

public class CodeMLParserTest
    extends TestCase   
{
    public void testParseMarkVariants()
    {   
    	assertTrue( CodeML.parseMark( "{+$scriptName()+}" ) instanceof AddScriptResult );
        assertTrue( CodeML.parseMark( "{+$scriptName()*+}" ) instanceof AddScriptResult ); //REQUIRED

        assertTrue( CodeML.parseMark( "/*{$$directiveName()$$}*/" ) instanceof TailorDirective );   
        assertTrue( CodeML.parseMark( "/*{$$directiveName()*$$}*/" ) instanceof TailorDirective ); //REQUIRED
        
        assertTrue( CodeML.parseMark( "/*{(( 4 + 10 ))}*/" ) instanceof EvalExpression );
        assertTrue( CodeML.parseMark( "/*{$scriptName()$}*/" ) instanceof EvalScript );        
        assertTrue( CodeML.parseMark( "/*{$scriptName()*$}*/" ) instanceof EvalScript ); //REQUIRED
        
        assertTrue( CodeML.parseMark( "{+varName+}" ) instanceof AddVarExpression );
        assertTrue( CodeML.parseMark( "{+varName|default+}" ) instanceof AddVarExpression );
        assertTrue( CodeML.parseMark( "{+varName*+}" ) instanceof AddVarExpression );
        
        assertTrue( CodeML.parseMark( "/*{+((3+8))*/REPLACEME/*+}*/") instanceof ReplaceWithExpressionResult );
    	
                
        assertTrue( CodeML.parseMark( "/*{+varName+}*/" ) instanceof AddVarExpression );
        assertTrue( CodeML.parseMark( "/*{+varName|default+}*/" ) instanceof AddVarExpression );
        assertTrue( CodeML.parseMark( "/*{+varName*+}*/" ) instanceof AddVarExpression );
        assertTrue( CodeML.parseMark( "/*{+varName:(( varName.length() > 2 ))+}*/" ) instanceof AddVarExpression );
        assertTrue( CodeML.parseMark( "/*{+varName:(( varName.length() > 2 ))*+}*/" ) instanceof AddVarExpression );
        assertTrue( CodeML.parseMark( "/*{+varName:(( varName.length() > 2 ))|defaultName+}*/" ) instanceof AddVarExpression );
        
        
        assertTrue( CodeML.parseMark( "/*{+?varName:conditionalText+}*/" ) instanceof AddIfVar );
        assertTrue( CodeML.parseMark( "/*{+?varName=1:conditionalText+}*/" ) instanceof AddIfVar );
        assertTrue( CodeML.parseMark( "/*{+?varName==1:conditionalText+}*/" ) instanceof AddIfVar );
        
        assertTrue( CodeML.parseMark( "{+(( Math.sqrt( a * a + b * b ) ))+}" ) instanceof AddExpressionResult );
        assertTrue( CodeML.parseMark( "/*{+(( Math.sqrt( a * a + b * b ) ))+}*/" ) instanceof AddExpressionResult );
    	
    	
        assertTrue( CodeML.parseMark( "/*{+varName*/replace/*+}*/" ) instanceof ReplaceWithVar );
        assertTrue( CodeML.parseMark( "/*{+varName|*/replace default /*+}*/" ) instanceof ReplaceWithVar );
        assertTrue( CodeML.parseMark( "/*{+varName**/   replace   /*+}*/" ) instanceof ReplaceWithVar );
        assertTrue( CodeML.parseMark( "/*{+varName*/   replace   /*+}*/" ) instanceof ReplaceWithVar );
        assertTrue( CodeML.parseMark( "/*{+varName*/    \"replace\"   /*+}*/" ) instanceof ReplaceWithVar );
        
        assertTrue( CodeML.parseMark( "/*{#varName:value#}*/" ) instanceof DefineVar.InstanceVar );
        assertTrue( CodeML.parseMark( "/*{#varName=value#}*/" ) instanceof DefineVar.InstanceVar );
        
        assertTrue( CodeML.parseMark( "/*{#varName:$scriptName()#}*/" ) instanceof DefineVarAsScriptResult.InstanceVar );
        assertTrue( CodeML.parseMark( "/*{#varName=$scriptName()#}*/" ) instanceof DefineVarAsScriptResult.InstanceVar );
        
        assertTrue( CodeML.parseMark( "/*{#varName:(( a + b ))#}*/" ) instanceof DefineVarAsExpressionResult.InstanceVar );
        assertTrue( CodeML.parseMark( "/*{#varName=(( a + b ))#}*/" ) instanceof DefineVarAsExpressionResult.InstanceVar );
        
        assertTrue( CodeML.parseMark( "/*{##varName:((a + b))##}*/" ) instanceof DefineVarAsExpressionResult.StaticVar );
        assertTrue( CodeML.parseMark( "/*{##varName=((a + b))##}*/" ) instanceof DefineVarAsExpressionResult.StaticVar );
        
        //System.out.println( Cml.parseMark( "/*{##name:$uuid()##}*/" ).getClass() );
        
        assertTrue( CodeML.parseMark( "/*{##varName:$scriptName()##}*/" ) instanceof DefineVarAsScriptResult.StaticVar );
        
        assertTrue( CodeML.parseMark( "/*{{+:({+areacode+})-{+first3+}-{+last4+}+}}*/" ) instanceof AddForm );
        assertTrue( CodeML.parseMark( "/*{_+:({+areacode+})-{+first3+}-{+last4+}+_}*/" ) instanceof AddForm );
        
        assertTrue( CodeML.parseMark( "/*{{+?showPhone:({+areacode+})-{+first3+}-{+last4+}#}}*/" ) instanceof AddFormIfVar );
        assertTrue( CodeML.parseMark( "/*{_+?showPhone:({+areacode+})-{+first3+}-{+last4+}#_}*/" ) instanceof AddFormIfVar );
        
        assertTrue( CodeML.parseMark( "/*{{#phone:({+areacode+})-{+first3+}-{+last4+}#}}*/" ) instanceof DefineVarAsForm.InstanceVar );        
        assertTrue( CodeML.parseMark( "/*{{##phone:({+areacode*+})-{+first3*+}-{+last4*+}##}}*/" ) instanceof DefineVarAsForm.StaticVar );
        
        assertTrue( CodeML.parseMark( "/*{_#phone:({+areacode+})-{+first3+}-{+last4+}#_}*/" ) instanceof DefineVarAsForm.InstanceVar );        
        assertTrue( CodeML.parseMark( "/*{_##phone:({+areacode*+})-{+first3*+}-{+last4*+}##_}*/" ) instanceof DefineVarAsForm.StaticVar );
        
        assertTrue( CodeML.parseMark( "/*{##varName:value##}*/" ) instanceof DefineVar.StaticVar );
        //MarkAction ma = Tags.of( "/**{#name:$uuid()}*/" );
        // System.out.println (ma.getClass());
        assertTrue( CodeML.parseMark( "/*{##name:$uuid()##}*/" ) instanceof DefineVarAsScriptResult );
        
        
        assertTrue( CodeML.parseMark( "/*{-*/ cut this /*-}*/" ) instanceof Cut );
        assertTrue( CodeML.parseMark( "/*{-?((fred==dead)):*/cut this comment/*-}*/" ) instanceof CutIfExpression );
        assertTrue( CodeML.parseMark( "/*{-  cut this comment -}*/" ) instanceof CutComment );
        
        assertTrue( CodeML.parseMark( "/**{-  cut this Javadoc comment -}*/" ) instanceof CutJavaDoc );
        
        assertTrue( CodeML.parseMark( "/**{@metadata:value@}*/" ) instanceof SetMetadata );
        assertTrue( CodeML.parseMark( "/*{@metadata:value@}*/" ) instanceof SetMetadata );
    }
    
    public void testFindFirstTag()
    {
    	assertEquals( "/*{((", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{(( 3 + 4 ))}*/" ) );
    	
    	assertEquals( "{+", CodeMLParser.INSTANCE.getFirstOpenTag( "{+inline+}" ) );
    	assertEquals( "{+", CodeMLParser.INSTANCE.getFirstOpenTag( "{+$script()+}" ) );
    	
    	assertEquals( "/*{#", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{#$tailorDirective()#}*/" ) );
    	
    	assertEquals( "/*{#", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{#varName:((a + b))#}*/" ) );
    	assertEquals( "/*{#", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{#varName=((a + b))#}*/" ) );
    	
    	assertEquals( "/*{##", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{##varName:((a + b))##}*/" ) );
    	assertEquals( "/*{##", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{##varName=((a + b))##}*/" ) );
        
    	assertEquals( "/*{{+", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{{+:({+areacode})-{+first3}-{last4}+}}*/" ) );
    	assertEquals( "/*{_+", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{_+:({+areacode})-{+first3}-{last4}+_}*/" ) );
    	
    	assertEquals( "/*{{+", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{{+?showPhone:({+areacode})-{+first3}-{last4}#}}*/" ) );
    	assertEquals( "/*{_+", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{_+?showPhone:({+areacode})-{+first3}-{last4}#}}*/" ) );
    	
    	
        assertTrue( CodeML.parseMark( "/*{_+?showPhone:({+areacode+})-{+first3+}-{+last4+}#_}*/" ) instanceof AddFormIfVar );
         
        assertEquals( "/*{$", CodeMLParser.INSTANCE.getFirstOpenTag( "/*{$name()$}*/" ) );
        assertEquals( "{+", CodeMLParser.INSTANCE.getFirstOpenTag("{+name+}" ) );
        assertEquals( "/*{+", CodeMLParser.INSTANCE.getFirstOpenTag("/*{+name+}*/" ) );
        
        assertEquals( "/*{{+", CodeMLParser.INSTANCE.getFirstOpenTag("/*{{+name+}*/" ) );
        assertEquals( "/*{{+", CodeMLParser.INSTANCE.getFirstOpenTag("/*{{+?name+}*/" ) );
        
        assertEquals( "/*{#", CodeMLParser.INSTANCE.getFirstOpenTag("/*{#name:0#}*/" ) );
        assertEquals( "/*{{#", CodeMLParser.INSTANCE.getFirstOpenTag("/*{{#name=0#}}*/" ) );
        assertEquals( "/*{{##", CodeMLParser.INSTANCE.getFirstOpenTag("/*{{##name=1##}}*/" ) );
        assertEquals( "/*{##", CodeMLParser.INSTANCE.getFirstOpenTag("/*{##name:4##}*/" ) );
        
        assertEquals( "/*{-",CodeMLParser.INSTANCE.getFirstOpenTag("/*{- name - }*/" ) );
        
        assertEquals( "/*{-?(",CodeMLParser.INSTANCE.getFirstOpenTag("/*{-?(fred=dead):*/name/*-}*/" ) );
        
        assertEquals( "/**{-",CodeMLParser.INSTANCE.getFirstOpenTag("/**{- name}*/" ) );
        
        assertEquals( "/*{@",CodeMLParser.INSTANCE.getFirstOpenTag("/*{@name:6-@}*/" ) );
        
        assertEquals( "/**{@",CodeMLParser.INSTANCE.getFirstOpenTag("/**{@name:6-@}*/" ) );
        
        assertEquals( null ,CodeMLParser.INSTANCE.getFirstOpenTag("/**{name}*/" ) );
        assertEquals( null ,CodeMLParser.INSTANCE.getFirstOpenTag("" ) );
        assertEquals( null ,CodeMLParser.INSTANCE.getFirstOpenTag("}*/" ) );
        assertEquals( null ,CodeMLParser.INSTANCE.getFirstOpenTag("}*/" ) );
    }
    
    public void testFindCloseTags()
    {
    	assertEquals( "))}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{(( 3 + 4 ))}*/" ) ) );
    	
    	assertEquals( "+}", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "{+name+}" ) ) );
    	assertEquals( "+}", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "{+$script()+}" ) ) );
    	
    	
    	assertEquals( "#}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{#$tailorDirective()}*/" ) ) );    	
    	
        assertEquals( "$}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag("/*{$name()$}*/" ) ) );
        
        assertEquals( "+}}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{{+?showPhone:({+areacode})-{+first3}-{last4}#}}*/" ) ) );
    	assertEquals( "+_}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{_+?showPhone:({+areacode})-{+first3}-{last4}#}}*/" ) ) );
    	
        
        assertEquals( "+}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{+name+}*/" ) ));
        assertEquals( "+}}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{{+:name+}}*/" ) ));
        assertEquals( "+}}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{{+?name:{+salutation} {+firstName+} {+mi+} {+lastName+}+}}*/" )) );
        
        assertEquals( "#}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{#name#}*/" ) ));
        assertEquals( "#}}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{{#name#}}*/" ) ));
        assertEquals( "##}}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{{##name##}}*/" ) ));
        assertEquals( "##}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{##name##}*/" ) ));
        
        assertEquals( "/*-}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{-?(" ) );
        assertEquals( "-}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{- name -}*/" ) ));
        assertEquals( "-}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/**{- name -}*/" ) ));
        
        assertEquals( "@}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/**{@name:value@}*/" ) ));
        //System.out.println( Cml.PARSER.getFirstOpenTag("/*{@name:value@}*/" ) );
        assertEquals( "@}*/", CodeMLParser.INSTANCE.closeTagFor( CodeMLParser.INSTANCE.getFirstOpenTag( "/*{@name:value@}*/" ) ));
    }    
}
