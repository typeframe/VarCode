package io.varcode.context;

import javax.script.SimpleBindings;

import io.varcode.dom.Dom;
import io.varcode.java.JavaCase;
import junit.framework.TestCase;

/*{$init(
var fun1 = function( name ) 
{
    print('Hi there from Javascript, ' + name);
    return "greetings from javascript";
};

var fun2 = function( object ) 
{
    print("JS Class Definition: " + Object.prototype.toString.call(object) );
};
)$}*/

/*{((
var fun1 = function( name ) 
{
    print('Hi there from Javascript, ' + name);
    return "greetings from javascript";
};

var fun2 = function( object ) 
{
    print("JS Class Definition: " + Object.prototype.toString.call(object) );
};
))}*/
/** 
 * @author eric
 *
 */
public class ExpressionEvaluator_JavaScriptTest
	extends TestCase
{

	String functionLib = 
	"var fun1 = function(name) { " + System.lineSeparator() +
	"print('Hi there from Javascript, ' + name);" + System.lineSeparator() + 
	"return \"greetings from javascript\";" + System.lineSeparator() +
	"};"+ System.lineSeparator() +
	System.lineSeparator() +
	"var fun2 = function (object) {" + System.lineSeparator() +
	"print(\"JS Class Definition: \" + Object.prototype.toString.call(object));"+ System.lineSeparator() +
	"};";
	
	public void testLoadAFunction()
	{
		SimpleBindings sb = new SimpleBindings();
		sb.put( "myFunction", "function myFunction(p1, p2){ return p1* p2;}" );
		
		//Object res = 
	    //		ExpressionEvaluator_JavaScript.INSTANCE.evaluate( sb, "myFunction(2, 4);" );
		ExpressionEvaluator_JavaScript.INSTANCE.evaluate( sb, functionLib );
		
		System.out.println( 
			ExpressionEvaluator_JavaScript.INSTANCE.evaluate( sb, "fun1('Eric')" ) );
		//System.out.println( "res" );
	}
	
	public static void main( String[] args )
	{
		Dom dom = JavaCase.dom( ExpressionEvaluator_JavaScriptTest.class ); 
		System.out.println( dom );
	}
}
