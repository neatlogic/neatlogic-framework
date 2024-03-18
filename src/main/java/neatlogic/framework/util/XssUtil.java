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
