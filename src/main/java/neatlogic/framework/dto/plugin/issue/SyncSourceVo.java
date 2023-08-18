package neatlogic.framework.dto.plugin.issue;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.util.SnowflakeUtil;

import java.io.Serializable;

/**
 * @author 
 * 
 */
public class SyncSourceVo extends BasePageVo implements Serializable {

    private static final long serialVersionUID = -6345148868592428580L;

    private Long id;
    /**
     * 来源
     */
    private String source;

    /**
     * 来源分类
     */
    private String type;

    /**
     * 请求来源名称
     */
    private String name;

    /**
     * 请求来源url
     */
    private String url;

    /**
     * url请求结果根节点类型，jsonarray或者json
     */
    private String rootType;

    /**
     * 请求接口调用认证方式
     */
    private String authType;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求模式
     */
    private String requestMode;

    /**
     * 请求认证对应用户名
     */
    private String username;

    /**
     * 请求认证对应密码
     */
    private String password;

    /**
     * 请求来源查询条件配置信息
     */
    private String config;

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRootType() {
        return rootType;
    }

    public void setRootType(String rootType) {
        this.rootType = rootType;
    }

    public String getAuthType() {
        return authType;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestMode() {
        return requestMode;
    }

    public void setRequestMode(String requestMode) {
        this.requestMode = requestMode;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
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

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}