/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core;

import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IBinaryStreamApiComponent {

	String getId();

	String getName();

	// true时返回格式不再包裹固定格式
	default boolean isRaw() {
		return false;
	}

	String getConfig();

	int needAudit();

	Object doService(ApiVo interfaceVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;

	JSONObject help();

	/**
	 * @Description: 是否支持匿名访问
	 * @Author: linbq
	 * @Date: 2021/3/11 18:37
	 * @Params:[]
	 * @Returns:boolean
	 **/
	default boolean supportAnonymousAccess() {
		return false;
	}

	default boolean disableReturnCircularReferenceDetect() {
		return false;
	}
}
