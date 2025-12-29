package neatlogic.framework.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.config.FrameworkTenantConfig;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.Md5Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class JwtVo implements Serializable {
    private String cc;
    private String jwthead;
    private String jwtbody;
    private String jwtsign;
    @EntityField(name = "创建时间", type = ApiParamType.STRING)
    private Long tokenCreateTime;

    @EntityField(name = "token", type = ApiParamType.STRING)
    private String token;

    @EntityField(name = "token哈希", type = ApiParamType.STRING)
    private String tokenHash;

    @EntityField(name = "登录认证 请求headers", type = ApiParamType.STRING)
    JSONObject headers = new JSONObject();

    public JwtVo() {

    }

    private JSONObject getJwtBodyObj(UserVo checkUserVo, Long tokenCreateTime, AuthenticationInfoVo authenticationInfoVo) {
        JSONObject jwtBodyObj = new JSONObject();
        jwtBodyObj.put("useruuid", checkUserVo.getUuid());
        jwtBodyObj.put("userid", checkUserVo.getUserId());
        jwtBodyObj.put("username", checkUserVo.getUserName());
        jwtBodyObj.put("tenant", TenantContext.get() != null ? TenantContext.get().getTenantUuid() : null);
        jwtBodyObj.put("isSuperAdmin", checkUserVo.getIsSuperAdmin());
        jwtBodyObj.put("createTime", tokenCreateTime);
        if (RequestContext.get() != null && RequestContext.get().getRequest() != null) {
            JSONObject headers = new JSONObject();
            //补充设备header,防止移动端和pc端session互相影响
            String deviceHeader = RequestContext.get().getRequest().getHeader("Device");
            if (StringUtils.isNotBlank(deviceHeader)) {
                headers.put("Device", deviceHeader);
            }
            if (CollectionUtils.isNotEmpty(authenticationInfoVo.getHeaderSet())) {
                for (String header : authenticationInfoVo.getHeaderSet()) {
                    String value = RequestContext.get().getRequest().getHeader(header);
                    if (value != null) {
                        headers.put(header, value);
                    }
                }
            }
            if (MapUtils.isNotEmpty(headers)) {
                jwtBodyObj.put("headers", headers.toString());
            }
            this.headers = headers;
        }
        this.setTokenCreateTime(tokenCreateTime);

        return jwtBodyObj;
    }


    public JwtVo(UserVo checkUserVo, Long tokenCreateTime, AuthenticationInfoVo authenticationInfoVo) {
        JSONObject jwtBodyObj = getJwtBodyObj(checkUserVo, tokenCreateTime, authenticationInfoVo);
        jwtbody = Base64.getUrlEncoder().encodeToString(jwtBodyObj.toJSONString().getBytes());
    }

    public JwtVo(UserVo checkUserVo, Long tokenCreateTime, AuthenticationInfoVo authenticationInfoVo, String authType) {
        JSONObject jwtBodyObj = getJwtBodyObj(checkUserVo, tokenCreateTime, authenticationInfoVo);
        String needPwdExpiredCheck = ConfigManager.getConfig(FrameworkTenantConfig.PASSWORD_NEED_EXPIRED_CHECK);
        if (Objects.equals(needPwdExpiredCheck, "1") && StringUtils.isNotBlank(authType)) {
            ILoginAuthHandler loginAuth = LoginAuthFactory.getLoginAuth(authType);
            if (loginAuth != null) {
                if (loginAuth.checkPwdExpired(checkUserVo)) {
                    jwtBodyObj.put("pwdExpired", true);
                }
            }
        }

        jwtbody = Base64.getUrlEncoder().encodeToString(jwtBodyObj.toJSONString().getBytes());
    }

    public JwtVo(String[] jwtParts) {
        this.jwthead = jwtParts[0];
        this.jwtbody = jwtParts[1];
        this.jwtsign = jwtParts[2];
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getJwthead() {
        if (StringUtils.isBlank(jwthead)) {
            JSONObject jwtHeadObj = new JSONObject();
            jwtHeadObj.put("alg", "HS256");
            jwtHeadObj.put("typ", "JWT");
            jwthead = Base64.getUrlEncoder().encodeToString(jwtHeadObj.toJSONString().getBytes());
        }
        return jwthead;
    }

    public String getJwtbody() {
        return jwtbody;
    }

    public void setJwthead(String jwthead) {
        this.jwthead = jwthead;
    }

    public void setJwtbody(String jwtbody) {
        this.jwtbody = jwtbody;
    }

    public String getJwtsign() {
        return jwtsign;
    }

    public void setJwtsign(String jwtsign) {
        this.jwtsign = jwtsign;
    }

    public Long getTokenCreateTime() {
        return tokenCreateTime;
    }

    public void setTokenCreateTime(Long tokenCreateTime) {
        this.tokenCreateTime = tokenCreateTime;
    }

    public String getTokenHash() {
        if (StringUtils.isBlank(tokenHash)) {
            tokenHash = Md5Util.encryptMD5(getToken());
        }
        return tokenHash;
    }

    public String getToken() {
        if (StringUtils.isBlank(token)) {
            String jwtBody = new String(Base64.getUrlDecoder().decode(getJwtbody()), StandardCharsets.UTF_8);
            JSONObject jwtBodyObj = JSON.parseObject(jwtBody);
            JSONObject tokenJson = new JSONObject();
            tokenJson.put("tenant", jwtBodyObj.getString("tenant"));
            tokenJson.put("useruuid", jwtBodyObj.getString("useruuid"));
            if (jwtBodyObj.containsKey("headers")) {
                tokenJson.put("headers", jwtBodyObj.getString("headers"));
            }
            token = tokenJson.toJSONString();
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isNotValidTokenCreateTime() {
        return Config.ENABLE_NO_SECRET() || Config.ENABLE_CONCURRENT_SESSION();

    }

    public boolean validTokenCreateTime(Long userSessionTokenCreateTime) {
        if (isNotValidTokenCreateTime()) {
            return true;
        }
        return Objects.equals(userSessionTokenCreateTime, getTokenCreateTime());
    }


    public JSONObject getHeaders() {
        if (MapUtils.isEmpty(headers) && StringUtils.isNotBlank(jwtbody)) {
            String jwtBody = new String(Base64.getUrlDecoder().decode(jwtbody), StandardCharsets.UTF_8);
            JSONObject jwtBodyObj = JSONObject.parseObject(jwtBody);
            return jwtBodyObj.getJSONObject("headers");
        }
        return headers;
    }
}
