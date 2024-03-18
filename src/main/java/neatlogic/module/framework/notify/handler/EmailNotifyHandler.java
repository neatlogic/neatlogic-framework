/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.notify.handler;

import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.notify.core.NotifyHandlerBase;
import neatlogic.framework.notify.core.NotifyHandlerType;
import neatlogic.framework.notify.dto.NotifyVo;
import neatlogic.framework.notify.exception.EmailServerNotFoundException;
import neatlogic.framework.notify.exception.NotifyNoReceiverException;
import neatlogic.framework.service.UserService;
import neatlogic.framework.util.EmailUtil;
import neatlogic.module.framework.notify.exception.ExceptionNotifyThread;
import neatlogic.module.framework.notify.exception.ExceptionNotifyTriggerType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @program: neatlogic
 * @description:
 * @create: 2019-12-09 15:34
 **/
@Component
public class EmailNotifyHandler extends NotifyHandlerBase {

    private static Logger logger = LoggerFactory.getLogger(EmailNotifyHandler.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Override
    public boolean myExecute(NotifyVo notifyVo) {
        try {
            sendEmail(notifyVo);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (!(e instanceof EmailServerNotFoundException)) {
                if (notifyVo.getIsSendExceptionNotify() == 1) {
                    notifyVo.setIsSendExceptionNotify(0);// 防止循环调用NotifyPolicyUtil.execute方法
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer, true));
                    notifyVo.appendError(writer.toString().replaceAll("\r\n\t", "<br>&nbsp;&nbsp;&nbsp;&nbsp;"));
                    CachedThreadPool.execute(new ExceptionNotifyThread(notifyVo, ExceptionNotifyTriggerType.EMAILNOTIFYEXCEPTION));
                }
            }
            return false;
        }
    }

    @Override
    public String getName() {
        return NotifyHandlerType.EMAIL.getText();
    }


    @Override
    public String getType() {
        return NotifyHandlerType.EMAIL.getValue();
    }

    private void sendEmail(NotifyVo notifyVo) throws Exception {
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
        Set<String> toEmailSet = new HashSet<>();
        for (UserVo user : toUserSet) {
            if (StringUtils.isNotBlank(user.getEmail())) {
                toEmailSet.add(user.getEmail());
            } else {
                logger.error("接收对象用户：”" + user.getUserName() + "(" + user.getUserId() + ")”没有设置邮箱地址");
            }
        }
        if (CollectionUtils.isEmpty(toEmailSet)) {
            throw new NotifyNoReceiverException();
        }
        notifyVo.setActualRecipientList(new ArrayList<>(toEmailSet));
        if (StringUtils.isNotBlank(notifyVo.getFromUser())) {
            UserVo userVo = userMapper.getUserBaseInfoByUuid(notifyVo.getFromUser());
            if (userVo != null && StringUtils.isNotBlank(userVo.getEmail())) {
                notifyVo.setFromUserEmail(userVo.getEmail());
            }
        }

        Map<String, InputStream> attachmentMap = new HashMap<>();
        List<FileVo> fileList = notifyVo.getFileList();
        if (CollectionUtils.isNotEmpty(fileList)) {
            for (FileVo fileVo : fileList) {
                InputStream stream = FileUtil.getData(fileVo.getPath());
                if (stream != null) {
                    attachmentMap.put(fileVo.getName(), stream);
                }
            }
        }
        EmailUtil.sendEmailWithFile(
                clearStringHTML(notifyVo.getTitle()),
                notifyVo.getContent(),
                String.join(",", toEmailSet),
                null,
                attachmentMap
                );
    }

    private String clearStringHTML(String sourceContent) {
        String content = "";
        if (sourceContent != null) {
            content = sourceContent.replaceAll("</?[^>]+>", "");
        }
        return content;
    }
}
