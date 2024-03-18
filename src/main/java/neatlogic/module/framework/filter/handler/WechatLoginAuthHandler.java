/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
