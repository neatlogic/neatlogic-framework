/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core;

import codedriver.framework.restful.constvalue.ApiAnonymousAccessSupportEnum;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IBinaryStreamApiComponent {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getId();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getName();

    // true时返回格式不再包裹固定格式
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean isRaw() {
        return false;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getConfig();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    int needAudit();

    Object doService(ApiVo interfaceVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    JSONObject help();

    /**
     * @Description: 是否支持匿名访问
     * @Author: linbq
     * @Date: 2021/3/11 18:37
     * @Params:[]
     * @Returns:boolean
     **/
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default ApiAnonymousAccessSupportEnum supportAnonymousAccess() {
        return ApiAnonymousAccessSupportEnum.ANONYMOUS_ACCESS_FORBIDDEN;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean disableReturnCircularReferenceDetect() {
        return false;
    }
}
