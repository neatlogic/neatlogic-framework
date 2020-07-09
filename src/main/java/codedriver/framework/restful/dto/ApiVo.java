package codedriver.framework.restful.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.restful.core.ApiComponentFactory;

public class ApiVo extends BasePageVo implements Serializable {

	private static final long serialVersionUID = 3689437871016436622L;

	public enum Type {
		OBJECT("object", "对象模式"), STREAM("stream", "json流模式"), BINARY("binary", "字节流模式");

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

	public enum ApiType {
		SYSTEM("system", "系统接口"), CUSTOM("custom", "自定义接口");

		private String name;
		private String text;

		private ApiType(String _name, String _text) {
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
			for (ApiType s : ApiType.values()) {
				if (s.getValue().equals(name)) {
					return s.getText();
				}
			}
			return "";
		}
	}

	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "处理器", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "处理器名", type = ApiParamType.STRING)
	private String handlerName;
	@EntityField(name = "配置信息，json格式", type = ApiParamType.JSONOBJECT)
	private String config;
	@EntityField(name = "状态", type = ApiParamType.INTEGER)
	private Integer isActive;
	@EntityField(name = "地址", type = ApiParamType.STRING)
	private String token;
	@EntityField(name = "使用期限", type = ApiParamType.LONG)
	private Date expire;
	@EntityField(name = "描述", type = ApiParamType.STRING)
	private String description;
	@EntityField(name = "用户名", type = ApiParamType.STRING)
	private String username;
	@EntityField(name = "密码", type = ApiParamType.STRING)
	private String password;
	@EntityField(name = "认证方式", type = ApiParamType.STRING)
	private String authtype = "";
	@EntityField(name = "请求时效", type = ApiParamType.INTEGER)
	private Integer timeout = 0;
	@EntityField(name = "是否失效", type = ApiParamType.BOOLEAN)
	private boolean isExpire;
	@EntityField(name = "模块ID", type = ApiParamType.STRING)
	private String moduleId;
	@EntityField(name = "访问次数", type = ApiParamType.INTEGER)
	private Integer visitTimes = 0;
	@EntityField(name = "接口类型", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "接口类型名称", type = ApiParamType.STRING)
	private String typeText;
	@EntityField(name = "是否需要保存记录", type = ApiParamType.INTEGER)
	private Integer needAudit = 0;
	@EntityField(name = "访问频率", type = ApiParamType.INTEGER)
	private Integer qps = 0;
	@EntityField(name = "是否能删除", type = ApiParamType.INTEGER)
	private Integer isDeletable = 1;
	@EntityField(name = "是否是私有接口", type = ApiParamType.BOOLEAN)
	private Boolean isPrivate;
	@EntityField(name = "接口类型(系统接口-system，自定义接口-custom)", type = ApiParamType.STRING)
	private String apiType;
	@EntityField(name = "功能ID(从token中截取第一个单词而来)", type = ApiParamType.STRING)
	private String funcId;
//	@EntityField(name = "访问次数", type = ApiParamType.INTEGER)
//	private Integer count;
	@JSONField(serialize = false)
	private transient JSONObject pathVariableObj;
	@JSONField(serialize = false)
	private transient List<String> pathVariableList;
	@JSONField(serialize = false)
	private transient String keyword;
	@JSONField(serialize = false)
	private transient List<String> tokenList;
//	private Long totalDataSize = 0l;
//	private String totalDataSizeText;

	public void addPathVariable(String para) {
		if (pathVariableList == null) {
			pathVariableList = new ArrayList<>();
		}
		pathVariableList.add(para);
	}
	
	public ApiVo() {
		this.setPageSize(20);
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List<String> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<String> tokenList) {
		this.tokenList = tokenList;
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
		if (type != null) {
			return type;
		}
		if (handler == null) {
			return null;
		}
		ApiHandlerVo apiHandlerVo = ApiComponentFactory.getApiHandlerByHandler(handler);
		if (apiHandlerVo == null) {
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
		if (handlerName != null) {
			return handlerName;
		}
		if (handler == null) {
			return null;
		}
		ApiHandlerVo apiHandlerVo = ApiComponentFactory.getApiHandlerByHandler(handler);
		if (apiHandlerVo == null) {
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

	public Date getExpire() {
		return expire;
	}

	public boolean getIsExpire() {
		if (this.expire != null) {
			return this.expire.after(new Date());
		}
		return isExpire;
	}

	public void setExpire(Date expire) {
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

//	public Long getTotalDataSize() {
//		return totalDataSize;
//	}
//
//	public void setTotalDataSize(Long totalDataSize) {
//		this.totalDataSize = totalDataSize;
//	}
//
//	public String getTotalDataSizeText() {
//		if (this.totalDataSize != null) {
//			if (this.totalDataSize / (1024 * 1024 * 1024) > 1) {
//				totalDataSizeText = ((float) this.totalDataSize / (1024 * 1024 * 1024)) + "GB";
//			} else if (this.totalDataSize / (1024 * 1024) > 1) {
//				totalDataSizeText = ((float) this.totalDataSize / (1024 * 1024)) + "MB";
//			} else if (this.totalDataSize / (1024) > 1) {
//				totalDataSizeText = ((float) this.totalDataSize / (1024)) + "KB";
//			} else {
//				totalDataSizeText = this.totalDataSize + "B";
//			}
//		}
//		return totalDataSizeText;
//	}
//
//	public void setTotalDataSizeText(String totalDataSizeText) {
//		this.totalDataSizeText = totalDataSizeText;
//	}

	public Integer getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(Integer needAudit) {
		this.needAudit = needAudit;
	}

	public Integer getQps() {
		return qps;
	}

	public void setQps(Integer qps) {
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

	public Boolean getIsPrivate() {
		if (isPrivate != null) {
			return isPrivate;
		}
		if (handler == null) {
			return null;
		}
		ApiHandlerVo apiHandlerVo = ApiComponentFactory.getApiHandlerByHandler(handler);
		if (apiHandlerVo == null) {
			return null;
		}
		isPrivate = apiHandlerVo.isPrivate();
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public String getFuncId() {
		return funcId;
	}

	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

//	public Integer getCount() {
//		return count;
//	}
//
//	public void setCount(Integer count) {
//		this.count = count;
//	}

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
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	public JSONObject getPathVariableObj() {
		return pathVariableObj;
	}

	public void setPathVariableObj(JSONObject pathVariableObj) {
		this.pathVariableObj = pathVariableObj;
	}

	public List<String> getPathVariableList() {
		return pathVariableList;
	}

	public void setPathVariableList(List<String> pathVariableList) {
		this.pathVariableList = pathVariableList;
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
