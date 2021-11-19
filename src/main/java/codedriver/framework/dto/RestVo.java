/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.integration.authentication.enums.AuthenticateType;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class RestVo {
    private String id;
    private boolean lazyLoad = false;
    private String url;
    private String authType;
    private String username;
    private String password;
    private String token;
    private String method;
    private JSONObject payload;
    private String tenant;
    List<String> paramNameList;
    List<String> paramValueList;
    private int connectTimeout = 5000;
    private int readTimeout = 30000;//毫秒

    private List<Object> paramList;
    private Map<String, Object> paramMap;

    public RestVo() {
    }

    public RestVo(String url, String authType, JSONObject payload) {
        this.url = url;
        this.authType = authType;
        this.payload = payload;
    }

    public RestVo(String url, String authType, String username, String password, JSONObject payload) {
        this.url = url;
        this.authType = authType;
        this.username = username;
        this.password = password;
        this.payload = payload;
    }

    public RestVo(String url, String authType, String username, String password) {
        this.url = url;
        this.authType = authType;
        this.username = username;
        this.password = password;
    }

    public String getAuthType() {
        return authType;
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

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public List<Object> getParamList() {
        return paramList;
    }

    public void setParamList(List<Object> paramList) {
        this.paramList = paramList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLazyLoad() {
        return lazyLoad;
    }

    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

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

    public List<String> getParamNameList() {
        return paramNameList;
    }

    public void setParamNameList(List<String> paramNameList) {
        this.paramNameList = paramNameList;
    }

    public List<String> getParamValueList() {
        return paramValueList;
    }

    public void setParamValueList(List<String> paramValueList) {
        this.paramValueList = paramValueList;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JSONObject getAuthConfig() {
        JSONObject authObj = new JSONObject();
        if (AuthenticateType.BASIC.getValue().equals(this.authType)) {
            authObj.put("username", this.getUsername());
            authObj.put("password", this.getPassword());
        } else if (AuthenticateType.BEARER.getValue().equals(this.authType)) {
            authObj.put("token", this.token);
        }
        return authObj;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getTenant() {
        if (StringUtils.isBlank(tenant)) {
            return TenantContext.get().getTenantUuid();
        }
        return tenant;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
