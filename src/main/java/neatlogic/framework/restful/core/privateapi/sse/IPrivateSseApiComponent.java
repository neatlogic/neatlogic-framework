/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.restful.core.privateapi.sse;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.restful.constvalue.ApiAnonymousAccessSupportEnum;
import neatlogic.framework.restful.dto.ApiVo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IPrivateSseApiComponent {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getToken();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getName();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default String getDescription() {
        return this.getName();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default String getConfig() {
        return null;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean isRaw() {
        return true;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    int needAudit();

    Object doService(ApiVo apiVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    JSONObject help();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default JSONObject example() {
        return null;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default ApiAnonymousAccessSupportEnum supportAnonymousAccess() {
        return ApiAnonymousAccessSupportEnum.ANONYMOUS_ACCESS_FORBIDDEN;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean disableReturnCircularReferenceDetect() {
        return false;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean isBasicSupport() {
        return false;
    }
}
