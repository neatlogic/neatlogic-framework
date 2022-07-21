/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util.javascript;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.StringWriter;

public class JavascriptUtil {
    static Logger logger = LoggerFactory.getLogger(JavascriptUtil.class);
    //private static final ScriptEngineManager sem = new ScriptEngineManager();
    private static final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

    public static ScriptEngine getEngine() {
        return factory.getScriptEngine(new ScriptClassFilter());
    }

    public static ScriptEngine getEngine(ScriptClassFilter classFilter) {
        return factory.getScriptEngine(classFilter);
    }

    public static String transform(Object paramObj, String script) throws ScriptException, NoSuchMethodException {
        return transform(paramObj, script, null);
    }

    /**
     * 执行一个表达式，返回true或false
     *
     * @param paramObj   参数，使用第一层key作为参数名
     * @param expression 表达式
     * @return 执行结果
     */
    public static boolean runExpression(JSONObject paramObj, String expression) throws ScriptException, NoSuchMethodException {
        //处理is-null和is-not-null两种表达式
        expression = expression.replace("-", "");
        //ScriptEngine se = getEngine("-strict");
        ScriptEngine se = getEngine(new ScriptClassFilter("codedriver.framework.util.javascript.expressionHandler."));
        //ScriptEngine se = sem.getEngineByName("nashorn");
        if (MapUtils.isNotEmpty(paramObj)) {
            for (String key : paramObj.keySet()) {
                se.put(key, paramObj.get(key));
            }
        }
        String script = "function run(){\n" + "return " + expression + ";\n" + "}\n";

        script += "function calculate(expression, dataValue, conditionValue){\n";
        script += "var calculateClass = Java.type('codedriver.framework.util.javascript.expressionHandler.'+ expression); \n";
        script += "var result = calculateClass.calculate(dataValue, conditionValue);\n";
        script += "return result;\n";
        script += "}\n";
        se.eval(script);
        //System.out.println(script);
        Invocable invocableEngine = (Invocable) se;
        Object rv = invocableEngine.invokeFunction("run");
        if (rv != null) {
            return Boolean.parseBoolean(rv.toString());
        }
        return false;
    }

    public static void main2(String[] v) throws ScriptException, NoSuchMethodException {
        JSONObject paramObj = new JSONObject();
        JSONArray attr1 = new JSONArray();
        attr1.add("1");
        JSONArray attr2 = new JSONArray();
        attr2.add("a");
        attr2.add("b");
        JSONObject valueObj = new JSONObject();
        valueObj.put("attr_1", attr1);
        valueObj.put("attr_2", attr2);
        paramObj.put("data", valueObj);

        JSONArray attr3 = new JSONArray();
        attr3.add("1");
        JSONArray attr4 = new JSONArray();
        attr4.add("b");
        attr4.add("a");
        JSONObject conditionObj = new JSONObject();
        conditionObj.put("attr_1", attr3);
        conditionObj.put("attr_2", attr4);
        paramObj.put("condition", conditionObj);

        String expression = "calculate('equal', data['attr_1'],condition['attr_1']) && calculate('equal', data['attr_2'], condition['attr_2'])";
        System.out.println(runExpression(paramObj, expression));
    }

    /*public static void main(String[] v) throws ScriptException {

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            Runnable r = () -> {
                ScriptEngine se = factory.getScriptEngine("-strict", "-doe", "--no-java");
                se.put("a", finalI);
                se.put("b", 2);
                se.put("c", 2);
                try {
                    System.out.println("result" + finalI + ":" + se.eval("function calFunction(a, b, c){return a+b+c;}calFunction(a,b,c);"));
                } catch (ScriptException e) {
                    throw new RuntimeException(e);
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        System.out.println("======done!");
    }*/


    public static String transform(Object paramObj, String script, StringWriter sw) throws ScriptException, NoSuchMethodException {
        if (StringUtils.isBlank(script)) {
            if (paramObj != null) {
                return JSONObject.toJSONString(paramObj);
            } else {
                return "{}";
            }
        }
        //ScriptEngine se = sem.getEngineByName("nashorn");
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
                resultStr = result.toString();// JSON.toJSONString(result);
            } catch (Exception ex) {
                resultStr = result.toString();
            }
        }
        return resultStr;
    }


}
