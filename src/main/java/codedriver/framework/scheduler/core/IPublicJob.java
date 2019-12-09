package codedriver.framework.scheduler.core;

public interface IPublicJob extends IJob{

	/**
	* @Author: linbq
	* @Time:2019年11月15日
	* @Description: job类型(flow级别的，task级别的,， once只允许配一次)
	* @param @return 
	* @return String
	 */
	public abstract String getType();
	/**
	* @Author: chenqiwei
	* @Time:Dec 6, 2018
	* @Description: 模块中文名 
	* @param @return 
	* @return Integer
	 */
	public abstract String getJobClassName();
}
