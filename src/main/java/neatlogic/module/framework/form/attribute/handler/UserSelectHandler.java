/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    public int getSort() {
        return 10;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj != null) {
            boolean isMultiple = configObj.getBooleanValue("isMultiple");
            attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
            if (isMultiple) {
                List<String> valueList = JSON.parseArray(JSON.toJSONString(dataObj), String.class);
                if (CollectionUtils.isNotEmpty(valueList)) {
                    List<String> textList = new ArrayList<>();
                    for (String key : valueList) {
                        textList.add(parse(key));
                    }
                    return textList;
                }
                return valueList;
            } else {
                String value = (String) dataObj;
                if (StringUtils.isNotBlank(value)) {
                    return parse(value);
                }
            }
        }
        return dataObj;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return valueConversionText(attributeDataVo, configObj);
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        if (CollectionUtils.isNotEmpty(values)) {
            boolean isMultiple = config.getBooleanValue("isMultiple");
            JSONArray groupList = config.getJSONArray("groupList");
            if (CollectionUtils.isNotEmpty(groupList)) {
                Object result = null;
                List<String> valueList = new ArrayList<>();
                if (isMultiple) {
                    for (String value : values) {
                        for (Object groupSearch : groupList) {
                            if (GroupSearch.USER.getValue().equals(groupSearch.toString())) {
                                List<String> list = userMapper.getUserUuidListByUserName(value);
                                if (CollectionUtils.isNotEmpty(list)) {
                                    valueList.add(GroupSearch.USER.getValuePlugin() + list.get(0));
                                    break;
                                }
                            } else if (GroupSearch.TEAM.getValue().equals(groupSearch.toString())) {
                                List<String> list = teamMapper.getTeamUuidByName(value);
                                if (CollectionUtils.isNotEmpty(list)) {
                                    valueList.add(GroupSearch.TEAM.getValuePlugin() + list.get(0));
                                    break;
                                }
                            } else if (GroupSearch.ROLE.getValue().equals(groupSearch.toString())) {
                                List<String> list = roleMapper.getRoleUuidByName(value);
                                if (CollectionUtils.isNotEmpty(list)) {
                                    valueList.add(GroupSearch.ROLE.getValuePlugin() + list.get(0));
                                    break;
                                }
                            } else if (GroupSearch.COMMON.getValue().equals(groupSearch.toString())) {
                                UserType[] userTypes = UserType.values();
                                for (UserType type : userTypes) {
                                    if (type.getText().equals(value)) {
                                        valueList.add(GroupSearch.COMMON.getValuePlugin() + type.getValue());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    result = valueList;
                } else {
                    String value = values.get(0);
                    for (Object groupSearch : groupList) {
                        if (GroupSearch.USER.getValue().equals(groupSearch.toString())) {
                            List<String> list = userMapper.getUserUuidListByUserName(value);
                            if (CollectionUtils.isNotEmpty(list)) {
                                result = GroupSearch.USER.getValuePlugin() + list.get(0);
                                break;
                            }
                        } else if (GroupSearch.TEAM.getValue().equals(groupSearch.toString())) {
                            List<String> list = teamMapper.getTeamUuidByName(value);
                            if (CollectionUtils.isNotEmpty(list)) {
                                result = GroupSearch.TEAM.getValuePlugin() + list.get(0);
                                break;
                            }
                        } else if (GroupSearch.ROLE.getValue().equals(groupSearch.toString())) {
                            List<String> list = roleMapper.getRoleUuidByName(value);
                            if (CollectionUtils.isNotEmpty(list)) {
                                result = GroupSearch.ROLE.getValuePlugin() + list.get(0);
                                break;
                            }
                        } else if (GroupSearch.COMMON.getValue().equals(groupSearch.toString())) {
                            UserType[] userTypes = UserType.values();
                            for (UserType type : userTypes) {
                                if (type.getText().equals(value)) {
                                    result = GroupSearch.COMMON.getValuePlugin() + type.getValue();
                                    break;
                                }
                            }
                        }
                    }
                }
                return result;
            }

        }
        return null;
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
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj != null) {
            JSONObject resultObj = new JSONObject();
            List<String> valueList = new ArrayList<>();
            List<String> textList = new ArrayList<>();
            List<ValueTextVo> valueTextList = new ArrayList<>();
            boolean isMultiple = configObj.getBooleanValue("isMultiple");
            attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
            if (isMultiple) {
                valueList = JSON.parseArray(JSON.toJSONString(dataObj), String.class);
                if (CollectionUtils.isNotEmpty(valueList)) {
                    for (String value : valueList) {
                        String text = parse(value);
                        textList.add(text);
                        valueTextList.add(new ValueTextVo(value, text));
                    }
                }
            } else {
                String value = (String) dataObj;
                if (StringUtils.isNotBlank(value)) {
                    String text = parse(value);
                    textList.add(text);
                    valueList.add(value);
                    valueTextList.add(new ValueTextVo(value, text));
                }
            }
            resultObj.put("text", textList);
            resultObj.put("value", valueList);
            resultObj.put("valueTextList", valueTextList);
            return resultObj;
        }
        return null;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject detailedData = getMyDetailedData(attributeDataVo, configObj);
        if (detailedData != null) {
            JSONArray text = detailedData.getJSONArray("text");
            if (CollectionUtils.isNotEmpty(text)) {
                return String.join(",", text.toJavaList(String.class));
            }
        }
        return null;
    }
}