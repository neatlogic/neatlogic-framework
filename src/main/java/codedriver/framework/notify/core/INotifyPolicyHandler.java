package codedriver.framework.notify.core;

import java.util.List;

import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.ConditionParamVo;

public interface INotifyPolicyHandler {
	
	public String getName();
	
	public List<ValueTextVo> getNotifyTriggerList();
	
	public List<ValueTextVo> getParamTypeList();
	
	public List<ConditionParamVo> getSystemParamList();
	
	public List<ConditionParamVo> getSystemConditionOptionList();
	
	public JSONObject getAuthorityConfig();
	
	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}
}
