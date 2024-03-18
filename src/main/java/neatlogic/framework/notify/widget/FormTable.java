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

package neatlogic.framework.notify.widget;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import neatlogic.framework.form.dto.AttributeExtendedDataVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;

public class FormTable implements TemplateMethodModelEx {

    private static Map<String, Function<Object, String>> map = new HashMap<>();

    static {
        // 文本框
        map.put("formtext", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 文本域
        map.put("formtextarea", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 富文本框
        map.put("formckeditor", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 数字
        map.put("formnumber", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 密码
        map.put("formpassword", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 时间
        map.put("formtime", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 日期
        map.put("formdate", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 超链接
        map.put("formlink", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 评分
        map.put("formrate", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 上传附件
        map.put("formupload", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 树形下拉框
        map.put("formtreeselect", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 连接协议
        map.put("formprotocol", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 级联下拉框
        map.put("formcascader", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 复选框
        map.put("formcheckbox", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 单选框
        map.put("formradio", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 下拉框
        map.put("formselect", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 用户选择
        map.put("formuserselect", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 矩阵选择
        map.put("formcube", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 执行目标
        map.put("formresoureces", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 配置项选择
        map.put("formcientityselector", dataObj -> {
            if (dataObj instanceof String) {
                return (String) dataObj;
            } else {
                return dataObj.toString();
            }
        });
        // 账号
        map.put("formaccounts", dataObj -> {
            if (dataObj instanceof JSONObject) {
                JSONObject tableObj = (JSONObject) dataObj;
                return createTableHtml(tableObj);
            } else {
                return StringUtils.EMPTY;
            }
        });
        // 配置项修改
        map.put("formcientitymodify", dataObj -> {
            if (dataObj instanceof JSONObject) {
                JSONObject tableObj = (JSONObject) dataObj;
                return createTableHtml(tableObj);
            } else {
                return StringUtils.EMPTY;
            }
        });
        // 表格选择
        map.put("formtableselector", dataObj -> {
            if (dataObj instanceof JSONObject) {
                JSONObject tableObj = (JSONObject) dataObj;
                return createTableHtml(tableObj);
            } else {
                return StringUtils.EMPTY;
            }
        });
        // 表格输入
        map.put("formtableinputer", dataObj -> {
            if (dataObj instanceof JSONObject) {
                JSONObject tableObj = (JSONObject) dataObj;
                return createNestedTableHtml(tableObj);
            } else {
                return StringUtils.EMPTY;
            }
        });
    }

    /**
     * 生成表格，表单的账号、配置项修改、表格选择等组件需要用到表格
     *
     * @param tableObj
     * @return
     */
    private static String createTableHtml(JSONObject tableObj) {
        JSONArray theadList = tableObj.getJSONArray("theadList");
        if (CollectionUtils.isEmpty(theadList)) {
            return StringUtils.EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder("<table border=\"1\">");
        stringBuilder.append("<tr>");
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < theadList.size(); i++) {
            JSONObject theadObj = theadList.getJSONObject(i);
            if (MapUtils.isEmpty(theadObj)) {
                continue;
            }
            String title = theadObj.getString("title");
            String key = theadObj.getString("key");
            if (StringUtils.isBlank(title) || StringUtils.isBlank(key)) {
                continue;
            }
            stringBuilder.append("<th>");
            stringBuilder.append(title);
            stringBuilder.append("</th>");
            keyList.add(key);
        }
        stringBuilder.append("</tr>");
        JSONArray tbodyList = tableObj.getJSONArray("tbodyList");
        if (CollectionUtils.isNotEmpty(tbodyList)) {
            for (int i = 0; i < tbodyList.size(); i++) {
                JSONObject tbodyObj = tbodyList.getJSONObject(i);
                if (MapUtils.isEmpty(tbodyObj)) {
                    continue;
                }
                stringBuilder.append("<tr>");
                for (String key : keyList) {
                    String value = tbodyObj.getString(key);
                    if (value == null) {
                        value = StringUtils.EMPTY;
                    }
                    stringBuilder.append("<td>");
                    stringBuilder.append(value);
                    stringBuilder.append("</td>");
                }
                stringBuilder.append("</tr>");
            }
        }
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    /**
     * 生成嵌套表格，表单的表格输入组件需要用到嵌套表格
     * @param tableObj
     * @return
     */
    private static String createNestedTableHtml(JSONObject tableObj) {
        JSONArray theadList = tableObj.getJSONArray("theadList");
        if (CollectionUtils.isEmpty(theadList)) {
            return StringUtils.EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder("<table border=\"1\">");
        stringBuilder.append("<tr>");
        Map<String, String> keyToHandlerMap = new HashMap<>();
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < theadList.size(); i++) {
            JSONObject theadObj = theadList.getJSONObject(i);
            if (MapUtils.isEmpty(theadObj)) {
                continue;
            }
            String title = theadObj.getString("title");
            String key = theadObj.getString("key");
            String handler = theadObj.getString("handler");
            if (StringUtils.isBlank(title) || StringUtils.isBlank(key) || StringUtils.isBlank(handler)) {
                continue;
            }
            stringBuilder.append("<th>");
            stringBuilder.append(title);
            stringBuilder.append("</th>");
            keyList.add(key);
            keyToHandlerMap.put(key, handler);
        }
        stringBuilder.append("</tr>");
        JSONArray tbodyList = tableObj.getJSONArray("tbodyList");
        if (CollectionUtils.isNotEmpty(tbodyList)) {
            for (int i = 0; i < tbodyList.size(); i++) {
                JSONObject tbodyObj = tbodyList.getJSONObject(i);
                if (MapUtils.isEmpty(tbodyObj)) {
                    continue;
                }
                stringBuilder.append("<tr>");
                for (String key : keyList) {
                    stringBuilder.append("<td>");
                    String handler = keyToHandlerMap.get(key);
                    if (Objects.equals(handler, "formtable")) {
                        JSONObject cellTableObj = tbodyObj.getJSONObject(key);
                        if (MapUtils.isNotEmpty(cellTableObj)) {
                            stringBuilder.append(createTableHtml(cellTableObj));
                        } else {
                            stringBuilder.append(StringUtils.EMPTY);
                        }
                    } else {
                        String value = tbodyObj.getString(key);
                        if (value == null) {
                            value = StringUtils.EMPTY;
                        }
                        stringBuilder.append(value);
                    }
                    stringBuilder.append("</td>");
                }
                stringBuilder.append("</tr>");
            }
        }
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }
    // 表单组件数据列表
    private List<AttributeExtendedDataVo> attributeExtendedDataList;

    public FormTable(List<AttributeExtendedDataVo> attributeDataList) {
        this.attributeExtendedDataList = attributeDataList;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (CollectionUtils.isEmpty(attributeExtendedDataList)) {
            return StringUtils.EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder("<table border=\"1\" width=\"100%\">");
        for (AttributeExtendedDataVo attributeDataVo : attributeExtendedDataList) {
            if (attributeDataVo == null) {
                continue;
            }
            String attributeLabel = attributeDataVo.getAttributeLabel();
            String type = attributeDataVo.getHandler();
            Object dataObj = attributeDataVo.getExtendedData();
            String result = StringUtils.EMPTY;
            if (dataObj != null) {
                Function<Object, String> function = map.get(type);
                if (function != null) {
                    result = function.apply(dataObj);
                }
            }
            stringBuilder.append("<tr>");
            stringBuilder.append("<td class=\"label\">");
            stringBuilder.append(attributeLabel);
            stringBuilder.append("</td>");
            stringBuilder.append("<td>");
            stringBuilder.append(result);
            stringBuilder.append("</td>");
            stringBuilder.append("</tr>");
        }
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }
}
