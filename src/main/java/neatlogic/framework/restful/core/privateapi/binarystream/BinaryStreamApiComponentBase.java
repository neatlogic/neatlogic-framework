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

package neatlogic.framework.restful.core.privateapi.binarystream;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.CacheControlType;
import neatlogic.framework.dto.api.CacheControlVo;
import neatlogic.framework.restful.core.ApiComponentTemplateBase;
import neatlogic.framework.restful.dto.ApiVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 二进制流接口基类。
 * 基于公共模板封装代理调用、参数校验、重提校验、审计和帮助信息，并保留下载场景的 Cache-Control 处理语义。
 */
public abstract class BinaryStreamApiComponentBase extends ApiComponentTemplateBase implements MyBinaryStreamApiComponent {

    /**
     * 二进制流接口执行模板。
     * 保留下载失败时回写 no-cache 的行为。
     */
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
            response.setHeader("Cache-Control", CacheControlType.NOCACHE.getValue());//下次还需验证
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

    /*public final String getId() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }*/

    @Override
    public final JSONObject help() {
        return getApiComponentHelp(JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
    }

    /**
     * 执行二进制流接口的实际服务逻辑。
     */
    private Object executeService(ApiVo apiVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response,
                                  Object component, Class<?> targetClass) throws Exception {
        validApi(targetClass, paramObj, apiVo, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
        validIsReSubmit(targetClass, apiVo.getToken(), paramObj, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
        CacheControlVo cacheControlVo = getCacheControl(JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
        if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
            response.setHeader("Cache-Control", "max-age=" + cacheControlVo.getMaxAge());
        }
        return invokeComponentMethod(component, "myDoService",
                new Class[]{JSONObject.class, HttpServletRequest.class, HttpServletResponse.class}, paramObj, request, response);
    }
}
