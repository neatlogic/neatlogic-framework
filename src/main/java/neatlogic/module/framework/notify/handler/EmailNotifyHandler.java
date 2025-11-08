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

package neatlogic.module.framework.notify.handler;

import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.common.util.StringUtil;
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

@Component
public class EmailNotifyHandler extends NotifyHandlerBase {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotifyHandler.class);

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
            if (e instanceof NotifyNoReceiverException) {
                logger.warn(e.getMessage(), e);
            } else {
                logger.error(e.getMessage(), e);
            }
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
//        if (CollectionUtils.isNotEmpty(notifyVo.getToUserUuidList())) {
//            List<UserVo> userVoList = userMapper.getUserByUserUuidList(notifyVo.getToUserUuidList());
//            toUserSet.addAll(userVoList);
//        }
//        if (CollectionUtils.isNotEmpty(notifyVo.getToTeamUuidList())) {
//            for (String teamId : notifyVo.getToTeamUuidList()) {
//                List<UserVo> userVoList = userMapper.getActiveUserByTeamId(teamId);
//                toUserSet.addAll(userVoList);
//            }
//        }
//        if (CollectionUtils.isNotEmpty(notifyVo.getToRoleUuidList())) {
//            for (String roleUuid : notifyVo.getToRoleUuidList()) {
//                List<UserVo> userVoList = userService.getUserListByRoleUuid(roleUuid);
//                toUserSet.addAll(userVoList);
//            }
//        }
        List<String> userUuidList = userService.getUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidList(notifyVo.getToUserUuidList(), notifyVo.getToTeamUuidList(), notifyVo.getToRoleUuidList());
        if (CollectionUtils.isNotEmpty(userUuidList)) {
            List<UserVo> userVoList = userMapper.getUserByUserUuidList(userUuidList);
            toUserSet.addAll(userVoList);
        }
        if (CollectionUtils.isEmpty(toUserSet)) {
            throw new NotifyNoReceiverException();
        }
        Set<String> toEmailSet = new HashSet<>();
        for (UserVo user : toUserSet) {
            if (StringUtils.isNotBlank(user.getEmail())) {
                toEmailSet.add(user.getEmail());
            } else {
                logger.warn("接收对象用户：”{}({})”没有设置邮箱地址", user.getUserName(), user.getUserId());
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
                new ArrayList<>(toEmailSet),
                null,
                attachmentMap
        );
    }

    private String clearStringHTML(String sourceContent) {
        return StringUtil.removeHtml(sourceContent);
    }
}
