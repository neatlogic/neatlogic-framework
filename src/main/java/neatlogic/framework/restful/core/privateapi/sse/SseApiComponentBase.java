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
import neatlogic.framework.restful.core.ApiComponentTemplateBase;
import neatlogic.framework.restful.dto.ApiVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class SseApiComponentBase extends ApiComponentTemplateBase implements MySseApiComponent {

    @Override
    public final Object doService(ApiVo apiVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String error = "";
        Object result = null;
        String param = getAuditParam(apiVo, paramObj);
        long startTime = System.currentTimeMillis();
        try {
            result = invokeWithProxyFallback(
                    (proxy, targetClass) -> executeService(apiVo, paramObj, request, response, proxy, targetClass),
                    () -> executeService(apiVo, paramObj, request, response, this, this.getClass())
            );
        } catch (Exception e) {
            error = resolveErrorMessage(e);
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            if (shouldAudit(apiVo)) {
                saveAudit(apiVo, JSONObject.parseObject(param), result, error, startTime, endTime);
            }
        }
        return result;
    }

    @Override
    public final JSONObject help() {
        return getApiComponentHelp(JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
    }

    private Object executeService(ApiVo apiVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response,
                                  Object component, Class<?> targetClass) throws Exception {
        validApi(targetClass, paramObj, apiVo, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
        validIsReSubmit(targetClass, apiVo.getToken(), paramObj, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
        return invokeComponentMethod(component, "myDoService",
                new Class[]{JSONObject.class, HttpServletRequest.class, HttpServletResponse.class}, paramObj, request, response);
    }
}
