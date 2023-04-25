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

package neatlogic.framework.notify.core;

import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.ConditionParamVo;
import neatlogic.framework.notify.constvalue.CommonNotifyParam;
import neatlogic.framework.notify.dto.NotifyTriggerTemplateVo;
import neatlogic.framework.notify.dto.NotifyTriggerVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class NotifyPolicyHandlerBase implements INotifyPolicyHandler{

	@Override
	public List<NotifyTriggerVo> getNotifyTriggerList() {
		return myNotifyTriggerList();
	}

	protected abstract List<NotifyTriggerVo> myNotifyTriggerList();

	/** 获取通知触发点模版列表 */
	@Override
	public List<NotifyTriggerTemplateVo> getNotifyTriggerTemplateList(NotifyHandlerType type) {
		return myNotifyTriggerTemplateList(type);
	}

	protected abstract List<NotifyTriggerTemplateVo> myNotifyTriggerTemplateList(NotifyHandlerType type);

	@Override
	public List<ValueTextVo> getParamTypeList() {
		List<ValueTextVo> resultList = new ArrayList<>();
		for(ParamType type : ParamType.values()) {
			resultList.add(new ValueTextVo(type.getName(), type.getText()));
		}
		return resultList;
	}

	@Override
	public List<ConditionParamVo> getSystemParamList() {
	    List<ConditionParamVo> resultList = new ArrayList<>();
		for (CommonNotifyParam param : CommonNotifyParam.values()) {
			ConditionParamVo paramVo = new ConditionParamVo();
			paramVo.setName(param.getValue());
			paramVo.setLabel(param.getText());
			paramVo.setController("input");

			paramVo.setType("common");
			ParamType paramType = param.getParamType();
			paramVo.setParamType(paramType.getName());
			paramVo.setParamTypeName(paramType.getText());
//			paramVo.setDefaultExpression(paramType.getDefaultExpression().getExpression());
//			for(Expression expression : paramType.getExpressionList()) {
//				paramVo.getExpressionList().add(new ExpressionVo(expression.getExpression(), expression.getExpressionName()));
//			}
			paramVo.setIsEditable(0);
			paramVo.setFreemarkerTemplate(param.getFreemarkerTemplate());
			resultList.add(paramVo);
		}
//	    if(StringUtils.isNotBlank(Config.HOME_URL())) {
//	        ConditionParamVo param = new ConditionParamVo();
//	        param.setName("homeUrl");
//	        param.setLabel("域名");
//	        param.setController("input");
//
//	        param.setType("common");
//	        param.setParamType(ParamType.STRING.getName());
//	        param.setParamTypeName(ParamType.STRING.getText());
//	        param.setDefaultExpression(ParamType.STRING.getDefaultExpression().getExpression());
//	        for(Expression expression : ParamType.STRING.getExpressionList()) {
//	            param.getExpressionList().add(new ExpressionVo(expression.getExpression(), expression.getExpressionName()));
//	        }
//	        param.setIsEditable(0);
//	        param.setFreemarkerTemplate("<a href=\"${homeUrl}\" target=\"_blank\"></a>");
//	        resultList.add(param);
//	    }
	    List<ConditionParamVo> mySystemParamList = mySystemParamList();
	    if(CollectionUtils.isNotEmpty(mySystemParamList)) {
	        resultList.addAll(mySystemParamList);
	    }
		return resultList;
	}
	
	protected abstract List<ConditionParamVo> mySystemParamList();

	protected ConditionParamVo createConditionParam(INotifyParam param) {
		ConditionParamVo paramVo = new ConditionParamVo();
		paramVo.setName(param.getValue());
		paramVo.setLabel(param.getText());
		paramVo.setParamType(param.getParamType().getName());
		paramVo.setParamTypeName(param.getParamType().getText());
		paramVo.setFreemarkerTemplate(param.getFreemarkerTemplate());
		paramVo.setIsEditable(0);
		return paramVo;
	}

    @Override
    public List<ConditionParamVo> getSystemConditionOptionList() {
        return mySystemConditionOptionList();
    }

    protected abstract List<ConditionParamVo> mySystemConditionOptionList();
    
	@Override
	public JSONObject getAuthorityConfig() {
		JSONObject config = new JSONObject();
		List<String> excludeList = new ArrayList<>();
		config.put("excludeList", excludeList);
		List<String> includeList = new ArrayList<>();
		config.put("includeList", includeList);
		List<String> groupList = new ArrayList<>();
		groupList.add(GroupSearch.USER.getValue());
		groupList.add(GroupSearch.TEAM.getValue());
		groupList.add(GroupSearch.ROLE.getValue());
		config.put("groupList", groupList);
		myAuthorityConfig(config);
		return config;
	}
	
	protected abstract void myAuthorityConfig(JSONObject config);
}
