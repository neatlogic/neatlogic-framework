/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import codedriver.framework.restful.dto.ApiVo;

public interface IJsonStreamApiComponent {

    String getId();

    String getName();

    String getConfig();

    // true时返回格式不再包裹固定格式
    default boolean isRaw() {
        return false;
    }

    int needAudit();

    Object doService(ApiVo interfaceVo, JSONObject paramObj, JSONReader jsonReader) throws Exception;

    JSONObject help();

    /**
     * 是否支持匿名访问
     *
     * @return
     */
    default boolean supportAnonymousAccess() {
        return false;
    }

    default boolean disableReturnCircularReferenceDetect() {
        return false;
    }
}
