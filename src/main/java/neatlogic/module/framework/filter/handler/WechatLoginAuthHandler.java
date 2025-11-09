/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.filter.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dao.mapper.NotifyConfigMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.WechatVo;
import neatlogic.framework.exception.wechat.WechatAuthenticationInformationNotFoundException;
import neatlogic.framework.exception.wechat.WechatGetAccessTokenFailedException;
import neatlogic.framework.exception.wechat.WechatGetCodeFailedException;
import neatlogic.framework.exception.wechat.WechatGetUserIdFailedException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.notify.core.NotifyHandlerType;
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
    private NotifyConfigMapper notifyConfigMapper;

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
            String config = notifyConfigMapper.getConfigByType(NotifyHandlerType.WECHAT.getValue());
            if (StringUtils.isBlank(config)) {
                throw new WechatAuthenticationInformationNotFoundException();
            }
            WechatVo wechatVo = JSONObject.parseObject(config, WechatVo.class);
            access_token = WechatUtil.getAccessToken(wechatVo.getCorpId(), wechatVo.getCorpSecret()).getToken();
            logger.info("---> access_token:" + access_token);
            if (StringUtils.isNotBlank(access_token)) {
                userId = WechatUtil.getUserID(access_token, code, wechatVo.getAgentId());
                logger.info("---> get user from wechat api result : " + userId);
                if (StringUtils.isNotBlank(userId)) {
                    return userMapper.getUserByUserId(userId);
                } else {
                    throw new WechatGetUserIdFailedException(String.format("wechat get userId is %s ; code : %s   access_token : %s  appId : %s", userId, code, access_token, wechatVo.getAgentId()));
                }
            } else {
                throw new WechatGetAccessTokenFailedException("access_token could not be null!");
            }
        } else {
            throw new WechatGetCodeFailedException(String.format("code could not be null!  please check source url : %s", request.getRequestURL()));
        }
    }

}
