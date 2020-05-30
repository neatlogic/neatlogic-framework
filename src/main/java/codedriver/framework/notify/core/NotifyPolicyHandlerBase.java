package codedriver.framework.notify.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyPolicyParamTypeVo;
import codedriver.framework.notify.dto.NotifyPolicyParamVo;
import codedriver.framework.notify.dto.ProcessExpressionVo;

public abstract class NotifyPolicyHandlerBase implements INotifyPolicyHandler{

	@Override
	public List<ValueTextVo> getNotifyTriggerList() {
		return myNotifyTriggerList();
	}

	protected abstract List<ValueTextVo> myNotifyTriggerList();
	
	@Override
	public List<NotifyPolicyParamTypeVo> getParamTypeList() {
		List<NotifyPolicyParamTypeVo> resultList = new ArrayList<>();
		for (ApiParamType type : ApiParamType.values()) {
			NotifyPolicyParamTypeVo notifyPolicyParamTypeVo = new NotifyPolicyParamTypeVo();
			notifyPolicyParamTypeVo.setHandler(type.getValue());
			notifyPolicyParamTypeVo.setHandlerName(type.getText());
			notifyPolicyParamTypeVo.setHandlerType("input");
			notifyPolicyParamTypeVo.setType("base");
			notifyPolicyParamTypeVo.setDefaultExpression("equals");
			List<ProcessExpressionVo> expressionList = new ArrayList<>();
			ProcessExpressionVo processExpressionVo1 = new ProcessExpressionVo();
			processExpressionVo1.setExpression("equals");
			processExpressionVo1.setExpressionName("等于");
			expressionList.add(processExpressionVo1);
			notifyPolicyParamTypeVo.setExpressionList(expressionList);
			ProcessExpressionVo processExpressionVo2 = new ProcessExpressionVo();
			processExpressionVo2.setExpression("like");
			processExpressionVo2.setExpressionName("包含");
			expressionList.add(processExpressionVo2);
			notifyPolicyParamTypeVo.setExpressionList(expressionList);
			resultList.add(notifyPolicyParamTypeVo);
		}
		List<NotifyPolicyParamTypeVo> paramTypeList = myParamTypeList();
		if(CollectionUtils.isNotEmpty(paramTypeList)) {
			resultList.addAll(paramTypeList);
		}
		return resultList;
	}

	protected abstract List<NotifyPolicyParamTypeVo> myParamTypeList();

	@Override
	public List<NotifyPolicyParamVo> getSystemParamList() {
		return null;
	}
	
	protected abstract List<NotifyPolicyParamTypeVo> mySystemParamList();
}
