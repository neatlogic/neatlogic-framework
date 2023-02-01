/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.util;

import neatlogic.framework.util.javascript.JavascriptUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class RunScriptUtil {
    public static boolean runScript(String expression) throws ScriptException, NoSuchMethodException {
        String script = "function run(){" +
                "return " + expression + ";\n" +
                "}";

        ScriptEngine se = JavascriptUtil.getEngine();
        se.eval(script);
        Invocable invocableEngine = (Invocable) se;
        Object callbackvalue = invocableEngine.invokeFunction("run");
        return Boolean.parseBoolean(callbackvalue.toString());
    }

    public static void main(String[] args) throws NoSuchMethodException, ScriptException {
        System.out.println(runScript("(true && true)"));
    }
}
