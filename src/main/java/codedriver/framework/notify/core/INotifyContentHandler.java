package codedriver.framework.notify.core;

import org.springframework.util.ClassUtils;

/**
 * 通知定时任务插件接口
 */
public interface INotifyContentHandler {


	public String getName();


	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

}
