package codedriver.framework.notify.core;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.BasicType;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyPolicyParamVo;

public abstract class NotifyPolicyHandlerBase implements INotifyPolicyHandler{

	@Override
	public List<ValueTextVo> getNotifyTriggerList() {
		return myNotifyTriggerList();
	}

	protected abstract List<ValueTextVo> myNotifyTriggerList();
	
	@Override
	public List<ValueTextVo> getParamTypeList() {
		List<ValueTextVo> resultList = new ArrayList<>();
		for(BasicType type : BasicType.values()) {
			resultList.add(new ValueTextVo(type.getName(), type.getText()));
		}
//		for (ApiParamType type : ApiParamType.values()) {
//			NotifyPolicyParamTypeVo notifyPolicyParamTypeVo = new NotifyPolicyParamTypeVo();
//			notifyPolicyParamTypeVo.setHandler(type.getValue());
//			notifyPolicyParamTypeVo.setHandlerName(type.getText());
//			notifyPolicyParamTypeVo.setHandlerType("input");
//			notifyPolicyParamTypeVo.setType("base");
//			notifyPolicyParamTypeVo.setDefaultExpression("equals");
//			List<ProcessExpressionVo> expressionList = new ArrayList<>();
//			ProcessExpressionVo processExpressionVo1 = new ProcessExpressionVo();
//			processExpressionVo1.setExpression("equals");
//			processExpressionVo1.setExpressionName("等于");
//			expressionList.add(processExpressionVo1);
//			notifyPolicyParamTypeVo.setExpressionList(expressionList);
//			ProcessExpressionVo processExpressionVo2 = new ProcessExpressionVo();
//			processExpressionVo2.setExpression("like");
//			processExpressionVo2.setExpressionName("包含");
//			expressionList.add(processExpressionVo2);
//			notifyPolicyParamTypeVo.setExpressionList(expressionList);
//			resultList.add(notifyPolicyParamTypeVo);
//		}
		return resultList;
	}

	@Override
	public List<NotifyPolicyParamVo> getSystemParamList() {
		return mySystemParamList();
	}
	
	protected abstract List<NotifyPolicyParamVo> mySystemParamList();

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
