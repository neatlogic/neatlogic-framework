package codedriver.framework.notify.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import codedriver.framework.dao.mapper.MailServerMapper;
import codedriver.framework.dto.MailServerVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.notify.core.NotifyHandlerBase;
import codedriver.framework.notify.core.NotifyHandlerType;
import codedriver.framework.notify.dto.NotifyVo;
import codedriver.framework.notify.exception.EmailServerNotFoundException;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 15:34
 **/
@Component
public class EmailNotifyHandler extends NotifyHandlerBase {

	private static Logger logger = LoggerFactory.getLogger(EmailNotifyHandler.class);

	@Autowired
	private MailServerMapper mailServerMapper;

	@Override
	public void myExecute(NotifyVo notifyVo) {
		sendEmail(notifyVo, true);
	}

	@Override
	public String getName() {
		return NotifyHandlerType.EMAIL.getText();
	}


	@Override
	public String getType() {
		return NotifyHandlerType.EMAIL.getValue();
	}
}
