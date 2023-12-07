package neatlogic.framework.dto;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.Md5Util;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

public class JwtVo {
    private String cc;
    private String jwthead;
    private String jwtbody;
    private String jwtsign;
    @EntityField(name = "创建时间", type = ApiParamType.STRING)
    private Long tokenCreateTime;

    @EntityField(name = "token哈希", type = ApiParamType.STRING)
    private String tokenHash;

    public JwtVo() {

    }

    public JwtVo(UserVo checkUserVo) {
        JSONObject jwtBodyObj = new JSONObject();
        jwtBodyObj.put("useruuid", checkUserVo.getUuid());
        jwtBodyObj.put("userid", checkUserVo.getUserId());
        jwtBodyObj.put("username", checkUserVo.getUserName());
        jwtBodyObj.put("tenant", checkUserVo.getTenant());
        jwtBodyObj.put("isSuperAdmin", checkUserVo.getIsSuperAdmin());
        jwtBodyObj.put("createTime", tokenCreateTime);
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
        return Md5Util.encryptMD5(getJwthead() + "." + getJwtbody() + "." + getJwtsign());
    }
}
