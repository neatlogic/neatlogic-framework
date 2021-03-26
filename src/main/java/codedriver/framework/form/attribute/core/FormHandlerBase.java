/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.attribute.core;

import codedriver.framework.common.constvalue.FormHandlerType;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.restful.core.IApiComponent;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public abstract class FormHandlerBase implements IFormAttributeHandler {

    public enum ConversionType {

        TOVALUE("toValue", "text转换成value"),
        TOTEXT("toText", "value转换成text");

        private String value;
        private String text;

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        private ConversionType(String _value, String _text) {
            value = _value;
            text = _text;
        }
    }

    @Override
    public final String getType() {
        return "form";
    }

    protected String getValue(String matrixUuid, ValueTextVo mapping, String value, IApiComponent restComponent, ApiVo api) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("matrixUuid", matrixUuid);
            JSONArray columnList = new JSONArray();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            paramObj.put("columnList", columnList);
            JSONObject resultObj = (JSONObject) restComponent.doService(api, paramObj, null);
            JSONArray columnDataList = resultObj.getJSONArray("columnDataList");
            for (int i = 0; i < columnDataList.size(); i++) {
                JSONObject firstObj = columnDataList.getJSONObject(i);
                JSONObject valueObj = firstObj.getJSONObject((String) mapping.getValue());
                /** 当text与value字段相同时，不同类型的矩阵字段，拼接value的逻辑不同，下拉、用户、组、角色，按uuid&=&text拼接，其余按value&=&value拼接 **/
                if (mapping.getValue().equals(mapping.getText())
                        && (GroupSearch.USER.getValue().equals(valueObj.getString("type"))
                        || GroupSearch.ROLE.getValue().equals(valueObj.getString("type"))
                        || GroupSearch.TEAM.getValue().equals(valueObj.getString("type"))
                        || FormHandlerType.SELECT.toString().equals(valueObj.getString("type")))
                        && value.equals(valueObj.getString("text"))) {
                    return valueObj.getString("value") + IFormAttributeHandler.SELECT_COMPOSE_JOINER + valueObj.getString("text");
                } else if (mapping.getValue().equals(mapping.getText()) && value.equals(valueObj.getString("text"))) {
                    return valueObj.getString("value") + IFormAttributeHandler.SELECT_COMPOSE_JOINER + valueObj.getString("value");
                }
                if (valueObj.getString("compose").contains(value)) {
                    return valueObj.getString("compose");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> indexFieldContentList(String data) {
        List<String> contentList = myIndexFieldContentList(data);
        if (CollectionUtils.isEmpty(contentList)) {
            contentList = Collections.singletonList(data);
        }
        return contentList;
    }

    protected List<String> myIndexFieldContentList(String data) {
        return null;
    }

    @Override
    public Boolean isNeedSliceWord() {
        return true;
    }
}
