package codedriver.framework.restful.dto;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.core.ApiComponentFactory;

public class ApiVo extends BasePageVo implements Serializable{

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

	private String name;
	private String handler;
	private String handlerName;
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
	private Integer isDeletable = 1;
	
	private transient String keyword;
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getTypeText() {
		if (getType() != null) {
			typeText = Type.getText(type);
		}
		return typeText;
	}

	public void setTypeText(String typeText) {
		this.typeText = typeText;
	}

	public String getType() {
		if(type != null) {
			return type;
		}
		if(handler == null) {
			return null;
		}
		ApiHandlerVo apiHandlerVo = ApiComponentFactory.getApiHandlerByHandler(handler);
		if(apiHandlerVo == null) {
			return null;
		}
		type = apiHandlerVo.getType();
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getHandlerName() {
		if(handlerName != null) {
			return handlerName;
		}
		if(handler == null) {
			return null;
		}
		ApiHandlerVo apiHandlerVo = ApiComponentFactory.getApiHandlerByHandler(handler);
		if(apiHandlerVo == null) {
			return null;
		}
		handlerName = apiHandlerVo.getName();
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
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

	public Integer getIsDeletable() {
		return isDeletable;
	}

	public void setIsDeletable(Integer isDeletable) {
		this.isDeletable = isDeletable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authtype == null) ? 0 : authtype.hashCode());
		result = prime * result + ((config == null) ? 0 : config.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((expire == null) ? 0 : expire.hashCode());
		result = prime * result + ((handler == null) ? 0 : handler.hashCode());
		result = prime * result + ((isActive == null) ? 0 : isActive.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((needAudit == null) ? 0 : needAudit.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((qps == null) ? 0 : qps.hashCode());
		result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiVo other = (ApiVo) obj;
		if (authtype == null) {
			if (other.authtype != null)
				return false;
		} else if (!authtype.equals(other.authtype))
			return false;
		if (config == null) {
			if (other.config != null)
				return false;
		} else if (!config.equals(other.config))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (expire == null) {
			if (other.expire != null)
				return false;
		} else if (!expire.equals(other.expire))
			return false;
		if (handler == null) {
			if (other.handler != null)
				return false;
		} else if (!handler.equals(other.handler))
			return false;
		if (isActive == null) {
			if (other.isActive != null)
				return false;
		} else if (!isActive.equals(other.isActive))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (needAudit == null) {
			if (other.needAudit != null)
				return false;
		} else if (!needAudit.equals(other.needAudit))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (qps == null) {
			if (other.qps != null)
				return false;
		} else if (!qps.equals(other.qps))
			return false;
		if (timeout == null) {
			if (other.timeout != null)
				return false;
		} else if (!timeout.equals(other.timeout))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

//	@Override
//	public boolean equals(Object other) {
//		if (this == other)
//			return true;
//		if (other == null)
//			return false;
//		if (!(other instanceof ApiVo))
//			return false;
//
//		final ApiVo api = (ApiVo) other;
//		try {
//			if (getToken().equals(api.getToken())) {
//				return true;
//			}
//		} catch (Exception ex) {
//			return false;
//		}
//		return false;
//	}
//
//	@Override
//	public int hashCode() {
//		int result = getToken().hashCode() * 117;
//		return result;
//	}
}
