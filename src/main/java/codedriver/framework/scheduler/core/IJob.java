package codedriver.framework.scheduler.core;

import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.ClassUtils;

import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobPropVo;

public interface IJob extends Job {

	public abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

	/**
	 * @Author: chenqiwei
	 * @Time:Dec 6, 2018
	 * @Description: 模块全路径
	 * @param @return
	 * @return Integer
	 */
	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}
	
	/**
	* @Author: chenqiwei
	* @Time:Feb 8, 2020
	* @Description: TODO 
	* @param @return 
	* @return IJob
	 */
	public default IJob getThis() {
		return SchedulerManager.getHandler(this.getClassName());
	}
	/**
	* @Author: chenqiwei
	* @Time:Feb 8, 2020
	* @Description: 获取分组名称 
	* @param @return 
	* @return String
	 */
	public abstract String getGroupName();

	/**
	 * @Description: 解析注解参数
	 * @Param: []
	 * @return: net.sf.json.JSONObject
	 * @Author: lixs
	 * @Date: 2019/1/18
	 */
	public abstract Map<String, Param> initProp();

	/**
	 * @Description: 参数类型校验
	 * @Param: [jobPropVoList]
	 * @return: boolean
	 * @Author: lixs
	 * @Date: 2019/1/18
	 */
	public abstract boolean valid(List<JobPropVo> jobPropVoList);

	public Boolean checkCronIsExpired(JobObject jobObject);

	/**
	 * 
	 * @Time:Feb 4, 2020
	 * @Description: 重新加载单个作业
	 * @param @param
	 *            jobObject
	 * @return void
	 */
	public void reloadJob(JobObject jobObject);

	/**
	 * @Time:Feb 4, 2020
	 * @Description: 加载当前类的租户作业
	 * @param @param
	 *            tenantUuid
	 * @return void
	 */
	public void initJob(String tenantUuid);
}
