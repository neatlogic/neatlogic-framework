package codedriver.framework.integration.dto;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class IntegrationVo {
	@EntityField(name = "uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "目标地址", type = ApiParamType.STRING)
	private String url;
	@EntityField(name = "组件", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "组件名称", type = ApiParamType.STRING)
	private String handlerName;
	@EntityField(name = "请求方法", type = ApiParamType.STRING)
	private String method;
	@EntityField(name = "输入参数模板", type = ApiParamType.JSONARRAY)
	private List<PartternVo> inputPartternList;
	@EntityField(name = "配置", type = ApiParamType.JSONOBJECT)
	private JSONObject config;
	@JSONField(serialize = false)
	private transient String configStr;
	// 请求参数
	@JSONField(serialize = false)
	private transient JSONObject paramObj;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PartternVo> getInputPartternList() {
		return inputPartternList;
	}

	public void setInputPartternList(List<PartternVo> inputPartternList) {
		this.inputPartternList = inputPartternList;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	public String getConfigStr() {
		if (StringUtils.isBlank(configStr) && config != null) {
			configStr = config.toJSONString();
		}
		return configStr;
	}

	public void setConfigStr(String configStr) {
		this.configStr = configStr;
	}

	public void setConfig(JSONObject config) {
		this.config = config;
	}

	public JSONObject getConfig() {
		return config;
	}

	public JSONObject getParamObj() {
		return paramObj;
	}

	public void setParamObj(JSONObject paramObj) {
		this.paramObj = paramObj;
	}

}
