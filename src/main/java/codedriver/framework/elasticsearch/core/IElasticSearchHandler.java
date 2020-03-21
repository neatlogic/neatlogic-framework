package codedriver.framework.elasticsearch.core;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface IElasticSearchHandler {
	
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
	public void doService(JSONObject paramObj);
	
	/**
	 * 执行动作
	 */
	public JSONObject getConfig(List<Object> paramList);
}
