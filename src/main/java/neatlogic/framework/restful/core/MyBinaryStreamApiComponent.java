package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: chenqiwei
 * @Time: Jun 19, 2020
 * @ClassName: MyBinaryStreamApiComponent
 * @Description: 此类用于提供两个接口方法，让实现类支持事务增强
 */
public interface MyBinaryStreamApiComponent extends IBinaryStreamApiComponent {
	Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
