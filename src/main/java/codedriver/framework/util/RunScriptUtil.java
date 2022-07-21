/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util;

import codedriver.framework.util.javascript.JavascriptUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class RunScriptUtil {
    public static boolean runScript(String expression) throws ScriptException, NoSuchMethodException {
        String script = "function run(){" +
                "return " + expression + ";\n" +
                "}";

        ScriptEngine se = JavascriptUtil.getEngine("-strict", "-doe", "--no-java");
        se.eval(script);
        Invocable invocableEngine = (Invocable) se;
        Object callbackvalue = invocableEngine.invokeFunction("run");
        return Boolean.parseBoolean(callbackvalue.toString());
    }

    public static void main(String[] args) throws NoSuchMethodException, ScriptException {
        System.out.println(runScript("(true && true)"));
    }
}
