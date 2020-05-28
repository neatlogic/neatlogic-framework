package codedriver.framework.notify.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BaseEditorVo;
import codedriver.framework.notify.constvalue.NotifyPolicyActionType;
import codedriver.framework.restful.annotation.EntityField;

public class NotifyPolicyVo extends BaseEditorVo {
	public final static Map<String, NotifyPolicyVo> notifyPolicyMap = new HashMap<>();
	static {
		Date currentDate = new Date();
		for(int i = 0; i < 100; i++) {
			NotifyPolicyVo notifyPolicyVo = new NotifyPolicyVo();
			notifyPolicyVo.setName("test" + i);
			notifyPolicyVo.setReferenceCount(i%10);
			if(i % 2 == 0) {
				notifyPolicyVo.setFcu("linbq");
				notifyPolicyVo.setFcd(new Date(currentDate.getTime() + (i * 1000)));
				notifyPolicyVo.setFcuName("林邦泉");
				notifyPolicyVo.setLcu("lvzk");
				notifyPolicyVo.setLcd(new Date(currentDate.getTime() + (i * 1000)));
				notifyPolicyVo.setLcuName("吕佐康");
			}else {
				notifyPolicyVo.setFcu("linbq");
				notifyPolicyVo.setFcd(new Date(currentDate.getTime() + (i * 1000)));
				notifyPolicyVo.setFcuName("林邦泉");
			}
			JSONObject configObj = new JSONObject();
			JSONArray triggerList = new JSONArray();
//			for (NotifyTriggerType notifyTriggerType : NotifyTriggerType.values()) {
//				if(NotifyTriggerType.TIMEOUT == notifyTriggerType) {
//					continue;
//				}
//				JSONObject triggerObj = new JSONObject();
//				triggerObj.put("trigger", notifyTriggerType.getTrigger());
//				triggerObj.put("triggerName", notifyTriggerType.getText());
//				triggerObj.put("handlerList", new JSONArray());
//				triggerList.add(triggerObj);
//			}
			configObj.put("triggerList", triggerList);
			notifyPolicyVo.setConfig(configObj.toJSONString());
			notifyPolicyMap.put(notifyPolicyVo.getUuid(), notifyPolicyVo);
		}
	}
	@EntityField(name = "策略uuid", type = ApiParamType.STRING)
	private String uuid;
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
	private int referenceCount;
	@EntityField(name = "配置项信息", type = ApiParamType.STRING)
	private String config;
	private transient JSONObject configObj;
	@EntityField(name = "模块id", type = ApiParamType.STRING)
	private String moduleId;
	public synchronized String getUuid() {
		if(StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	public int getReferenceCount() {
		return referenceCount;
	}
	public void setReferenceCount(int referenceCount) {
		this.referenceCount = referenceCount;
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
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
}
