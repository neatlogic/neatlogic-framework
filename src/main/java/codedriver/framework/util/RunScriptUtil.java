package codedriver.framework.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class RunScriptUtil {
	public static boolean runScript(String expression) throws ScriptException, NoSuchMethodException {

		StringBuilder script = new StringBuilder();
		script.append("function run(){");
		script.append("return " + expression + ";\n");
		script.append("}");

		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("nashorn");
		se.eval(script.toString());
		Invocable invocableEngine = (Invocable) se;
		Object callbackvalue = invocableEngine.invokeFunction("run");
		return Boolean.parseBoolean(callbackvalue.toString());
	}
	
	public static void main(String[] args) throws NoSuchMethodException, ScriptException {
		System.out.println(runScript("(true && true)"));
	}
}
