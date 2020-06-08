package codedriver.framework.notify.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.dto.BasePageVo;

public class NotifyPolicyInvokerVo extends BasePageVo {

	private Long policyId;
	private String invoker;
	private JSONObject config;
	
	public Long getPolicyId() {
		return policyId;
	}
	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}
	public String getInvoker() {
		return invoker;
	}
	public void setInvoker(String invoker) {
		this.invoker = invoker;
	}
	public JSONObject getConfig() {
		return config;
	}
	public void setConfig(String config) {
		try {
			this.config = JSONObject.parseObject(config);
		} catch (Exception ex) {

		}
	}
	
	@JSONField(serialize = false)
	public String getConfigStr() {
		if (getConfig() != null) {
			return config.toJSONString();
		}
		return null;
	}
	
}
