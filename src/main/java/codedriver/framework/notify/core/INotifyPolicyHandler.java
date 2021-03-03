package codedriver.framework.notify.core;

import java.util.List;

import codedriver.framework.notify.dto.NotifyTriggerTemplateVo;
import codedriver.framework.notify.dto.NotifyTriggerVo;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.ConditionParamVo;

public interface INotifyPolicyHandler {
	
	public String getName();
	
	public List<NotifyTriggerVo> getNotifyTriggerList();

	public List<NotifyTriggerVo> getNotifyTriggerListForNotifyTree();

	/** 获取通知触发点模版列表 */
	public List<NotifyTriggerTemplateVo> getNotifyTriggerTemplateList(NotifyHandlerType type);

	public List<ValueTextVo> getParamTypeList();
	
	public List<ConditionParamVo> getSystemParamList();
	
	public List<ConditionParamVo> getSystemConditionOptionList();
	
	public JSONObject getAuthorityConfig();
	
	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	/** 绑定权限，每种handler对应不同的权限 */
	public String getAuthName();
}
