package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import codedriver.framework.restful.dto.ApiVo;

public interface IJsonStreamApiComponent {

	public String getId();

	public String getName();

	public String getConfig();

	// true时返回格式不再包裹固定格式
	public default boolean isRaw() {
		return false;
	}

	public int needAudit();

	public Object doService(ApiVo interfaceVo, JSONObject paramObj, JSONReader jsonReader) throws Exception;

	public JSONObject help();

	/**
	 * @Description: 是否支持匿名访问
	 * @Author: linbq
	 * @Date: 2021/3/11 18:37
	 * @Params:[]
	 * @Returns:boolean
	 **/
	public default boolean supportAnonymousAccess(){
		return false;
	}
}
