package codedriver.framework.notify.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.notify.dto.NotifyReceiverVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.dao.mapper.MailServerMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.MailServerVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.notify.dto.NotifyVo;
import codedriver.framework.notify.exception.EmailServerNotFoundException;
import codedriver.framework.notify.exception.NotifyNoReceiverException;

public abstract class NotifyHandlerBase implements INotifyHandler {

    private static Logger logger = LoggerFactory.getLogger(NotifyHandlerBase.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailServerMapper mailServerMapper;


    public final void execute(NotifyVo notifyVo) {

        if (StringUtils.isNotBlank(notifyVo.getError())) {
            logger.error(notifyVo.getError());
            sendEmail(notifyVo, false);
        } else {
            myExecute(notifyVo);
        }

    }

    protected abstract void myExecute(NotifyVo notifyVo);

    protected void sendEmail(NotifyVo notifyVo, boolean isNormal) {
        Set<UserVo> toUserSet = new HashSet<>();
        if (isNormal) {
            for (NotifyReceiverVo notifyReceiverVo : notifyVo.getNotifyReceiverVoList()) {
                if (GroupSearch.USER.getValue().equals(notifyReceiverVo.getType())) {
                    UserVo userVo = userMapper.getUserBaseInfoByUuid(notifyReceiverVo.getUuid());
                    if (userVo != null) {
                        toUserSet.add(userVo);
                    }
                } else if (GroupSearch.TEAM.getValue().equals(notifyReceiverVo.getType())) {
                    List<UserVo> userVoList = userMapper.getActiveUserByTeamId(notifyReceiverVo.getUuid());
                    for (UserVo userVo : userVoList) {
                        toUserSet.add(userVo);
                    }
                } else if (GroupSearch.ROLE.getValue().equals(notifyReceiverVo.getType())) {
                    List<UserVo> userVoList = userMapper.getActiveUserByRoleUuid(notifyReceiverVo.getUuid());
                    for (UserVo userVo : userVoList) {
                        toUserSet.add(userVo);
                    }
                }
            }
        } else {
            for (String userUuid : notifyVo.getExceptionNotifyUserUuidList()) {
                UserVo userVo = userMapper.getUserBaseInfoByUuid(userUuid);
                if (userVo != null) {
                    toUserSet.add(userVo);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(toUserSet)) {
            if (StringUtils.isNotBlank(notifyVo.getFromUser())) {
                UserVo userVo = userMapper.getUserBaseInfoByUuid(notifyVo.getFromUser());
                if (userVo != null && StringUtils.isNotBlank(userVo.getEmail())) {
                    notifyVo.setFromUserEmail(userVo.getEmail());
                }
            }
            try {
                MailServerVo mailServerVo = mailServerMapper.getActiveMailServer();
                if (mailServerVo != null && StringUtils.isNotBlank(mailServerVo.getHost()) && mailServerVo.getPort() != null) {
                    HtmlEmail se = new HtmlEmail();
                    se.setHostName(mailServerVo.getHost());
                    se.setSmtpPort(mailServerVo.getPort());
                    if (StringUtils.isNotBlank(mailServerVo.getUserName()) && StringUtils.isNotBlank(mailServerVo.getPassword())) {
                        se.setAuthentication(mailServerVo.getUserName(), mailServerVo.getPassword());
                    }
                    if (StringUtils.isNotBlank(notifyVo.getFromUserEmail())) {
                        se.setFrom(notifyVo.getFromUserEmail(), notifyVo.getFromUser());
                    } else {
                        if (StringUtils.isNotBlank(mailServerVo.getFromAddress())) {
                            se.setFrom(mailServerVo.getFromAddress(), mailServerVo.getName());
                        }
                    }

                    se.setSubject(isNormal ? clearStringHTML(notifyVo.getTitle()) : "通知发送异常");
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html>");
                    sb.append("<head>");
                    sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
                    sb.append("<style type=\"text/css\">");
                    sb.append("</style>");
                    sb.append("</head><body>");
                    sb.append(isNormal ? notifyVo.getContent() : notifyVo.getError());
                    sb.append("</body></html>");
                    se.addPart(sb.toString(), "text/html;charset=utf-8");
                    boolean isSend = false;
                    for (UserVo user : toUserSet) {
                        if (StringUtils.isNotBlank(user.getEmail())) {
                            isSend = true;
                            se.addTo(user.getEmail());
                        }
                    }
                    if (isSend) {
                        mailServerMapper.insertMailHistory(notifyVo);
                        se.send();
                    } else {
                        throw new NotifyNoReceiverException();
                    }
                } else {
                    throw new EmailServerNotFoundException();
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                mailServerMapper.updateMailHistoryStatusAndFailureReasonById(notifyVo.getId(), ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    private String clearStringHTML(String sourceContent) {
        String content = "";
        if (sourceContent != null) {
            content = sourceContent.replaceAll("</?[^>]+>", "");
        }
        return content;
    }
}
