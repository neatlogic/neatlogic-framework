package codedriver.framework.notify.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BaseEditorVo;
import codedriver.framework.notify.constvalue.NotifyPolicyActionType;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class NotifyPolicyVo extends BaseEditorVo {
	
	@EntityField(name = "策略id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "策略名", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "操作用户", type = ApiParamType.STRING)
	private String actionUser;
	@EntityField(name = "操作时间", type = ApiParamType.LONG)
	private Date actionTime;
	@EntityField(name = "操作类型，create|update", type = ApiParamType.STRING)
	private String action;
	@EntityField(name = "操作类型名，创建|修改", type = ApiParamType.STRING)
	private String actionName;
	@EntityField(name = "引用数量", type = ApiParamType.INTEGER)
	private int invokerCount;
	@EntityField(name = "配置项信息", type = ApiParamType.STRING)
	private String config;
	private transient JSONObject configObj;
	@EntityField(name = "通知策略处理器", type = ApiParamType.STRING)
	private String handler;
	
	
	public NotifyPolicyVo(String name, String handler) {
		this.name = name;
		this.handler = handler;
	}
	
	public Long getId() {
		if(id == null) {
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getActionUser() {
		if(StringUtils.isBlank(actionUser)) {
			if(StringUtils.isNotBlank(getLcuName())) {
				actionUser = getLcuName();
			}else if(StringUtils.isNotBlank(getFcuName())) {
				actionUser = getFcuName();
			}
		}
		return actionUser;
	}
	public void setActionUser(String actionUser) {
		this.actionUser = actionUser;
	}
	public Date getActionTime() {
		if(actionTime == null) {
			if(getLcd() != null) {
				actionTime = getLcd();
			}else if(getFcd() != null) {
				actionTime = getFcd();
			}
		}
		return actionTime;
	}
	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}
	public String getAction() {
		if(action == null) {
			if(getLcd() != null) {
				action = NotifyPolicyActionType.UPDATE.getValue();
			}else if(getFcd() != null) {
				action = NotifyPolicyActionType.CREATE.getValue();
			}
		}
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getActionName() {
		if(actionName == null) {
			if(getLcd() != null) {
				actionName = NotifyPolicyActionType.UPDATE.getText();
			}else if(getFcd() != null) {
				actionName = NotifyPolicyActionType.CREATE.getText();
			}
		}
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public int getInvokerCount() {
		return invokerCount;
	}

	public void setInvokerCount(int invokerCount) {
		this.invokerCount = invokerCount;
	}

	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public JSONObject getConfigObj() {
		if(configObj == null && StringUtils.isNotBlank(config)) {
			try {
				configObj = JSON.parseObject(config);
			}catch(JSONException e) {
				//TODO linbq打印日志
			}
		}
		return configObj;
	}
	public void setConfigObj(JSONObject configObj) {
		this.configObj = configObj;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}

}
