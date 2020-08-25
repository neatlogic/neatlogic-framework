package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
/**
 * 
* @Author:chenqiwei
* @Time:Jun 19, 2020
* @ClassName: MyJsonStreamApiComponent 
* @Description: 此类用于提供两个接口方法，让实现类支持事务增强
 */
public interface MyJsonStreamApiComponent extends IJsonStreamApiComponent{
	public abstract Object myDoService(JSONObject paramObj, JSONReader jsonReader) throws Exception;
}
