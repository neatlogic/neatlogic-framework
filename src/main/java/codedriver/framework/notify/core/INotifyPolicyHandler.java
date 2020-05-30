package codedriver.framework.notify.core;

import java.util.List;

import org.springframework.util.ClassUtils;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyPolicyParamTypeVo;
import codedriver.framework.notify.dto.NotifyPolicyParamVo;

public interface INotifyPolicyHandler {
	
	public String getName();
	
	public List<ValueTextVo> getNotifyTriggerList();
	
	public List<NotifyPolicyParamTypeVo> getParamTypeList();
	
	public List<NotifyPolicyParamVo> getSystemParamList();
	
	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}
}
