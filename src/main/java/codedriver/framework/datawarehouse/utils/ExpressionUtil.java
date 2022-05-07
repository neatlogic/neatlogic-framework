/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Iterator;
import java.util.Map;

public class ExpressionUtil {
    public static boolean evaluateExpression(String expression, Map<String, Object> paramMap) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("nashorn");
        Iterator<Map.Entry<String, Object>> iter = paramMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            engine.put(entry.getKey(), entry.getValue());
        }
        try {
            return Boolean.parseBoolean(engine.eval(expression).toString());
        } catch (ScriptException e) {
            // logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }
}
