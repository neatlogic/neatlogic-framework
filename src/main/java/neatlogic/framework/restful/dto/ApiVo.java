/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.restful.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.restful.enums.PublicApiAuthType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiVo extends BasePageVo implements Serializable,Cloneable {

    private static final long serialVersionUID = 3689437871016436622L;


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
    @EntityField(name = "token", type = ApiParamType.STRING)
    private String token;
    @EntityField(name = "地址", type = ApiParamType.STRING)
    private String url;
    @EntityField(name = "帮助地址", type = ApiParamType.STRING)
    private String helpUrl;
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
    @EntityField(name = "认证方式名称", type = ApiParamType.STRING)
    private String authtypeName = "";
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
    @EntityField(name = "接口数据类型，stream,rest,binary", type = ApiParamType.STRING)
    private String dataType;
    @EntityField(name = "访问频率", type = ApiParamType.DOUBLE)
    private Double qps = 0.0;
    @EntityField(name = "是否能删除", type = ApiParamType.INTEGER)
    private Integer isDeletable = 1;
    @EntityField(name = "是否是私有接口", type = ApiParamType.BOOLEAN)
    private Boolean isPrivate;
    @EntityField(name = "接口类型(系统接口-system，自定义接口-custom)", type = ApiParamType.STRING)
    private String apiType;
    @EntityField(name = "功能ID(从token中截取第一个单词而来)", type = ApiParamType.STRING)
    private String funcId;
    @EntityField(name = "模块group", type = ApiParamType.STRING)
    private String moduleGroup;
    @EntityField(name = "模块group名称", type = ApiParamType.STRING)
    private String moduleGroupName;
    @JSONField(serialize = false)
    private JSONObject pathVariableObj;
    @JSONField(serialize = false)
    private List<String> pathVariableList;
    @JSONField(serialize = false)
    private List<String> tokenList;
    //	private Long totalDataSize = 0l;
//	private String totalDataSizeText;
    @JSONField(serialize = false)
    private String authorization;

    @JSONField(serialize = false)
    private String timezone;

    //明文
    private String passwordPlain;
    //密文
    private String passwordCipher;

    public void addPathVariable(String para) {
        if (pathVariableList == null) {
            pathVariableList = new ArrayList<>();
        }
        pathVariableList.add(para);
    }

    public ApiVo() {
        this.setPageSize(20);
    }

    public String getAuthtypeName() {
        if (StringUtils.isBlank(authtypeName) && StringUtils.isNotBlank(authtype)) {
            authtypeName = PublicApiAuthType.getText(authtype);
        }
        return authtypeName;
    }

    public void setAuthtypeName(String authtypeName) {
        this.authtypeName = authtypeName;
    }

    public List<String> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<String> tokenList) {
        this.tokenList = tokenList;
    }

    public String getTypeText() {
        if (getType() != null) {
            typeText = ApiParamType.getText(type);
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
        ApiHandlerVo apiHandlerVo = PrivateApiComponentFactory.getApiHandlerByHandler(handler);
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
        return getPasswordPlain();
    }

    public void setPassword(String password) {
        this.passwordPlain = password;
        this.password = password;
    }

    public String getHandlerName() {
        if (handlerName != null) {
            return handlerName;
        }
        if (handler == null) {
            return null;
        }
        ApiHandlerVo apiHandlerVo = PrivateApiComponentFactory.getApiHandlerByHandler(handler);
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
        if (token.startsWith("/")) {
            return token.substring(1);
        }
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

    public Double getQps() {
        return qps;
    }

    public void setQps(Double qps) {
        this.qps = qps;
    }

    public String getModuleId() {
        if (StringUtils.isBlank(moduleId) && StringUtils.isNotBlank(handler)) {
            //根据handler从apiHandlerMap中取出ApiHandlerVo的moduleId
            ApiHandlerVo apiHandlerVo = PrivateApiComponentFactory.getApiHandlerMap().get(handler);
            if (apiHandlerVo != null) {
                moduleId = apiHandlerVo.getModuleId();
            }
        }
        return moduleId;
    }

//	public String getModuleId(){return moduleId;}

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
        ApiHandlerVo apiHandlerVo = PrivateApiComponentFactory.getApiHandlerByHandler(handler);
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

    public String getModuleGroup() {
        if (StringUtils.isBlank(moduleGroup) && StringUtils.isNotBlank(moduleId)) {
            ModuleVo vo = ModuleUtil.getModuleById(moduleId);
            if (vo != null) {
                moduleGroup = vo.getGroup();
            }
        }
        return moduleGroup;
    }

    public void setModuleGroup(String moduleGroup) {
        this.moduleGroup = moduleGroup;
    }

    public String getModuleGroupName() {
        if (StringUtils.isBlank(moduleGroupName) && StringUtils.isNotBlank(moduleGroup)) {
            ModuleGroupVo group = ModuleUtil.getModuleGroupMap().get(moduleGroup);
            if (group != null) {
                String groupName = group.getGroupName();
                if (StringUtils.isNotBlank(groupName)) {
                    moduleGroupName = groupName;
                }
            }
        }
        return moduleGroupName;
    }

//	public String getModuleGroup(){return moduleGroup;}

//	public void setModuleGroup(String moduleGroup) {
//		this.moduleGroup = moduleGroup;
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ApiVo other = (ApiVo) obj;
        if (authtype == null) {
            if (other.authtype != null) {
                return false;
            }
        } else if (!authtype.equals(other.authtype)) {
            return false;
        }
        if (config == null) {
            if (other.config != null) {
                return false;
            }
        } else if (!config.equals(other.config)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (expire == null) {
            if (other.expire != null) {
                return false;
            }
        } else if (!expire.equals(other.expire)) {
            return false;
        }
        if (handler == null) {
            if (other.handler != null) {
                return false;
            }
        } else if (!handler.equals(other.handler)) {
            return false;
        }
        if (isActive == null) {
            if (other.isActive != null) {
                return false;
            }
        } else if (!isActive.equals(other.isActive)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (needAudit == null) {
            if (other.needAudit != null) {
                return false;
            }
        } else if (!needAudit.equals(other.needAudit)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (qps == null) {
            if (other.qps != null) {
                return false;
            }
        } else if (!qps.equals(other.qps)) {
            return false;
        }
        if (timeout == null) {
            if (other.timeout != null) {
                return false;
            }
        } else if (!timeout.equals(other.timeout)) {
            return false;
        }
        if (token == null) {
            if (other.token != null) {
                return false;
            }
        } else if (!token.equals(other.token)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
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

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getUrl() {
        if (StringUtils.isBlank(url) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(token) && isPrivate != null) {
            url = "api/" + ApiType.getUrlPre(type) + token;
            if (!isPrivate) {
                this.url = "public/" + url;
            }
        }
        return url;
    }

    public String getHelpUrl() {
        if (StringUtils.isBlank(helpUrl) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(token) && isPrivate != null) {
            helpUrl = "api/help/" + ApiType.getUrlPre(type) + token;
            if (!isPrivate) {
                helpUrl = "public/" + helpUrl;
            }

        }
        return helpUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPasswordPlain(String passwordPlain) {
        this.passwordPlain = passwordPlain;
    }

    public String getPasswordPlain() {
        if (StringUtils.isBlank(passwordPlain)) {
            if (StringUtils.isNotBlank(passwordCipher)) {
                this.passwordPlain = RC4Util.decrypt(this.passwordCipher);
            }
        }
        return passwordPlain;
    }

    public String getPasswordCipher() {
        if (StringUtils.isBlank(passwordCipher)) {
            if (StringUtils.isNotBlank(passwordPlain)) {
                this.passwordCipher = RC4Util.encrypt(passwordPlain);
            }
        }
        return passwordCipher;
    }

    public void setPasswordCipher(String passwordCipher) {
        this.passwordCipher = passwordCipher;
    }
    @Override
    public ApiVo clone() throws CloneNotSupportedException {
        return (ApiVo) super.clone();
    }

}
