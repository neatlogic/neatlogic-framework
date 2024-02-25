/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.form.attribute.handler;

import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserSelectHandler extends FormHandlerBase {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public String getHandler() {
        return FormHandler.FORMUSERSELECT.getHandler();
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public Object conversionDataType(Object source, String attributeLabel) {
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            String sourceStr = (String) source;
            if (sourceStr.startsWith("[") && sourceStr.endsWith("]")) {
                try {
                    return JSONObject.parseArray((String) source);
                } catch (Exception e) {
                    throw new AttributeValidException(attributeLabel);
                }
            } else {
                if (sourceStr.contains("#")) {
                    return source;
                }
                throw new AttributeValidException(attributeLabel);
            }
        } else if (source instanceof JSONArray) {
            return source;
        }
        throw new AttributeValidException(attributeLabel);
    }

    @Override
    public int getSort() {
        return 10;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            return textArray;
        }
        return resultObj.getJSONArray("valueList");
//        Object dataObj = attributeDataVo.getDataObj();
//        if (dataObj != null) {
//            boolean isMultiple = configObj.getBooleanValue("isMultiple");
//            attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
//            if (isMultiple) {
//                List<String> valueList = JSON.parseArray(JSON.toJSONString(dataObj), String.class);
//                if (CollectionUtils.isNotEmpty(valueList)) {
//                    List<String> textList = new ArrayList<>();
//                    for (String key : valueList) {
//                        textList.add(parse(key));
//                    }
//                    return textList;
//                }
//                return valueList;
//            } else {
//                String value = (String) dataObj;
//                if (StringUtils.isNotBlank(value)) {
//                    return parse(value);
//                }
//            }
//        }
//        return dataObj;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = textArray.toJavaList(String.class);
            return String.join("、", textList);
        }
        return null;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        if (text == null) {
            return null;
        }
        JSONArray groupArray = config.getJSONArray("groupList");
        if (CollectionUtils.isEmpty(groupArray)) {
            return null;
        }
        List<String> groupList = groupArray.toJavaList(String.class);
        boolean isMultiple = config.getBooleanValue("isMultiple");
        if (text instanceof String) {
            String textStr = (String) text;
            if (groupList.contains(GroupSearch.COMMON.getValue())) {
                if (Objects.equals(textStr, UserType.ALL.getText())) {
                    String value = GroupSearch.COMMON.getValuePlugin() + UserType.ALL.getValue();
                    if (isMultiple) {
                        List<String> arraylist = new ArrayList<>();
                        arraylist.add(value);
                        return arraylist;
                    }
                    return value;
                }
            }
            if (groupList.contains(GroupSearch.USER.getValue())) {
                UserVo userVo = userMapper.getUserByUserId(textStr);
                if (userVo != null) {
                    String value = GroupSearch.USER.getValuePlugin() + userVo.getUuid();
                    if (isMultiple) {
                        List<String> arraylist = new ArrayList<>();
                        arraylist.add(value);
                        return arraylist;
                    }
                    return value;
                }
                List<String> list = userMapper.getUserUuidListByUserName(textStr);
                if (CollectionUtils.isNotEmpty(list)) {
                    String value = GroupSearch.USER.getValuePlugin() + list.get(0);
                    if (isMultiple) {
                        List<String> arraylist = new ArrayList<>();
                        arraylist.add(value);
                        return arraylist;
                    }
                    return value;
                }
            }
            if (groupList.contains(GroupSearch.TEAM.getValue())) {
                List<String> list = teamMapper.getTeamUuidByName(textStr);
                if (CollectionUtils.isNotEmpty(list)) {
                    String value = GroupSearch.TEAM.getValuePlugin() + list.get(0);
                    if (isMultiple) {
                        List<String> arraylist = new ArrayList<>();
                        arraylist.add(value);
                        return arraylist;
                    }
                    return value;
                }
            }
            if (groupList.contains(GroupSearch.ROLE.getValue())) {
                List<String> list = roleMapper.getRoleUuidByName(textStr);
                if (CollectionUtils.isNotEmpty(list)) {
                    String value = GroupSearch.ROLE.getValuePlugin() + list.get(0);
                    if (isMultiple) {
                        List<String> arraylist = new ArrayList<>();
                        arraylist.add(value);
                        return arraylist;
                    }
                    return value;
                }
            }
        } else if (text instanceof List) {
            List<String> textList = (List) text;
            if (CollectionUtils.isEmpty(textList)) {
                return textList;
            }
            List<String> valueList = new ArrayList<>();
            for (String textStr : textList) {
                if (groupList.contains(GroupSearch.COMMON.getValue())) {
                    if (Objects.equals(textStr, UserType.ALL.getText())) {
                        valueList.add(GroupSearch.COMMON.getValuePlugin() + UserType.ALL.getValue());
                        break;
                    }
                }
                if (groupList.contains(GroupSearch.USER.getValue())) {
                    UserVo userVo = userMapper.getUserByUserId(textStr);
                    if (userVo != null) {
                        valueList.add(GroupSearch.USER.getValuePlugin() + userVo.getUuid());
                        break;
                    }
                    List<String> list = userMapper.getUserUuidListByUserName(textStr);
                    if (CollectionUtils.isNotEmpty(list)) {
                        valueList.add(GroupSearch.USER.getValuePlugin() + list.get(0));
                        break;
                    }
                }
                if (groupList.contains(GroupSearch.TEAM.getValue())) {
                    List<String> list = teamMapper.getTeamUuidByName(textStr);
                    if (CollectionUtils.isNotEmpty(list)) {
                        valueList.add(GroupSearch.TEAM.getValuePlugin() + list.get(0));
                        break;
                    }
                }
                if (groupList.contains(GroupSearch.ROLE.getValue())) {
                    List<String> list = roleMapper.getRoleUuidByName(textStr);
                    if (CollectionUtils.isNotEmpty(list)) {
                        valueList.add(GroupSearch.ROLE.getValuePlugin() + list.get(0));
                        break;
                    }
                }
            }
            return valueList;
        }
        return text;
    }

    private String parse(String key) {
        if (key.contains("#")) {
            String[] split = key.split("#");
            if (GroupSearch.COMMON.getValue().equals(split[0])) {
                return UserType.getText(split[1]);
            } else if (GroupSearch.USER.getValue().equals(split[0])) {
                UserVo user = userMapper.getUserBaseInfoByUuid(split[1]);
                if (user != null) {
                    return user.getUserName();
                } else {
                    return split[1];
                }
            } else if (GroupSearch.TEAM.getValue().equals(split[0])) {
                TeamVo team = teamMapper.getTeamByUuid(split[1]);
                if (team != null) {
                    return team.getName();
                } else {
                    return split[1];
                }
            } else if (GroupSearch.ROLE.getValue().equals(split[0])) {
                RoleVo role = roleMapper.getRoleByUuid(split[1]);
                if (role != null) {
                    return role.getName();
                } else {
                    return split[1];
                }
            }
        }
        return key;
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMUSERSELECT.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "tsfont-userinfo";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.ARRAY;
    }

    @Override
    public String getDataType() {
        return "string";
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
        return true;
    }

    @Override
    public boolean isFilterable() {
        return true;
    }

    @Override
    public boolean isExtendable() {
        return false;
    }

    @Override
    public String getModule() {
        return "framework";
    }

    @Override
    public boolean isForTemplate() {
        return true;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return true;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "userselect";
    }

    @Override
    protected List<String> myIndexFieldContentList(String data) {
        if (data.startsWith("[") && data.endsWith("]")) {
            JSONArray jsonArray = JSONArray.parseArray(data);
            return JSONObject.parseArray(jsonArray.toJSONString(), String.class);
        }
        return null;
    }

    //表单组件配置信息
