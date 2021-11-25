/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.file.dto.FileVo;
import codedriver.framework.integration.authentication.enums.AuthenticateType;
import codedriver.framework.util.RestUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class RestVo {
    private String id;
    private boolean lazyLoad ;
    private String url;
    private String authType;
    private String username;
    private String password;
    private String token;
    private String method;
    private JSONObject payload;
    private JSONObject formData;
    private String tenant;
    List<String> paramNameList;
    List<String> paramValueList;
    private int connectTimeout;
    private int readTimeout;
    private String contentType;
    private List<FileVo> fileVoList;

    private List<Object> paramList;
    private Map<String, Object> paramMap;

    public static class Builder {
        //必要参数
        private final String url;
        private final String authType;
        //非必要参数
        private String username;
        private String password;
        private JSONObject payload;
        private JSONObject formData;
        private boolean lazyLoad = false;
        private String id;
        private int connectTimeout = 5000;
        private int readTimeout = 30000;//毫秒
        private String contentType = RestUtil.APPLICATION_JSON;
        private List<FileVo> fileVoList;

        public Builder(String url,String authType){
            this.url = url;
            this.authType = authType;
        }

        /**
         * 设置验证用户
         * @param username 验证用户
         */
        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * 设置验证密码
         * @param password 验证密码
         */
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * 设置json入参
         * @param payload json入参
         */
        public Builder setPayload(JSONObject payload) {
            this.payload = payload;
            return this;
        }

        public Builder setLazyLoad(boolean lazyLoad) {
            this.lazyLoad = lazyLoad;
            return this;
        }

        public Builder setId(String id){
            this.id = id;
            return this;
        }

        /**
         * 设置链接超时时间
         * @param connectTimeout 链接超时
         */
        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * 设置读取超时时间
         * @param readTimeout 读取超时
         */
        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * 设置Content-Type 默认为 APPLICATION_JSON
         * @param contentType 数据类型
         */
        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * 设置上传文件 用于ContentType为multipart/form-data
         * @param fileVoList 文件列表
         */
        public Builder setFileVoList(List<FileVo> fileVoList) {
            this.fileVoList = fileVoList;
            return this;
        }

        /**
         * form入参 用于ContentType为multipart/form-data
         * @param formData form 入参
         */
        public Builder setFormData(JSONObject formData) {
            this.formData = formData;
            return this;
        }

        public RestVo build(){
            return new RestVo(this);
        }
    }

    public RestVo() {
    }

    public RestVo(Builder builder) {
        this.url = builder.url;
        this.authType = builder.authType;
        this.username = builder.username;
        this.password = builder.password;
        this.payload = builder.payload;
        this.connectTimeout = builder.connectTimeout;
        this.id = builder.id;
        this.readTimeout = builder.readTimeout;
        this.lazyLoad = builder.lazyLoad;
        this.contentType = builder.contentType;
        this.formData = builder.formData;
        this.fileVoList = builder.fileVoList;
    }

    public String getAuthType() {
        return authType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public List<Object> getParamList() {
        return paramList;
    }

    public String getId() {
        return id;
    }

    public boolean isLazyLoad() {
        return lazyLoad;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public List<String> getParamNameList() {
        return paramNameList;
    }

    public List<String> getParamValueList() {
        return paramValueList;
    }

    public JSONObject getPayload() {
        if(payload == null){
            payload = new JSONObject();
        }
        return payload;
    }

    public String getToken() {
        return token;
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

    public String getTenant() {
        if (StringUtils.isBlank(tenant)) {
            return TenantContext.get().getTenantUuid();
        }
        return tenant;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public String getContentType() {
        return contentType;
    }

    public JSONObject getFormData() {
        return formData;
    }

    public List<FileVo> getFileVoList() {
        return fileVoList;
    }
}
