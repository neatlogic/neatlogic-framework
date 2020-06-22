package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author:chenqiwei
 * @Time:Jun 19, 2020
 * @ClassName: MyApiComponent
 * @Description: 此类用于提供两个接口方法，让实现类支持事务增强
 */
public interface MyApiComponent extends IApiComponent {
	public abstract Object myDoService(JSONObject jsonObj) throws Exception;

	public default Object myDoTest(JSONObject jsonObj) {
		return null;
	}
}
