/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.module.framework.notify.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dao.mapper.WechatMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.WechatVo;
import neatlogic.framework.notify.core.NotifyHandlerBase;
import neatlogic.framework.notify.core.NotifyHandlerType;
import neatlogic.framework.notify.dto.NotifyVo;
import neatlogic.framework.notify.exception.NotifyNoReceiverException;
import neatlogic.framework.service.UserService;
import neatlogic.framework.util.WechatUtil;
import neatlogic.module.framework.notify.exception.ExceptionNotifyThread;
import neatlogic.module.framework.notify.exception.ExceptionNotifyTriggerType;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: neatlogic
 * @description:
 * @create: 2023-04-20 17:34
 **/
@Component
public class WechatNotifyHandler extends NotifyHandlerBase {

    private static Logger logger = LoggerFactory.getLogger(WechatNotifyHandler.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private WechatMapper wechatMapper;

    @Override
    public String getName() {
        return NotifyHandlerType.WECHAT.getText();
    }

    @Override
    public String getType() {
        return NotifyHandlerType.WECHAT.getValue();
    }

    @Override
    public boolean myExecute(NotifyVo notifyVo) {
        try {
            sendWechat(notifyVo);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (notifyVo.getIsSendExceptionNotify() == 1) {
                notifyVo.setIsSendExceptionNotify(0);// 防止循环调用NotifyPolicyUtil.execute方法
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer, true));
                notifyVo.appendError(writer.toString().replaceAll("\r\n\t", "<br>&nbsp;&nbsp;&nbsp;&nbsp;"));
                CachedThreadPool.execute(new ExceptionNotifyThread(notifyVo, ExceptionNotifyTriggerType.WECHATNOTIFYEXCEPTION));
            }
            return false;
        }
    }

    private void sendWechat(NotifyVo notifyVo) {
        Set<UserVo> toUserSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(notifyVo.getToUserUuidList())) {
            List<UserVo> userVoList = userMapper.getUserByUserUuidList(notifyVo.getToUserUuidList());
            toUserSet.addAll(userVoList);
        }
        if (CollectionUtils.isNotEmpty(notifyVo.getToTeamUuidList())) {
            for (String teamId : notifyVo.getToTeamUuidList()) {
                List<UserVo> userVoList = userMapper.getActiveUserByTeamId(teamId);
                toUserSet.addAll(userVoList);
            }
        }
        if (CollectionUtils.isNotEmpty(notifyVo.getToRoleUuidList())) {
            for (String roleUuid : notifyVo.getToRoleUuidList()) {
                List<UserVo> userVoList = userService.getUserListByRoleUuid(roleUuid);
                toUserSet.addAll(userVoList);
            }
        }
        if (CollectionUtils.isEmpty(toUserSet)) {
            throw new NotifyNoReceiverException();
        }

        String content = notifyVo.getContent();
        String toUser = "";
        WechatVo wechatVo = wechatMapper.getWechat();
        WechatUtil.AccessToken accessToken = WechatUtil.getAccessToken(wechatVo.getCorpId(), wechatVo.getCorpSecret());
        int index = 0 ;
        for (UserVo user : toUserSet) {
            if (index == toUserSet.size() - 1) {
                toUser = toUser + user.getUserId();
            } else {
                toUser = toUser + user.getUserId() + "|";
            }
            index ++ ;
        }
        JSONObject data = WechatUtil.getTextCardMsg(toUser , notifyVo.getTitle() , content , wechatVo.getCorpId());
        WechatUtil.sendMessage(accessToken.getToken(), data, wechatVo.getAgentId());
    }

}
