package codedriver.framework.scheduler.core;

import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dto.JobPropVo;


public interface IJob extends Job{
	@Transactional
	public abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;
	
	/**
	* @Author: chenqiwei
	* @Time:Dec 6, 2018
	* @Description: 模块id 
	* @param @return 
	* @return Integer
	 */
	public abstract Integer getJobClassId();
	
	/**
	* @Author: chenqiwei
	* @Time:Dec 6, 2018
	* @Description: 模块中文名 
	* @param @return 
	* @return Integer
	 */
	public abstract String getJobClassName();
	
	/**
	* @Author: chenqiwei
	* @Time:Dec 6, 2018
	* @Description: 模块全路径 
	* @param @return 
	* @return Integer
	 */
	public abstract String getClassName();
	
	/**
	* @Description: 解析注解参数
	* @Param: []
	* @return: net.sf.json.JSONObject
	* @Author: lixs
	* @Date: 2019/1/18
	*/
	public abstract  Map<String, Param> initProp();
	
	/**
	* @Description:  参数类型校验
	* @Param: [jobPropVoList]
	* @return: boolean
	* @Author: lixs
	* @Date: 2019/1/18
	*/
	public abstract boolean valid(List<JobPropVo> jobPropVoList);
}
