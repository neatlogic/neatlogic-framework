/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.notify.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.ConditionParamVo;
import neatlogic.framework.notify.constvalue.CommonNotifyParam;
import neatlogic.framework.notify.dto.NotifyTriggerVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class NotifyPolicyHandlerBase implements INotifyPolicyHandler{

	@Override
	public List<NotifyTriggerVo> getNotifyTriggerList() {
		return myNotifyTriggerList();
	}

	protected abstract List<NotifyTriggerVo> myNotifyTriggerList();

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
			paramVo.setIsEditable(0);
			paramVo.setFreemarkerTemplate(param.getFreemarkerTemplate());
			resultList.add(paramVo);
		}
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
