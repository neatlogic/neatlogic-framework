/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.filter.handler;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.WechatMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.WechatVo;
import neatlogic.framework.exception.wechat.WechatGetAccessTokenFailedException;
import neatlogic.framework.exception.wechat.WechatGetCodeFailedException;
import neatlogic.framework.exception.wechat.WechatGetUserIdFailedException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.util.WechatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class WechatLoginAuthHandler extends LoginAuthHandlerBase {

    @Resource
    private WechatMapper wechatMapper;

    @Override
    public String getType() {
        return "wechat";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws ServletException, IOException {
        String code = request.getHeader("AuthValue");
        String userId;
        String access_token;
        logger.info("---> code:" + code);
        if (StringUtils.isNotBlank(code) && !"authdeny".equals(code)) {
            WechatVo wechatVo = wechatMapper.getWechat();
            access_token = WechatUtil.getAccessToken(wechatVo.getCorpId(), wechatVo.getCorpSecret()).getToken();
            logger.info("---> access_token:" + access_token);
            if (StringUtils.isNotBlank(access_token)) {
                userId = WechatUtil.getUserID(access_token, code, wechatVo.getAgentId());
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

}
