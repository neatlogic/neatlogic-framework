package codedriver.framework.filter.core;

import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.JwtVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.transaction.util.TransactionUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.TransactionStatus;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

@DependsOn("loginService")
public abstract class LoginAuthHandlerBase implements ILoginAuthHandler {
    protected Logger logger = LoggerFactory.getLogger(LoginAuthHandlerBase.class);

    public abstract String getType();

    protected static UserMapper userMapper;

    protected static RoleMapper roleMapper;

    @Autowired
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    @Autowired
    public void setRoleMapper(RoleMapper _roleMapper) {
        roleMapper = _roleMapper;
    }

    @Override
    public UserVo auth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = this.getType();
        String tenant = request.getHeader("tenant");
        UserVo userVo = myAuth(request);
        logger.info("loginAuth type: " + type);
        if (userVo != null) {
            logger.info("get userUuId: " + userVo.getUuid());
            logger.info("get userId: " + userVo.getUserId());
        }
        //如果不存在 cookie authorization，则构建jwt,以便下次认证直接走 default 认证
        if (userVo != null && StringUtils.isNotBlank(userVo.getUuid()) && StringUtils.isBlank(userVo.getCookieAuthorization())) {
            JwtVo jwtVo = buildJwt(userVo);
            setResponseAuthCookie(response, request, tenant, jwtVo);
            userVo.setRoleUuidList(roleMapper.getRoleUuidListByUserUuid(userVo.getUuid()));
            TransactionStatus transactionStatus = TransactionUtil.openTx();
            if (userMapper.getUserSessionLockByUserUuid(userVo.getUuid()) != null) {
                userMapper.updateUserSession(userVo.getUuid());
            } else {
                userMapper.insertUserSession(userVo.getUuid());
            }
            TransactionUtil.commitTx(transactionStatus);

        }
        return userVo;
    }

    public abstract UserVo myAuth(HttpServletRequest request) throws ServletException, IOException;

    /**
     * 生成jwt对象
     *
     * @param checkUserVo 用户
     * @return jwt对象
     * @throws Exception 异常
     */
    public static JwtVo buildJwt(UserVo checkUserVo) throws Exception {
        JSONObject jwtHeadObj = new JSONObject();
        jwtHeadObj.put("alg", "HS256");
        jwtHeadObj.put("typ", "JWT");

        JSONObject jwtBodyObj = new JSONObject();
        jwtBodyObj.put("useruuid", checkUserVo.getUuid());
        jwtBodyObj.put("userid", checkUserVo.getUserId());
        jwtBodyObj.put("username", checkUserVo.getUserName());
        jwtBodyObj.put("tenant", checkUserVo.getTenant());
        if (CollectionUtils.isNotEmpty(checkUserVo.getRoleUuidList())) {
            jwtBodyObj.put("rolelist", checkUserVo.getRoleUuidList());
        }

        String jwthead = Base64.getUrlEncoder().encodeToString(jwtHeadObj.toJSONString().getBytes());
        String jwtbody = Base64.getUrlEncoder().encodeToString(jwtBodyObj.toJSONString().getBytes());

        SecretKeySpec signingKey = new SecretKeySpec(Config.JWT_SECRET().getBytes(), "HmacSHA1");
        Mac mac;

        mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal((jwthead + "." + jwtbody).getBytes());
        String jwtsign = Base64.getUrlEncoder().encodeToString(rawHmac);

        // 压缩cookie内容
        String c = "Bearer_" + jwthead + "." + jwtbody + "." + jwtsign;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos);
        gzipOutputStream.write(c.getBytes());
        gzipOutputStream.close();
        String cc = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        JwtVo jwtVo = new JwtVo();
        jwtVo.setCc(cc);
        jwtVo.setJwthead(jwthead);
        jwtVo.setJwtbody(jwtbody);
        jwtVo.setJwtsign(jwtsign);
        return jwtVo;
    }

    /**
     * 设置登录cookie
     *
     * @param response 响应
     * @param request  请求
     * @param tenant   租户
     * @param jwtVo    jwt对象
     */
    public static void setResponseAuthCookie(HttpServletResponse response, HttpServletRequest request, String tenant, JwtVo jwtVo) {
        Cookie authCookie = new Cookie("codedriver_authorization", "GZIP_" + jwtVo.getCc());
        authCookie.setPath("/" + tenant);
        String domainName = request.getServerName();
        if (StringUtils.isNotBlank(domainName)) {
            String[] ds = domainName.split("\\.");
            int len = ds.length;
            if (len > 2 && !StringUtils.isNumeric(ds[len - 1])) {
                authCookie.setDomain(ds[len - 2] + "." + ds[len - 1]);
            }
        }
        Cookie tenantCookie = new Cookie("codedriver_tenant", tenant);
        tenantCookie.setPath("/" + tenant);
        response.addCookie(authCookie);
        response.addCookie(tenantCookie);
        // 允许跨域携带cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType(Config.RESPONSE_TYPE_JSON);
    }
}