//{
//	"handler": "formuserselect",
//	"label": "用户选择器_9",
//	"type": "form",
//	"uuid": "d3f620927a584d2d868372cc64bd73df",
//	"config": {
//		"isRequired": false,
//		"defaultValueList": "user#003367933ead65dba67e1df1e66b1000",
//		"ruleList": [],
//		"width": "100%",
//		"validList": [],
//		"isMultiple": false,
//		"groupList": [
//			"user"
//		],
//		"quoteUuid": "",
//		"defaultValueType": "self",
//		"placeholder": "请选择",
//		"authorityConfig": [
//			"common#alluser"
//		]
//	}
//}
    //保存数据结构
//    "user#003367933ead65dba67e1df1e66b1000"
    //返回数据结构
//{
//	"valueTextList": [
//		{
//			"text": "蒋琪",
//			"value": "user#003367933ead65dba67e1df1e66b1000"
//		}
//	],
//	"text": [
//		"蒋琪"
//	],
//	"value": [
//		"user#003367933ead65dba67e1df1e66b1000"
//	]
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj == null) {
            return resultObj;
        }
        List<String> valueList = new ArrayList<>();
        List<String> textList = new ArrayList<>();
        List<ValueTextVo> valueTextList = new ArrayList<>();
//        boolean isMultiple = configObj.getBooleanValue("isMultiple");
//        attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
        if (dataObj instanceof JSONArray) {
            JSONArray valueArray = (JSONArray) dataObj;
            if (CollectionUtils.isNotEmpty(valueArray)) {
                valueList = valueArray.toJavaList(String.class);
                for (String value : valueList) {
                    String text = parse(value);
                    textList.add(text);
                    valueTextList.add(new ValueTextVo(value, text));
                }
            }
        } else if (dataObj instanceof String) {
            String value = (String) dataObj;
            if (StringUtils.isNotBlank(value)) {
                String text = parse(value);
                textList.add(text);
                valueList.add(value);
                valueTextList.add(new ValueTextVo(value, text));
            }
        }
        resultObj.put("textList", textList);
        resultObj.put("valueList", valueList);
        resultObj.put("valueTextList", valueTextList);
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject detailedData = getMyDetailedData(attributeDataVo, configObj);
        if (detailedData != null) {
            JSONArray text = detailedData.getJSONArray("textList");
            if (CollectionUtils.isNotEmpty(text)) {
                return String.join(",", text.toJavaList(String.class));
            }
        }
        return null;
    }
}
