/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

import codedriver.framework.common.util.FileUtil;
import codedriver.framework.dao.mapper.MailServerMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.MailServerVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.file.dto.FileVo;
import codedriver.framework.notify.dto.NotifyVo;
import codedriver.framework.notify.exception.EmailServerNotFoundException;
import codedriver.framework.notify.exception.NotifyNoReceiverException;
import codedriver.framework.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.InputStream;
import java.util.*;

public abstract class NotifyHandlerBase implements INotifyHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotifyHandlerBase.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private MailServerMapper mailServerMapper;


    public final void execute(NotifyVo notifyVo) throws Exception {

        if (StringUtils.isNotBlank(notifyVo.getError())) {
            logger.error(notifyVo.getError());
            sendEmail(notifyVo, false);
        } else {
            myExecute(notifyVo);
        }

    }

    protected abstract void myExecute(NotifyVo notifyVo) throws Exception;

    protected void sendEmail(NotifyVo notifyVo, boolean isNormal) throws Exception {
        Set<UserVo> toUserSet = new HashSet<>();
        if (isNormal) {
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
//                    List<UserVo> userVoList = userMapper.getActiveUserByRoleUuid(roleUuid);
                    toUserSet.addAll(userVoList);
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

            MailServerVo mailServerVo = mailServerMapper.getActiveMailServer();
            if (mailServerVo != null && StringUtils.isNotBlank(mailServerVo.getHost()) && mailServerVo.getPort() != null) {
                boolean isSend = false;
                List<String> toEmailList = new ArrayList<>();
                for (UserVo user : toUserSet) {
                    if (StringUtils.isNotBlank(user.getEmail())) {
                        isSend = true;
                        toEmailList.add(user.getEmail());
                    }
                }
                if (isSend) {
                    /* 开启邮箱服务器连接会话 */
                    Properties props = new Properties();
                    props.setProperty("mail.smtp.host", mailServerVo.getHost());
                    props.setProperty("mail.smtp.port", mailServerVo.getPort().toString());
                    props.put("mail.smtp.auth", "true");
                    Session session = Session.getInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(mailServerVo.getUserName(), mailServerVo.getPassword());
                        }
                    });

                    MimeMessage msg = new MimeMessage(session);
                    if (StringUtils.isNotBlank(notifyVo.getFromUserEmail())) {
                        msg.setFrom(new InternetAddress(notifyVo.getFromUserEmail(), notifyVo.getFromUser()));
                    } else if (StringUtils.isNotBlank(mailServerVo.getFromAddress())) {
                        msg.setFrom(new InternetAddress(mailServerVo.getFromAddress(), mailServerVo.getName()));
                    }

                    /* 设置收件人 */
                    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", toEmailList), false));
                    /* 设置邮件标题 */
                    msg.setSubject(isNormal ? clearStringHTML(notifyVo.getTitle()) : "通知发送异常");
                    msg.setSentDate(new Date());

                    MimeMultipart multipart = new MimeMultipart();
                    /* 设置邮件正文 */
                    String sb = "<html>" +
                            "<head>" +
                            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                            "<style type=\"text/css\">" +
                            "</style>" +
                            "</head><body>" +
                            (isNormal ? notifyVo.getContent() : notifyVo.getError()) +
                            "</body></html>";
                    MimeBodyPart text = new MimeBodyPart();
                    text.setContent(sb, "text/html;charset=UTF-8");
                    multipart.addBodyPart(text);
                    /* 设置附件 */
                    List<FileVo> fileList = notifyVo.getFileList();
                    if (CollectionUtils.isNotEmpty(fileList)) {
                        for (FileVo vo : fileList) {
                            InputStream stream = FileUtil.getData(vo.getPath());
                            if (stream != null) {
                                MimeBodyPart messageBodyPart = new MimeBodyPart();
                                DataSource dataSource = new ByteArrayDataSource(stream, "application/octet-stream");
                                DataHandler dataHandler = new DataHandler(dataSource);
                                messageBodyPart.setDataHandler(dataHandler);
                                messageBodyPart.setFileName(MimeUtility.encodeText(vo.getName()));
                                multipart.addBodyPart(messageBodyPart);
                            }
                        }
                    }
                    msg.setContent(multipart);
                    /* 发送邮件 */
                    Transport.send(msg);
                } else {
                    throw new NotifyNoReceiverException();
                }
            } else {
                throw new EmailServerNotFoundException();
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
