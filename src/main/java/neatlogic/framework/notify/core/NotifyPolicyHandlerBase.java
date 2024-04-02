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
