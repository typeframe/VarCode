/*<file>*/
/*<package>*/
package io.varcode.context.lib.text;
/*</package>*/

/*<imports>*/
import io.varcode.context.VarContext;
import io.varcode.context.VarScript;

/*<javadoc>*/
/**
 * Adds an "Annotation Hook" into the source code at the Mark position
 * (when used within a AddScriptResult Mark):
 * 
 * A Hook doesn't specifically "DO" anything when inserted into code, but 
 * it can allow searches for key aspects of programs to be used (searched for)
 * 
 * ...for instance, if some critical part of a program is being called, we might not 
 * ONLY want to log that this event is happening but also leave a HOOK so (later)
 * I can search for this code easily. (i.e. search for "JIRA-1213"
 * 
 * Hooks can Group like things together and or 
 * mark a key juncture of code (i.e. when a mass-refactoring is made, a Hook can signify a
 * point at which "new" code is being called... (i.e. a JIRA number, etc.)
 * so if changes for a bug occur in multiple places within the code, we can 
 * query for those changes looking for a hook with the JIRA number)
 * 
 * This can answer the historical question of "Why does this code look the way it does?"
 * (when a developer is looking at code, they might ask this and want to go from the code
 * back to the JIRA/requirements)
 * 
 * This is a good way of placing Markers in Code that may
 * Allow the Hook Comment Annotation to be replaced with Code (i.e. Logging, State debugging, etc.) later
 * Allow the Hook to help track down a Series
 */
/*</javadoc>*/




/*{@hook=JIRA-113 AddUser fails when user first name is blank@}*/
// Internal Text
/*<class name="HookAt">*/
/*<classDef>*/
public enum HookAt
	implements VarScript/*</classDef>*//*<body>*/	
{
	/*<staticInit>*/INSTANCE;/*</staticInit>*/
	
    /*<method name="getInputParser" return="io.varcode.context.script.ScriptInputParser" access="public">*/
	/*<annotations>*/@Override/*<annotations>*/
	/*<signature>*/
	public ScriptInputParser getInputParser()/*<signature>*/
	/*<body>*/
	{
		return VarScript.STRING_INPUT;
	}
	/*</body>*/
	/*</method>*/

	@Override
	public Object eval( VarContext context, String input ) 
	{
		return "/*{@hook=" + input + "}*/";
	}
	
	/*<method name="toString" access="public" returnType="String">*/
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}
}/*</body>*/
/*</class>*/
/*</file>*/
