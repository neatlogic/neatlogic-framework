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

import neatlogic.framework.restful.constvalue.ApiAnonymousAccessSupportEnum;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import neatlogic.framework.restful.dto.ApiVo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IJsonStreamApiComponent {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getId();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getName();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getConfig();

    // true时返回格式不再包裹固定格式
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean isRaw() {
        return false;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    int needAudit();

    Object doService(ApiVo interfaceVo, JSONObject paramObj, JSONReader jsonReader) throws Exception;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    JSONObject help();

    /**
     * 是否支持匿名访问
     *
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default ApiAnonymousAccessSupportEnum supportAnonymousAccess() {
        return ApiAnonymousAccessSupportEnum.ANONYMOUS_ACCESS_FORBIDDEN;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean disableReturnCircularReferenceDetect() {
        return false;
    }
}
