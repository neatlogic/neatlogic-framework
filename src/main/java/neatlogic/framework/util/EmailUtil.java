package neatlogic.framework.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.common.constvalue.MimeType;
import neatlogic.framework.dao.mapper.NotifyConfigMapper;
import neatlogic.framework.dto.MailServerVo;
import neatlogic.framework.notify.core.NotifyHandlerType;
import neatlogic.framework.notify.exception.EmailSendException;
import neatlogic.framework.notify.exception.EmailServerNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class EmailUtil {
    static Logger logger = LoggerFactory.getLogger(EmailUtil.class);
    private static NotifyConfigMapper notifyConfigMapper;

    @Autowired
    public void setNotifyConfigMapper(NotifyConfigMapper _notifyConfigMapper) {
        notifyConfigMapper = _notifyConfigMapper;
    }

    /**
     * 发送带附件的邮件
     *
     * @param title   邮件标题
     * @param content 邮件正文
     * @param to      收件人
     */
    public static void sendEmailWithFile(String title, String content, String to) throws MessagingException, IOException {
        sendEmailWithFile(title, content, to, null, null);
    }

    /**
     * 发送带附件的邮件
     *
     * @param title   邮件标题
     * @param content 邮件正文
     * @param to      收件人
     */
    public static void sendEmailWithFileWithMailServer(String title, String content, String to, MailServerVo mailServerVo) throws MessagingException, IOException {
        sendEmailWithFile(title, content, to, null, null, mailServerVo);
    }


    /**
     * 发送带附件的邮件
     *
     * @param title         邮件标题
     * @param content       邮件正文
     * @param to            收件人
     * @param cc            抄送人
     * @param attachmentMap 附件(key:文件名;value:流)，根据附件名后缀自动匹配MimeType，如果没有后缀名或未匹配到MimeType，则默认为application/octet-stream
     */
    public static void sendEmailWithFile(String title, String content, String to, String cc, Map<String, InputStream> attachmentMap) throws MessagingException, IOException {
        String config = notifyConfigMapper.getConfigByType(NotifyHandlerType.EMAIL.getValue());
        if (StringUtils.isBlank(config)) {
            throw new EmailServerNotFoundException();
        }
        MailServerVo mailServerVo = JSON.parseObject(config, MailServerVo.class);
        sendEmailWithFile(title, content, to, cc, attachmentMap, mailServerVo);
    }

    public static void sendEmailWithFile(String title, String content, List<String> to, List<String> cc, Map<String, InputStream> attachmentMap, MailServerVo mailServerVo) throws IOException, MessagingException {
        if (mailServerVo != null && StringUtils.isNotBlank(mailServerVo.getHost()) && mailServerVo.getPort() != null) {
            /* 开启邮箱服务器连接会话 */
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", mailServerVo.getHost());
            props.setProperty("mail.smtp.port", mailServerVo.getPort().toString());
            props.put("mail.smtp.ssl.enable", mailServerVo.getSslEnable());
            props.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailServerVo.getUserName(), mailServerVo.getPassword());
                }
            });

            MimeMessage msg = new MimeMessage(session);
            if (StringUtils.isNotBlank(mailServerVo.getFromAddress())) {
                msg.setFrom(new InternetAddress(mailServerVo.getFromAddress(), mailServerVo.getName()));
            }
            /* 设置收件人 */
            if (CollectionUtils.isNotEmpty(to)) {
                List<Address> addresses = new ArrayList<>();
                for (String toAddress : to) {
                    addresses.add(new InternetAddress(toAddress, false));
                }
                msg.setRecipients(Message.RecipientType.TO, addresses.toArray(new Address[0]));
            }

            /* 设置抄送人 */
            if (CollectionUtils.isNotEmpty(cc)) {
                List<Address> addresses = new ArrayList<>();
                for (String ccAddress : cc) {
                    addresses.add(new InternetAddress(ccAddress, false));
                }
                msg.setRecipients(Message.RecipientType.CC, addresses.toArray(new Address[0]));
            }
            /* 设置邮件标题 */
            msg.setSubject(title);
            msg.setSentDate(new Date());

            MimeMultipart multipart = new MimeMultipart();
            /* 设置邮件正文 */
            //if (StringUtils.isNotBlank(content)) { //注释掉，否则content 为空时发邮件会异常
            content = "<html>" +
                    "<head>" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                    "<style type=\"text/css\">" +
                    "</style>" +
                    "</head><body>" +
                    content +
                    "</body></html>";
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(content, "text/html;charset=UTF-8");
            multipart.addBodyPart(text);
            //}
            /* 设置附件 */
            if (MapUtils.isNotEmpty(attachmentMap)) {
                for (Map.Entry<String, InputStream> entry : attachmentMap.entrySet()) {
                    MimeType mimeType = null;
                    String key = entry.getKey();
                    if (key.contains(".")) {
                        mimeType = MimeType.getMimeType(key.substring(key.lastIndexOf(".")));
                    }
                    if (mimeType == null) {
                        mimeType = MimeType.STREAM;
                    }
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    DataSource dataSource = new ByteArrayDataSource(entry.getValue(), mimeType.getValue());
                    DataHandler dataHandler = new DataHandler(dataSource);
                    messageBodyPart.setDataHandler(dataHandler);
                    messageBodyPart.setFileName(MimeUtility.encodeText(key));
                    multipart.addBodyPart(messageBodyPart);
                }
            }
            msg.setContent(multipart);
            /* 发送邮件 */
            try {
                logger.info("邮件会话属性：{}", JSON.toJSONString(props));
                logger.info("邮件服务器身份验证账号：{}", mailServerVo.getUserName());
                logger.info("邮件服务器身份验证密码：{}", mailServerVo.getPassword());
                logger.info("发送方名称：{}", mailServerVo.getName());
                logger.info("发送方地址：{}", mailServerVo.getUserName());
                logger.info("接收方地址：{}", to);
                logger.info("邮件标题：{}", title);
                logger.info("发送日期：{}", new Date());
                logger.info("邮件内容：{}", content);
                logger.info("邮件MimeMessage封装对象：{}", JSON.toJSONString(msg, SerializerFeature.PrettyFormat));
                Transport.send(msg);
                logger.info("邮件发送成功");
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new EmailSendException(ex);
            }
        } else {
            throw new EmailServerNotFoundException();
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param title         邮件标题
     * @param content       邮件正文
     * @param to            收件人
     * @param cc            抄送人
     * @param attachmentMap 附件(key:文件名;value:流)，根据附件名后缀自动匹配MimeType，如果没有后缀名或未匹配到MimeType，则默认为application/octet-stream
     */
    public static void sendEmailWithFile(String title, String content, String to, String cc, Map<String, InputStream> attachmentMap, MailServerVo mailServerVo) throws MessagingException, IOException {
        if (mailServerVo != null && StringUtils.isNotBlank(mailServerVo.getHost()) && mailServerVo.getPort() != null) {
            /* 开启邮箱服务器连接会话 */
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", mailServerVo.getHost());
            props.setProperty("mail.smtp.port", mailServerVo.getPort().toString());
            props.put("mail.smtp.ssl.enable", mailServerVo.getSslEnable());
            props.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailServerVo.getUserName(), mailServerVo.getPassword());
                }
            });

            MimeMessage msg = new MimeMessage(session);
            if (StringUtils.isNotBlank(mailServerVo.getFromAddress())) {
                msg.setFrom(new InternetAddress(mailServerVo.getFromAddress(), mailServerVo.getName()));
            }
            /* 设置收件人 */
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            /* 设置抄送人 */
            if (StringUtils.isNotBlank(cc)) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
            }
            /* 设置邮件标题 */
            msg.setSubject(title);
            msg.setSentDate(new Date());

            MimeMultipart multipart = new MimeMultipart();
            /* 设置邮件正文 */
            //if (StringUtils.isNotBlank(content)) { //注释掉，否则content 为空时发邮件会异常
            content = "<html>" +
                    "<head>" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                    "<style type=\"text/css\">" +
                    "</style>" +
                    "</head><body>" +
                    content +
                    "</body></html>";
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(content, "text/html;charset=UTF-8");
            multipart.addBodyPart(text);
            //}
            /* 设置附件 */
            if (MapUtils.isNotEmpty(attachmentMap)) {
                for (Map.Entry<String, InputStream> entry : attachmentMap.entrySet()) {
                    MimeType mimeType = null;
                    String key = entry.getKey();
                    if (key.contains(".")) {
                        mimeType = MimeType.getMimeType(key.substring(key.lastIndexOf(".")));
                    }
                    if (mimeType == null) {
                        mimeType = MimeType.STREAM;
                    }
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    DataSource dataSource = new ByteArrayDataSource(entry.getValue(), mimeType.getValue());
                    DataHandler dataHandler = new DataHandler(dataSource);
                    messageBodyPart.setDataHandler(dataHandler);
                    messageBodyPart.setFileName(MimeUtility.encodeText(key));
                    multipart.addBodyPart(messageBodyPart);
                }
            }
            msg.setContent(multipart);
            /* 发送邮件 */
            try {
                logger.info("邮件会话属性：{}", JSON.toJSONString(props));
                logger.info("邮件服务器身份验证账号：{}", mailServerVo.getUserName());
                logger.info("邮件服务器身份验证密码：{}", mailServerVo.getPassword());
                logger.info("发送方名称：{}", mailServerVo.getName());
                logger.info("发送方地址：{}", mailServerVo.getUserName());
                logger.info("接收方地址：{}", to);
                logger.info("邮件标题：{}", title);
                logger.info("发送日期：{}", new Date());
                logger.info("邮件内容：{}", content);
                logger.info("邮件MimeMessage封装对象：{}", JSON.toJSONString(msg, SerializerFeature.PrettyFormat));
                Transport.send(msg);
                logger.info("邮件发送成功");
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new EmailSendException(ex);
            }
        } else {
            throw new EmailServerNotFoundException();
        }
    }

    public static void sendEmailWithFile(String title, String content, List<String> to, List<String> cc, Map<String, InputStream> attachmentMap) throws MessagingException, IOException {
        String config = notifyConfigMapper.getConfigByType(NotifyHandlerType.EMAIL.getValue());
        if (StringUtils.isBlank(config)) {
            throw new EmailServerNotFoundException();
        }
        MailServerVo mailServerVo = JSON.parseObject(config, MailServerVo.class);
        sendEmailWithFile(title, content, to, cc, attachmentMap, mailServerVo);
    }

    public static void sendHtmlEmail(String title, String content, List<String> to, List<String> cc) throws MessagingException, IOException {
        sendEmailWithFile(title, content, to, cc, null);
    }

    public static void sendHtmlEmail(String title, String content, String to, String cc) throws MessagingException, IOException {
        sendEmailWithFile(title, content, to, cc, null);
    }
}
