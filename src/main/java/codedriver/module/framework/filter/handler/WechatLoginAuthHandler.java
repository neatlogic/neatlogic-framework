/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.filter.handler;

import codedriver.framework.common.config.Config;
import codedriver.framework.dto.UserVo;
import codedriver.framework.exception.wechat.WechatGetAccessTokenFailedException;
import codedriver.framework.exception.wechat.WechatGetCodeFailedException;
import codedriver.framework.exception.wechat.WechatGetUserIdFailedException;
import codedriver.framework.filter.core.LoginAuthHandlerBase;
import codedriver.framework.util.WechatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class WechatLoginAuthHandler extends LoginAuthHandlerBase {

    @Override
    public String getType() {
        return "wechat";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws ServletException, IOException {
        String code = request.getHeader("AuthValue");
        logger.info("requestUrl : "+request.getRequestURL() +"    Header time : " + request.getHeader("time"));
        String userId;
        String access_token;
        logger.info("---> code:" + code);
        if (StringUtils.isNotBlank(code) && !"authdeny".equals(code)) {
            access_token = WechatUtil.getAccessToken(Config.WECHAT_CORP_ID(), Config.WECHAT_APP_SECRET()).getToken();
            logger.info("---> access_token:" + access_token);
            if (StringUtils.isNotBlank(access_token)) {
                userId = WechatUtil.getUserID(access_token, code, Config.WECHAT_APP_AGENT_ID());
                logger.info("---> get user from wechat api result : " + userId);
                if (StringUtils.isNotBlank(userId)) {
                    return userMapper.getUserByUserId(userId);
                } else {
                    throw new WechatGetUserIdFailedException(String.format("wechat get userId is %s ; code : %s   access_token : %s  appId : %s", userId, code, access_token, Config.WECHAT_APP_AGENT_ID()));
                }
            } else {
                throw new WechatGetAccessTokenFailedException("access_token could not be null!");
            }
        } else {
            throw new WechatGetCodeFailedException(String.format("code could not be null!  please check source url : %s", request.getRequestURL()));
        }
    }

    @Override
    public String directUrl() {
        // TODO Auto-generated method stub
        return null;
    }

}
