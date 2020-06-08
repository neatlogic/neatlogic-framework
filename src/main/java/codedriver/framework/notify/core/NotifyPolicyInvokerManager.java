package codedriver.framework.notify.core;

import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.common.RootComponent;
import codedriver.framework.notify.dao.mapper.NotifyMapper;
import codedriver.framework.notify.dto.NotifyPolicyInvokerVo;
@RootComponent
public class NotifyPolicyInvokerManager {
	
	@Autowired
	private NotifyMapper notifyMapper;
	
	public void addInvoker(NotifyPolicyInvokerVo notifyPolicyInvokerVo) {
		notifyMapper.insertNotifyPolicyInvoker(notifyPolicyInvokerVo);
	}
	
	public void removeInvoker(String invoker) {
		notifyMapper.deleteNotifyPolicyInvoker(invoker);
	}
}
