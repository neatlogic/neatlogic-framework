package codedriver.framework.restful.dto;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import codedriver.framework.common.dto.BasePageVo;

public class ApiVo extends BasePageVo implements Serializable {

	private static final long serialVersionUID = 3689437871016436622L;

	public enum Type {
		OBJECT("object", "对象模式"),
		STREAM("stream", "json流模式"),
		BINARY("binary", "字节流模式");

		private String name;
		private String text;

		private Type(String _name, String _text) {
			this.name = _name;
			this.text = _text;
		}

		public String getValue() {
			return name;
		}

		public String getText() {
			return text;
		}

		public static String getText(String name) {
			for (Type s : Type.values()) {
				if (s.getValue().equals(name)) {
					return s.getText();
				}
			}
			return "";
		}
	}

	private Integer id;
	private String name;
	private String componentId;
	private String componentName;
	private String config;
	private Integer isActive;
	private String token;
	private String expire;
	private String description;
	private String username;
	private String password;
	private String authtype = "";
	private Integer timeout;
	private boolean isExpire;
	private String moduleId;
	private Integer visitTimes = 0;
	private Long totalDataSize = 0l;
	private String totalDataSizeText;
	private String type;
	private String typeText;
	private Integer needAudit;
	private Double qps;

	public String getTypeText() {
		if (type != null) {
			typeText = Type.getText(type);
		}
		return typeText;
	}

	public void setTypeText(String typeText) {
		this.typeText = typeText;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private List<Integer> idList;

	public List<Integer> getIdList() {
		return idList;
	}

	public void setIdList(List<Integer> idList) {
		this.idList = idList;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getAuthtype() {
		return authtype;
	}

	public void setAuthtype(String authtype) {
		this.authtype = authtype;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpire() {
		if (expire != null && this.expire.equals("")) {
			return null;
		}
		return expire;
	}

	public boolean getIsExpire() {
		isExpire = false;
		if (this.expire != null && !this.expire.equals("")) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date ed = f.parse(this.expire);
				Date now = new Date();
				isExpire = ed.before(now);
			} catch (ParseException e) {
				isExpire = false;
			}
		}
		return isExpire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getVisitTimes() {
		return visitTimes;
	}

	public void setVisitTimes(Integer visitTimes) {
		this.visitTimes = visitTimes;
	}

	public Long getTotalDataSize() {
		return totalDataSize;
	}

	public void setTotalDataSize(Long totalDataSize) {
		this.totalDataSize = totalDataSize;
	}

	public String getTotalDataSizeText() {
		if (this.totalDataSize != null) {
			if (this.totalDataSize / (1024 * 1024 * 1024) > 1) {
				totalDataSizeText = ((float) this.totalDataSize / (1024 * 1024 * 1024)) + "GB";
			} else if (this.totalDataSize / (1024 * 1024) > 1) {
				totalDataSizeText = ((float) this.totalDataSize / (1024 * 1024)) + "MB";
			} else if (this.totalDataSize / (1024) > 1) {
				totalDataSizeText = ((float) this.totalDataSize / (1024)) + "KB";
			} else {
				totalDataSizeText = this.totalDataSize + "B";
			}
		}
		return totalDataSizeText;
	}

	public void setTotalDataSizeText(String totalDataSizeText) {
		this.totalDataSizeText = totalDataSizeText;
	}

	public Integer getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(Integer needAudit) {
		this.needAudit = needAudit;
	}

	public Double getQps() {
		return qps;
	}

	public void setQps(Double qps) {
		this.qps = qps;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof ApiVo))
			return false;

		final ApiVo api = (ApiVo) other;
		try {
			if (getToken().equals(api.getToken())) {
				return true;
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = getToken().hashCode() * 117;
		return result;
	}
}
