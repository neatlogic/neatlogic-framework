/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.notify.handler;

import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.common.util.FileUtil;
import codedriver.framework.dao.mapper.MailServerMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.MailServerVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.file.dto.FileVo;
import codedriver.framework.notify.core.NotifyHandlerBase;
import codedriver.framework.notify.core.NotifyHandlerType;
import codedriver.framework.notify.dto.NotifyVo;
import codedriver.framework.notify.exception.EmailServerNotFoundException;
import codedriver.framework.notify.exception.NotifyNoReceiverException;
import codedriver.framework.service.UserService;
import codedriver.module.framework.notify.exception.ExceptionNotifyThread;
import codedriver.module.framework.notify.exception.ExceptionNotifyTriggerType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @program: codedriver
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

    @Resource
    private MailServerMapper mailServerMapper;

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
        MailServerVo mailServerVo = mailServerMapper.getActiveMailServer();
        if (mailServerVo == null || StringUtils.isBlank(mailServerVo.getHost()) || mailServerVo.getPort() == null) {
            throw new EmailServerNotFoundException();
        }

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
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", toEmailSet), false));
        /* 设置邮件标题 */
        msg.setSubject(clearStringHTML(notifyVo.getTitle()));
        msg.setSentDate(new Date());

        MimeMultipart multipart = new MimeMultipart();
        /* 设置邮件正文 */
        String sb = "<html>" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                "<style type=\"text/css\">" +
                "</style>" +
                "</head><body>" +
                notifyVo.getContent() +
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
    }

    private String clearStringHTML(String sourceContent) {
        String content = "";
        if (sourceContent != null) {
            content = sourceContent.replaceAll("</?[^>]+>", "");
        }
        return content;
    }
}
