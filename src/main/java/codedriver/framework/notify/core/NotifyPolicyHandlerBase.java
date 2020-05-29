package codedriver.framework.notify.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyPolicyParamTypeVo;

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
//			resultList.add(new ValueTextVo(type.getValue(), type.getText()));
		}
		List<NotifyPolicyParamTypeVo> paramTypeList = myParamTypeList();
		if(CollectionUtils.isNotEmpty(paramTypeList)) {
			resultList.addAll(paramTypeList);
		}
		return resultList;
	}

	protected abstract List<NotifyPolicyParamTypeVo> myParamTypeList();
}
