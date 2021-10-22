package codedriver.framework.integration.dto;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.integration.core.IntegrationInvokerBase;
import codedriver.framework.integration.core.IntegrationInvokerFactory;
import codedriver.framework.util.Md5Util;
@Deprecated
public class IntegrationInvokeVo {
	private String integrationUuid;
	private IntegrationInvokerBase invoker;
	private JSONObject invokeConfig;

	public IntegrationInvokeVo(String _integrationUuid, IntegrationInvokerBase _invoker) {
		this.integrationUuid = _integrationUuid;
		this.invoker = _invoker;
	}

	public JSONObject getInvokeConfig() {
		if (invokeConfig == null) {
			return invoker.getConfig();
		} else {
			return invokeConfig;
		}
	}

	public String getInvokeConfigStr() {
		if (this.getInvokeConfig() != null) {
			return this.getInvokeConfig().toJSONString();
		} else {
			return null;
		}
	}

	public String getInvokeHash() {
		return Md5Util.encryptMD5(this.getInvokeConfigStr());
	}

	public JSONObject getDetail() {
		return null;//IntegrationInvokerFactory.getHandler(invoker).getInvokeDetail(invoker.getConfig());
	}

	public String getIntegrationUuid() {
		return integrationUuid;
	}

	public void setIntegrationUuid(String integrationUuid) {
		this.integrationUuid = integrationUuid;
	}

	public IntegrationInvokerBase getInvoker() {
		return invoker;
	}

	public void setInvokeConfig(String invokeConfig) {
		try {
			this.invokeConfig = JSONObject.parseObject(invokeConfig);
		} catch (Exception ex) {

		}
	}
}
