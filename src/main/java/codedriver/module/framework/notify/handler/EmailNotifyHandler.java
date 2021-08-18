/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.notify.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import codedriver.framework.dao.mapper.MailServerMapper;
import codedriver.framework.notify.core.NotifyHandlerBase;
import codedriver.framework.notify.core.NotifyHandlerType;
import codedriver.framework.notify.dto.NotifyVo;

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
