package codedriver.framework.notify.core;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
		if (CollectionUtils.isEmpty(notifyVo.getToUserList())) {
			if (CollectionUtils.isNotEmpty(notifyVo.getToUserUuidList())) {
				for (String userUuid : notifyVo.getToUserUuidList()) {
					UserVo userVo = userMapper.getUserBaseInfoByUuid(userUuid);
					if (userVo != null) {
						notifyVo.addUser(userVo);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(notifyVo.getToTeamIdList())) {
				for (String teamId : notifyVo.getToTeamIdList()) {
					List<UserVo> teamUserList = userMapper.getActiveUserByTeamId(teamId);
					for (UserVo userVo : teamUserList) {
						notifyVo.addUser(userVo);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(notifyVo.getToRoleUuidList())) {
				for (String roleUuid : notifyVo.getToRoleUuidList()) {
					List<UserVo> roleUserList = userMapper.getActiveUserByRoleUuid(roleUuid);
					for (UserVo userVo : roleUserList) {
						notifyVo.addUser(userVo);
					}
				}
			}
		}
		if (StringUtils.isNotBlank(notifyVo.getFromUser())) {
			UserVo userVo = userMapper.getUserBaseInfoByUuid(notifyVo.getFromUser());
			if (userVo != null && StringUtils.isNotBlank(userVo.getEmail())) {
				notifyVo.setFromUserEmail(userVo.getEmail());
			}
		}
		if(CollectionUtils.isNotEmpty(notifyVo.getExceptionNotifyUserUuidList())) {
			List<UserVo> exceptionNotifyUserList = notifyVo.getExceptionNotifyUserList();
			for(String userUuid : notifyVo.getExceptionNotifyUserUuidList()) {
				UserVo userVo = userMapper.getUserBaseInfoByUuid(userUuid);
				if (userVo != null) {
					exceptionNotifyUserList.add(userVo);
				}
			}
		}
		if(StringUtils.isNotBlank(notifyVo.getError())) {
		    System.out.println(notifyVo.getError());
		    logger.error(notifyVo.getError());
			sendEmail(notifyVo);
		}else {
			if (CollectionUtils.isNotEmpty(notifyVo.getToUserList())) {
				myExecute(notifyVo);
			} else {
				throw new NotifyNoReceiverException();
			}
		}
		
	}

	protected abstract void myExecute(NotifyVo notifyVo);
	
	private void sendEmail(NotifyVo notifyVo) {
		if (CollectionUtils.isNotEmpty(notifyVo.getToUserList())) {
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

					se.setSubject("通知发送异常");
					StringBuilder sb = new StringBuilder();
					sb.append("<html>");
					sb.append("<head>");
					sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
					sb.append("<style type=\"text/css\">");
					sb.append("</style>");
					sb.append("</head><body>");
					sb.append(notifyVo.getError());
					sb.append("</body></html>");
					se.addPart(sb.toString(), "text/html;charset=utf-8");
					boolean isSend = false;
					for (UserVo user : notifyVo.getExceptionNotifyUserList()) {
						if (StringUtils.isNotBlank(user.getEmail())) {
						    isSend = true;
							se.addTo(user.getEmail());
						}
					}
					if(isSend) {
	                    se.send();					    
					}
				} else {
					throw new EmailServerNotFoundException();
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}
}
