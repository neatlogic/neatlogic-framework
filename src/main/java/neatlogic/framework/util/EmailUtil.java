package neatlogic.framework.util;

import com.alibaba.fastjson.JSON;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import neatlogic.framework.common.constvalue.MimeType;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.dao.mapper.NotifyConfigMapper;
import neatlogic.framework.dto.MailServerVo;
import neatlogic.framework.dto.NotifyConfigVo;
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
     * 发送带附件的邮件（单个收件人字符串，逗号分隔）
     */
    public static void sendEmailWithFile(String title, String content, String to) throws MessagingException, IOException {
        List<String> toList = parseAddressList(to);
        sendEmailWithFile(title, content, toList, null, null);
    }

    /**
     * 发送带附件的邮件（指定 MailServerVo）
     */
    public static void sendEmailWithFileWithMailServer(String title, String content, String to, MailServerVo mailServerVo) throws MessagingException, IOException {
        List<String> toList = parseAddressList(to);
        sendEmailWithFile(title, content, toList, null, null, mailServerVo);
    }

    /**
     * 发送带附件的邮件（含抄送/附件，配置来自DB）
     */
    public static void sendEmailWithFile(String title, String content, String to, String cc,
                                         Map<String, InputStream> attachmentMap) throws MessagingException, IOException {
        String config = notifyConfigMapper.getConfigByType(NotifyHandlerType.EMAIL.getValue());
        if (StringUtils.isBlank(config)) {
            throw new EmailServerNotFoundException();
        }
        MailServerVo mailServerVo = JSON.parseObject(config, MailServerVo.class);
        List<String> toList = parseAddressList(to);
        List<String> ccList = parseAddressList(cc);
        sendEmailWithFile(title, content, toList, ccList, attachmentMap, mailServerVo);
    }

    /**
     * 核心发邮件方法（Jakarta Mail）
     */
    public static void sendEmailWithFile(String title, String content,
                                         List<String> to,
                                         List<String> cc,
                                         Map<String, InputStream> attachmentMap,
                                         MailServerVo mailServerVo) throws IOException, MessagingException {
        if (mailServerVo == null || StringUtils.isBlank(mailServerVo.getHost()) || mailServerVo.getPort() == null) {
            throw new EmailServerNotFoundException();
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", mailServerVo.getHost());
        props.put("mail.smtp.port", mailServerVo.getPort().toString());
        props.put("mail.smtp.auth", "true");

        if (Boolean.parseBoolean(mailServerVo.getSslEnable())) {
            // SSL (465)
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        } else {
            // STARTTLS (587)
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        }

        Session session;
        if (StringUtils.isNotBlank(mailServerVo.getUserName()) && StringUtils.isNotBlank(mailServerVo.getPassword())) {
            String password = RC4Util.decrypt(mailServerVo.getPassword());
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailServerVo.getUserName(), password);
                }
            });
        } else {
            props.put("mail.smtp.auth", "false");
            session = Session.getInstance(props);
        }

        MimeMessage msg = new MimeMessage(session);
        if (StringUtils.isNotBlank(mailServerVo.getFromAddress())) {
            msg.setFrom(new InternetAddress(mailServerVo.getFromAddress(), mailServerVo.getName()));
        }

        // 收件人
        if (CollectionUtils.isNotEmpty(to)) {
            msg.setRecipients(Message.RecipientType.TO, to.stream()
                    .map(addr -> {
                        try {
                            return new InternetAddress(addr, false);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(Address[]::new));
        }

        // 抄送人
        if (CollectionUtils.isNotEmpty(cc)) {
            msg.setRecipients(Message.RecipientType.CC, cc.stream()
                    .map(addr -> {
                        try {
                            return new InternetAddress(addr, false);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(Address[]::new));
        }

        msg.setSubject(title, "UTF-8");
        msg.setSentDate(new Date());

        // 构建 multipart 内容
        MimeMultipart multipart = new MimeMultipart();

        // 正文
        content = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>"
                + content + "</body></html>";
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(content, "text/html;charset=UTF-8");
        multipart.addBodyPart(text);

        // 附件
        if (MapUtils.isNotEmpty(attachmentMap)) {
            for (Map.Entry<String, InputStream> entry : attachmentMap.entrySet()) {
                String key = entry.getKey();
                MimeType mimeType = key.contains(".")
                        ? MimeType.getMimeType(key.substring(key.lastIndexOf(".")))
                        : MimeType.STREAM;
                if (mimeType == null) {
                    mimeType = MimeType.STREAM;
                }
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                DataSource dataSource = new ByteArrayDataSource(entry.getValue(), mimeType.getValue());
                messageBodyPart.setDataHandler(new DataHandler(dataSource));
                messageBodyPart.setFileName(MimeUtility.encodeText(key));
                multipart.addBodyPart(messageBodyPart);
            }
        }

        msg.setContent(multipart);

        // 发送
        try {
            Transport.send(msg);
            logger.info("邮件发送成功");
        } catch (Exception ex) {
            List<String> list = new ArrayList<>();
            Address[] allRecipients = msg.getAllRecipients();
            if (allRecipients != null) {
                for (Address address : allRecipients) {
                    if (address instanceof InternetAddress) {
                        list.add(((InternetAddress) address).getAddress());
                    }
                }
            }
            String allRecipientStr = String.join(",", list);
            logger.error(ex.getMessage() + "，所有收件人：" + allRecipientStr, ex);
            throw new EmailSendException(ex.getMessage(), allRecipientStr);
        }
    }

    /**
     * 从 notifyConfigId 读取配置发邮件
     */
    public static void sendEmailWithFile(Long notifyId, String title, String content,
                                         List<String> to, List<String> cc,
                                         Map<String, InputStream> attachmentMap) throws MessagingException, IOException {
        String config = null;
        if (notifyId != null) {
            NotifyConfigVo notifyConfigVo = notifyConfigMapper.getNotifyConfigById(notifyId);
            if (notifyConfigVo != null) {
                config = notifyConfigVo.getConfigStr();
            }
        }
        if (StringUtils.isBlank(config)) {
            config = notifyConfigMapper.getConfigByType(NotifyHandlerType.EMAIL.getValue());
        }
        if (StringUtils.isBlank(config)) {
            throw new EmailServerNotFoundException();
        }
        MailServerVo mailServerVo = JSON.parseObject(config, MailServerVo.class);
        sendEmailWithFile(title, content, to, cc, attachmentMap, mailServerVo);
    }

    public static void sendEmailWithFile(String title, String content,
                                         List<String> to, List<String> cc,
                                         Map<String, InputStream> attachmentMap) throws MessagingException, IOException {
        String config = notifyConfigMapper.getConfigByType(NotifyHandlerType.EMAIL.getValue());
        if (StringUtils.isBlank(config)) {
            throw new EmailServerNotFoundException();
        }
        MailServerVo mailServerVo = JSON.parseObject(config, MailServerVo.class);
        sendEmailWithFile(title, content, to, cc, attachmentMap, mailServerVo);
    }

    public static void sendHtmlEmail(Long notifyConfigId, String title, String content,
                                     List<String> to, List<String> cc) throws MessagingException, IOException {
        sendEmailWithFile(notifyConfigId, title, content, to, cc, null);
    }

    public static void sendHtmlEmail(String title, String content,
                                     List<String> to, List<String> cc) throws MessagingException, IOException {
        sendEmailWithFile(title, content, to, cc, null);
    }

    public static void sendHtmlEmail(String title, String content,
                                     String to, String cc) throws MessagingException, IOException {
        List<String> toList = parseAddressList(to);
        List<String> ccList = parseAddressList(cc);
        sendEmailWithFile(title, content, toList, ccList, null);
    }

    private static List<String> parseAddressList(String addressStr) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(addressStr)) {
            if (addressStr.contains(",")) {
                list.addAll(Arrays.asList(addressStr.split(",")));
            } else {
                list.add(addressStr);
            }
        }
        return list;
    }
}
