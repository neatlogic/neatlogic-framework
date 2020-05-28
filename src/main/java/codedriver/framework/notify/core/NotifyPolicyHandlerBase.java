package codedriver.framework.notify.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.ValueTextVo;

public abstract class NotifyPolicyHandlerBase implements INotifyPolicyHandler{

	@Override
	public List<ValueTextVo> getNotifyTriggerList() {
		return myNotifyTriggerList();
	}

	protected abstract List<ValueTextVo> myNotifyTriggerList();
	
	@Override
	public List<ValueTextVo> getVariableTypeList() {
		List<ValueTextVo> resultList = new ArrayList<>();
		for (ApiParamType type : ApiParamType.values()) {
			resultList.add(new ValueTextVo(type.getValue(), type.getText()));
		}
		List<ValueTextVo> variableTypeList = myVariableTypeList();
		if(CollectionUtils.isNotEmpty(variableTypeList)) {
			resultList.addAll(variableTypeList);
		}
		return resultList;
	}

	protected abstract List<ValueTextVo> myVariableTypeList();
}
