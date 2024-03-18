/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.form.attribute.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.form.treeselect.core.ITreeSelectDataSourceHandler;
import neatlogic.framework.form.treeselect.core.TreeSelectDataSourceFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linbq
 * @since 2021/8/3 18:39
 **/
@Component
public class TreeSelectHandler extends FormHandlerBase {
    @Override
    public String getHandler() {
        return FormHandler.FORMTREESELECT.getHandler();
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMTREESELECT.getHandlerName();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "select";
    }

    @Override
    public String getIcon() {
        return "ts-sitemap";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.STRING;
    }

    @Override
    public String getDataType() {
        return "string";
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    public boolean isConditionable() {
        return true;
    }

    @Override
    public boolean isShowable() {
        return true;
    }

    @Override
    public boolean isValueable() {
        return false;
    }

    @Override
    public boolean isFilterable() {
        return false;
    }

    @Override
    public boolean isExtendable() {
        return false;
    }

    @Override
    public boolean isForTemplate() {
        return true;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return false;
    }

    @Override
    public String getModule() {
        return "framework";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public Object conversionDataType(Object source, String attributeLabel) {
        return convertToString(source, attributeLabel);
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = textArray.toJavaList(String.class);
            return String.join("/", textList);
        }
        return resultObj.get("value");
//        String data = (String) attributeDataVo.getDataObj();
//        if (StringUtils.isNotBlank(data)) {
//            String dataSource = configObj.getString("dataSource");
//            if (StringUtils.isNotBlank(dataSource)) {
//                ITreeSelectDataSourceHandler handler = TreeSelectDataSourceFactory.getHandler(dataSource);
//                if (handler != null) {
//                    List<String> pathList = handler.valueConversionTextPathList(data);
//                    return String.join("/", pathList);
//                }
//            }
//        }
//        return data;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = textArray.toJavaList(String.class);
            return String.join("/", textList);
        }
        return null;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return null;
    }

    @Override
    public int getSort() {
        return 13;
    }

    /*
    表单组件配置信息
    {
        "handler": "formtreeselect",
        "reaction": {
            "hide": {},
            "readonly": {},
            "disable": {},
            "display": {}
        },
        "override_config": {},
        "icon": "tsfont-topo",
        "hasValue": true,
        "label": "树型下拉框_16",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "isMask": false,
            "width": "100%",
            "description": "",
            "dataSource": "knowledgeType",
            "config": {
                "textName": "name",
                "valueName": "uuid",
                "url": "/api/rest/knowledge/document/type/tree/forselect"
            },
            "url": "/api/rest/knowledge/document/type/tree/forselect",
            "isHide": false
        },
        "uuid": "289d1989bc5e42cba23499272ac4a531"
    }
     */
    /*
    保存数据结构
    7174540d09f043948fb4e168045a4094
     */
    /*
    返回数据结构
    {
        "value": "7174540d09f043948fb4e168045a4094",
        "text": "发布文档",
        "textList": ["测试", "发布文档"]
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        String value = (String) attributeDataVo.getDataObj();
        resultObj.put("value", value);
        if (StringUtils.isNotBlank(value)) {
            String dataSource = configObj.getString("dataSource");
            if (StringUtils.isNotBlank(dataSource)) {
                ITreeSelectDataSourceHandler handler = TreeSelectDataSourceFactory.getHandler(dataSource);
                if (handler != null) {
                    List<String> pathList = handler.valueConversionTextPathList(value);
                    resultObj.put("textList", pathList);
                    if (CollectionUtils.isNotEmpty(pathList)) {
                        resultObj.put("text", pathList.get(pathList.size() - 1));
                    }
                }
            }
        }
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = textArray.toJavaList(String.class);
            return String.join("/", textList);
        }
        return resultObj.get("value");
    }
}
