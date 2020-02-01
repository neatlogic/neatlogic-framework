package codedriver.framework.scheduler.core;

import org.springframework.util.ClassUtils;

public interface IPublicJob extends IJob {

	/**
	 * @Author: linbq
	 * @Time:2019年11月15日
	 * @Description: job类型(flow级别的，task级别的,， once只允许配一次)
	 * @param @return
	 * @return String
	 */
	public abstract String getType();

	public abstract String getName();
}
