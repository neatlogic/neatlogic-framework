package neatlogic.framework.util;

import neatlogic.framework.common.constvalue.MimeType;
import neatlogic.framework.dao.mapper.MailServerMapper;
import neatlogic.framework.dto.MailServerVo;
import neatlogic.framework.notify.exception.EmailServerNotFoundException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Component
public class EmailUtil {

    private static MailServerMapper mailServerMapper;

    @Autowired
    public void setMailServerMapper(MailServerMapper _mailServerMapper) {
        mailServerMapper = _mailServerMapper;
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
        MailServerVo mailServerVo = mailServerMapper.getActiveMailServer();
        if (mailServerVo != null && StringUtils.isNotBlank(mailServerVo.getHost()) && mailServerVo.getPort() != null) {
            /** 开启邮箱服务器连接会话 */
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
            if (StringUtils.isNotBlank(mailServerVo.getFromAddress())) {
                msg.setFrom(new InternetAddress(mailServerVo.getFromAddress(), mailServerVo.getName()));
            }
            /** 设置收件人 */
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            /** 设置抄送人 */
            if (StringUtils.isNotBlank(cc)) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
            }
            /** 设置邮件标题 */
            msg.setSubject(title);
            msg.setSentDate(new Date());

            MimeMultipart multipart = new MimeMultipart();
            /** 设置邮件正文 */
            if (StringUtils.isNotBlank(content)) {
                MimeBodyPart text = new MimeBodyPart();
                text.setContent(content, "text/html;charset=UTF-8");
                multipart.addBodyPart(text);
            }
            /** 设置附件 */
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
            /** 发送邮件 */
            Transport.send(msg);
        } else {
            throw new EmailServerNotFoundException();
        }
    }

    public static void sendHtmlEmail(String title, String content, String to, String cc) throws MessagingException, IOException {
        sendEmailWithFile(title, content, to, cc, null);
    }
}
