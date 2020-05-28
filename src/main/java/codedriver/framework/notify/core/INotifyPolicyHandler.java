package codedriver.framework.notify.core;

import java.util.List;

import org.springframework.util.ClassUtils;

import codedriver.framework.common.dto.ValueTextVo;

public interface INotifyPolicyHandler {

	//public String getType();
	
	public String getName();
	
	public List<ValueTextVo> getNotifyTriggerList();
	
	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}
}
