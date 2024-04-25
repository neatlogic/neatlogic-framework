/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util.javascript;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import neatlogic.framework.exception.core.ApiRuntimeException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.script.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class JavascriptUtil {
    private static final ThreadLocal<List<ApiRuntimeException>> instance = new ThreadLocal<>();


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

    // private static final Logger logger = LoggerFactory.getLogger(JavascriptUtil.class);
    private static final ThreadLocal<List<CacheItem>> engineCache = new ThreadLocal<>();

    //private static final ScriptEngineManager sem = new ScriptEngineManager();
    private static final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

    public static ScriptEngine getEngine(ScriptClassFilter classFilter, String... options) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if (ccl == null) {
            ccl = NashornScriptEngineFactory.class.getClassLoader();
        }
        return factory.getScriptEngine(options, ccl, classFilter);
    }

    public static ScriptEngine getEngine(String... options) {
        return factory.getScriptEngine(options);
    }

    public static ScriptEngine getEngine() {
        //默认禁用所有java引用
        return factory.getScriptEngine("--no-java");
    }

    public static ScriptEngine getEngine(ScriptClassFilter classFilter) {
        return factory.getScriptEngine(classFilter);
    }

    private static CompiledScript getCompiledScript(String script, boolean needCache) throws ScriptException {
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
            if (engineList.size() > 10) {
                engineList.remove(10);
            }
        } else {
            item = new CacheItem(script);
        }
        //System.out.println("size:" + engineList.size());
        return item.getCompiledScript();
    }

    public static String transform(Object paramObj, String script) throws ScriptException, NoSuchMethodException {
        return transform(paramObj, script, null);
    }

    public static Object runScript(JSONObject paramObj, String script) throws ScriptException {
        CompiledScript compiledScript;
        compiledScript = getCompiledScript("function run(){" + script + ";}run();", true);
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
    public static boolean runExpression(JSONObject paramObj, String expression, List<ApiRuntimeException> errorList) throws ScriptException, NoSuchMethodException {
        //处理is-null和is-not-null两种表达式
        expression = expression.replace("-", "");
        //ScriptEngine se = getEngine("-strict");
        ScriptEngine se = getEngine(new ScriptClassFilter("neatlogic.framework.util.javascript.expressionHandler."));
        //ScriptEngine se = sem.getEngineByName("nashorn");
        if (MapUtils.isNotEmpty(paramObj)) {
            for (String key : paramObj.keySet()) {
                se.put(key, paramObj.get(key));
            }
        }
        String script = "function run(){\n" + "return " + expression + ";\n" + "}\n";

        script += "function calculate(expression, dataValue, conditionValue, label){\n";
        script += "var calculateClass = Java.type('neatlogic.framework.util.javascript.expressionHandler.'+ expression); \n";
        script += "var result = calculateClass.calculate(dataValue, conditionValue, label);\n";
        script += "return result;\n";
        script += "}\n";
        se.eval(script);
        //由于表达式不能直接抛异常，创建threadlocal传递errorList
        if (errorList != null) {
            instance.set(errorList);
        }
        try {
            //System.out.println(script);
            Invocable invocableEngine = (Invocable) se;
            Object rv = invocableEngine.invokeFunction("run");
            if (rv != null) {
                return Boolean.parseBoolean(rv.toString());
            }
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
            ScriptException, NoSuchMethodException {
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

    public static List<ApiRuntimeException> getErrorList() {
        return instance.get();
    }

    public static String transform(Object paramObj, String script, StringWriter sw) throws
            ScriptException, NoSuchMethodException {
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
        se.put("N", null);
        se.eval("try{O = JSON.parse(O);}catch(e){}");
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
