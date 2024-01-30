/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.restful.constvalue.ApiAnonymousAccessSupportEnum;
import neatlogic.framework.restful.dto.ApiVo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IBinaryStreamApiComponent {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

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
