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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class XssUtil {
    public static void escapeXss(JSONObject paramObj, String key) {
        Object value = paramObj.get(key);
        if (value instanceof String) {
            try {
                JSONObject valObj = JSONObject.parseObject(value.toString());
                escapeXss(valObj);
                paramObj.replace(key, valObj.toJSONString());
            } catch (Exception ex) {
                try {
                    JSONArray valList = JSONArray.parseArray(value.toString());
                    encodeHtml(valList);
                    paramObj.replace(key, valList.toJSONString());
                } catch (Exception e) {
                    paramObj.replace(key, escapeXss(value.toString()));
                }
            }
        } else if (value instanceof JSONObject) {
            escapeXss((JSONObject) value);
            paramObj.replace(key, value);
        } else if (value instanceof JSONArray) {
            encodeHtml((JSONArray) value);
            paramObj.replace(key, value);
        }
    }

    /*
    private static Pattern scriptPattern = Pattern.compile("<script(.*?)</script>", Pattern.CASE_INSENSITIVE);
    private static Pattern javascriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    private static Pattern evalPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOLL);
    */

    public static String escapeXss(String str) {
        if (StringUtils.isNotBlank(str)) {
            /*
             处理js xss注入
             str = scriptPattern.matcher(str).replaceAll("");
             str = javascriptPattern.matcher(str).replaceAll("");
             str = evalPattern.matcher(str).replaceAll("");
            */
            //str = str.replace("&", "&amp;");
            str = str.replace("<", "&lt;");
            str = str.replace(">", "&gt;");
            str = str.replace("'", "&#39;");
            str = str.replace("\"", "&quot;");

            return str;
        }
        return "";
    }


    public static void escapeXss(JSONObject j) {
        Set<String> set = j.keySet();
        for (String s : set) {
            String newKey = escapeXss(s);
            if (!newKey.equals(s)) {
                Object value = j.get(s);
                j.remove(s);
                j.replace(newKey, value);
            }
            if (j.get(newKey) instanceof JSONObject) {
                escapeXss(j.getJSONObject(newKey));
            } else if (j.get(newKey) instanceof JSONArray) {
                encodeHtml(j.getJSONArray(newKey));
            } else if (j.get(newKey) instanceof String) {
                j.replace(newKey, escapeXss(j.getString(newKey)));
            }
        }
    }

    public static void encodeHtml(JSONArray j) {
        for (int i = 0; i < j.size(); i++) {
            if (j.get(i) instanceof JSONObject) {
                escapeXss(j.getJSONObject(i));
            } else if (j.get(i) instanceof JSONArray) {
                encodeHtml(j.getJSONArray(i));
            } else if (j.get(i) instanceof String) {
                j.set(i, escapeXss(j.getString(i)));
            }
        }
    }
}
