package codedriver.framework.elasticsearch.core;

import java.util.List;

public interface IElasticSearchHandler {
	
	/**
	 * poolName
	 * @return
	 */
	public String getPoolName();

	/**
	 * 处理类
	 * @return
	 */
	public String getHandler();
	
	/**
	 * 处理类名
	 * @return
	 */
	public String getHandlerName();
	
	/**
	 * 执行动作
	 */
	public void doService(List<Object> params);
}
