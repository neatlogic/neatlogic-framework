/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.util.javascript;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavascriptUtil {
    private static final Logger logger = LoggerFactory.getLogger(JavascriptUtil.class);
    private static final ThreadLocal<Map<String, JavascriptResult>> instance = new ThreadLocal<>();
    private static int MAX_CACHE_SIZE = 10;

    static class CacheItem {

        private final String script;
        private final CompiledScript compiledScript;

        public CacheItem(String script) throws ScriptException {
            this.script = script;
            ScriptEngine engine = JavascriptUtil.getEngine("--global-per-engine");
            Compilable compilable = ((Compilable) engine);
            this.compiledScript = compilable.compile(script);
        }

        public CompiledScript getCompiledScript() {
            return this.compiledScript;
        }

        public String getScript() {
            return script;
        }


    }

    private static final ThreadLocal<List<CacheItem>> engineCache = new ThreadLocal<>();

    private static final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

   /* public static ScriptEngine getEngine(ScriptClassFilter classFilter, String... options) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if (ccl == null) {
            ccl = NashornScriptEngineFactory.class.getClassLoader();
        }
        return factory.getScriptEngine(options, ccl, classFilter);
    }*/

    public static ScriptEngine getEngine(String... options) {
        return factory.getScriptEngine(options);
    }

    public static ScriptEngine getEngine() {
        //默认禁用所有java引用
        return factory.getScriptEngine("--no-java");
    }

    /*public static ScriptEngine getEngine(ScriptClassFilter classFilter) {
        return factory.getScriptEngine(classFilter);
    }*/

    public static CompiledScript getCompiledScript(String script, boolean needCache) throws ScriptException {
        CacheItem item = null;
        if (needCache) {
            List<CacheItem> engineList = engineCache.get();
            if (engineList == null) {
                engineList = new ArrayList<>();
                engineCache.set(engineList);
            }

            for (int i = 0; i < engineList.size(); i++) {
                if (engineList.get(i).getScript().equals(script)) {
                    item = engineList.get(i);
                    engineList.remove(i);
                    break;
                }
            }
            if (item == null) {
                item = new CacheItem(script);
            }
            engineList.add(item);
            if (engineList.size() > MAX_CACHE_SIZE) {
                engineList.remove(MAX_CACHE_SIZE);
            }
        } else {
            item = new CacheItem(script);
        }
        //System.out.println("size:" + engineList.size());
        return item.getCompiledScript();
    }

    public static String transform(Object paramObj, String script) throws ScriptException {
        return transform(paramObj, script, null);
    }

    public static Object runScript(JSONObject paramObj, String script) throws ScriptException {
        CompiledScript compiledScript = getCompiledScript("function run(){" + script + ";}run();", true);
        Bindings params = new SimpleBindings();
        if (MapUtils.isNotEmpty(paramObj)) {
            params.putAll(paramObj);
        }
        return compiledScript.eval(params);
    }

    /**
     * 执行一个表达式，返回true或false
     *
     * @param paramObj   参数，包含data,condition和define三个属性
     * @param expression 表达式
     * @return 执行结果
     */
    public static boolean runExpression(JSONObject paramObj, String expression, Map<String, JavascriptResult> resultMap) throws ScriptException {
        expression = expression.replace("-", "");
        Bindings params = new SimpleBindings();
        if (MapUtils.isNotEmpty(paramObj)) {
            params.putAll(paramObj);
        }
        String script = "function calculate(expression, dataValue, conditionValue, label, uuid){\n";
        script += "var calculateClass = Java.type('neatlogic.framework.util.javascript.expressionHandler.'+ expression); \n";
        // 修正 JDK17 Nashorn undefined 参数
        script += "if (typeof dataValue === 'undefined') dataValue = null;\n";
        script += "if (typeof conditionValue === 'undefined') conditionValue = null;\n";
        script += "if (typeof label === 'undefined') label = null;\n";
        script += "var result = calculateClass.calculate(dataValue, conditionValue, label, uuid);\n";
        script += "return result;\n";
        script += "}\n";
        script += expression + ";";

        CompiledScript compiledScript = getCompiledScript(script, true);

        //由于表达式不能直接抛异常，创建threadlocal传递errorList
        if (resultMap != null) {
            instance.set(resultMap);
        }
        try {
            Object rv = compiledScript.eval(params);
            if (rv != null) {
                return Boolean.parseBoolean(rv.toString());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (instance.get() != null) {
                instance.remove();
            }
        }
        return false;
    }

    /**
     * 执行一个表达式，返回true或false
     *
     * @param paramObj   参数，包含data,condition和define三个属性
     * @param expression 表达式
     * @return 执行结果
     */
    public static boolean runExpression(JSONObject paramObj, String expression) throws
            ScriptException {
        return runExpression(paramObj, expression, null);
    }


   /* public static void main(String[] v) throws ScriptException, InterruptedException {
        //ScriptEngine se = getEngine("--global-per-engine");
        //Compilable compilable = ((Compilable) se);
        //CompiledScript script = compilable.compile(
         //       "function run(a,m,n){  var x = param.a + 1; \n" +
           //             "  var y = x * 2 + param.m;\n" +
             //           "  var z = y * 3 - param.n;\n" +
               //         "  z;\n" +
                 //       "return z;} run(param.a,param.m,param.n);\n");
        long s = System.currentTimeMillis();
        AtomicInteger counter = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1000);
        for (int i = 1; i <= 1000; i++) {
            int finalI = i;
            Runnable runner = () -> {
                CompiledScript script = null;
                try {
                    script = getCompiledScript("function run(a,m,n){  var x = param.a + 1;\n" +
                            "var y = x * 2 + param.m;\n" +
                            "var z = y * 3 - param.n;\n" +
                            "return z;} run(param.a,param.m,param.n);\n", false);
                } catch (ScriptException e) {
                    throw new RuntimeException(e);
                }
                Bindings params = new SimpleBindings();
                params.put("param", new JSONObject() {{
                    this.put("a", finalI);
                    this.put("m", finalI + 1);
                    this.put("n", finalI + 2);
                }});
                //params.put("a", finalI);
                //params.put("m", finalI + 1);
                //params.put("n", finalI + 2);
                try {
                    double exp = Double.parseDouble(Integer.toString(expect(finalI, finalI + 1, finalI + 2)));
                    double act = (Double) script.eval(params);
                    if ((exp == act)) {
                        System.out.println("OK" + " " + System.currentTimeMillis());
                    } else {
                        System.out.println("FILED," + exp + " vs " + act + " " + System.currentTimeMillis());
                    }
                    counter.incrementAndGet();
                } catch (ScriptException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            };
            Thread t = new Thread(runner);
            t.start();
        }
        latch.await();
        System.out.println("done,cost:" + (System.currentTimeMillis() - s) + "ms");
    }

    private static int expect(int a, int m, int n) {
        int x = a + 1;
        int y = x * 2 + m;
        return y * 3 - n;
    }*/

    public static Map<String, JavascriptResult> getResultMap() {
        return instance.get();
    }

    public static String transform(Object paramObj, String script, StringWriter sw) throws
            ScriptException {
        if (StringUtils.isBlank(script)) {
            if (paramObj != null) {
                return JSON.toJSONString(paramObj);
            } else {
                return "{}";
            }
        }
        ScriptEngine se = getEngine();
        if (sw != null) {
            se.getContext().setWriter(sw);
        }
        se.put("O", paramObj);
        se.put("N", paramObj);
        se.eval("try{O = JSON.parse(O);N = JSON.parse(N);}catch(e){}");
        se.eval(script);
        Object result = se.eval("JSON.stringify(N);");
        String resultStr = "";
        if (result != null) {
            try {
                resultStr = result.toString();
            } catch (Exception ex) {
                resultStr = result.toString();
            }
        }
        return resultStr;
    }


}
