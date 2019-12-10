package codedriver.framework.inform.plugin;

import codedriver.framework.dao.mapper.MailServerMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.MailServerVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.inform.core.InformComponentBase;
import codedriver.framework.inform.dto.MessageVo;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 15:34
 **/
@Component
public class EmailInformPlugin implements InformComponentBase {

    private static Logger logger = LoggerFactory.getLogger(EmailInformPlugin.class);
    private static String SMTP_SERVER_HOST = null;
    private static String USER_MAIL_USERNAME = null;
    private static String USER_MAIL_PASSWORD = null;
    private static String SMTP_SERVER_USERNAME = null;
    private static Integer SMTP_PORT = null;
    private static String FROM_ADDRESS = null;

    @Autowired
    private MailServerMapper mailServerMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void execute(MessageVo messageVo) {
        this.loadSmtpInfo();
        this.sendEmail(messageVo);
    }

    @Override
    public String getId() {
        return "emailPlugin";
    }

    @Override
    public String getTemplateContent() {
        return null;
    }

    @Override
    public String getTemplateTitle() {
        return null;
    }

    private void sendEmail(MessageVo message){
        if (SMTP_SERVER_HOST != null && USER_MAIL_USERNAME != null && USER_MAIL_PASSWORD != null) {
            List<UserVo> toUserList = message.getToUserList();
            List<UserVo> ccUserList = new ArrayList<>();
            JSONArray ccUsers = message.getParamObj().getJSONArray("ccUserIdList");
            if (ccUsers != null && ccUsers.size() > 0){
                for (int i = 0; i < ccUsers.size(); i++){
                    UserVo ccUser =  userMapper.getUserByUserId(ccUsers.getString(i));
                    if (ccUser != null){
                        ccUserList.add(ccUser);
                    }
                }
            }
            HtmlEmail se = null;
            try {
                if (toUserList.size() > 0) {
                    se = new HtmlEmail();
                    se.setHostName(SMTP_SERVER_HOST);

                    if (USER_MAIL_USERNAME != null && !"".equals(USER_MAIL_USERNAME) && USER_MAIL_PASSWORD != null && !"".equals(USER_MAIL_PASSWORD)) {
                        se.setAuthentication(USER_MAIL_USERNAME, USER_MAIL_PASSWORD);
                    }
                    se.setSmtpPort(SMTP_PORT);
                    se.setFrom((FROM_ADDRESS == null || FROM_ADDRESS.equals("")? USER_MAIL_USERNAME : FROM_ADDRESS), (message.getFromUser() != null && !"".equals(message.getFromUser())) ? message.getFromUser() : SMTP_SERVER_USERNAME);
                    se.setSubject(clearStringHTML(message.getTitle()));
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html>");
                    sb.append("<head>");
                    sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
                    sb.append("<style type=\"text/css\">");
                    sb.append("</style>");
                    sb.append("</head><body>");
                    sb.append(message.getContent());
                    sb.append("</body></html>");
                    se.addPart(sb.toString(), "text/html;charset=utf-8");
                    for (UserVo user : toUserList) {
                        se.addTo(user.getEmail());
                    }
                    for (UserVo ccUser : ccUserList) {
                        se.addCc(ccUser.getEmail());
                    }
                    se.send();
                }
            } catch (Exception ex) {
                if (se != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("hostname:" + se.getHostName() + "\n");
                    sb.append("smtpport:" + se.getSmtpPort() + "\n");
                    sb.append("from:" + se.getFromAddress() + "\n");
                    sb.append("subject:" + se.getSubject() + "\n");
                    logger.error(sb.toString() + ex.getMessage());
                }
                logger.error(ex.getMessage(), ex);
            }
        } else {
            logger.error("MAIL SMTP CONFIGURATION INIT FAILURED! ::  Current System has not been configured mail server!");
        }
    }

    private void loadSmtpInfo() {
        MailServerVo mailServerVo = mailServerMapper.getActiveMailServer();
        if (mailServerVo != null) {
            SMTP_SERVER_HOST = mailServerVo.getHost();
            USER_MAIL_USERNAME = mailServerVo.getUserName();
            USER_MAIL_PASSWORD = mailServerVo.getPassword();
            SMTP_SERVER_USERNAME = mailServerVo.getName();
            SMTP_PORT = mailServerVo.getPort();
            FROM_ADDRESS = mailServerVo.getFromAddress();
        }
    }

    public String clearStringHTML(String sourceContent) {
        String content = "";
        if (sourceContent != null) {
            content = sourceContent.replaceAll("</?[^>]+>", "");
        }
        return content;
    }
}
