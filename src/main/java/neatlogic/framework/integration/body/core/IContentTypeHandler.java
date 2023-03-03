package neatlogic.framework.integration.body.core;

import java.net.HttpURLConnection;

import com.alibaba.fastjson.JSONObject;

public interface IContentTypeHandler {
	public String getType();

	/**
	 * @Author: chenqiwei
	 * @Time:Apr 16, 2020
	 * @Description: TODO
	 * @param @param connection 连接
	 * @param @param integrationParam 来自调用的参数
	 * @param @param config 配置
	 * @return void
	 */
	public String handleData(HttpURLConnection connection, JSONObject integrationParam, JSONObject config);
}
