package codedriver.framework.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.config.Config;
import codedriver.framework.dto.JwtVo;
import codedriver.framework.dto.UserVo;

@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public JwtVo buildJwt(UserVo checkUserVo) throws Exception {
        JSONObject jwtHeadObj = new JSONObject();
        jwtHeadObj.put("alg", "HS256");
        jwtHeadObj.put("typ", "JWT");

        JSONObject jwtBodyObj = new JSONObject();
        jwtBodyObj.put("useruuid", checkUserVo.getUuid());
        jwtBodyObj.put("userid", checkUserVo.getUserId());
        jwtBodyObj.put("username", checkUserVo.getUserName());
        jwtBodyObj.put("tenant", checkUserVo.getTenant());
        if (CollectionUtils.isNotEmpty(checkUserVo.getRoleUuidList())) {
            JSONArray roleList = new JSONArray();
            for (String role : checkUserVo.getRoleUuidList()) {
                roleList.add(role);
            }
            jwtBodyObj.put("rolelist", roleList);
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

}
