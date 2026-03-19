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

package neatlogic.framework.restful.core.privateapi.raw;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.dto.api.CacheControlVo;
import neatlogic.framework.restful.core.ApiComponentTemplateBase;
import neatlogic.framework.restful.dto.ApiVo;

import javax.servlet.http.HttpServletResponse;

/**
 * raw 接口基类。
 * 基于公共模板封装代理调用、权限校验、raw 重提校验、审计和帮助信息，并保留 raw 接口无字段校验模型的语义。
 */
public abstract class RawApiComponentBase extends ApiComponentTemplateBase implements MyRawApiComponent {

    /**
     * raw 接口没有字段模型，因此保持原语义直接返回 null。
     */
    public final FieldValidResultVo doValid(ApiVo apiVo, String param, String validField) throws Exception {
        //raw接口没有字段，因此无需验证逻辑
        return null;
    }

    /**
     * raw 接口执行模板。
     * 保留 raw 重提校验、异常拆包和与对象型相同的 master 审计豁免语义。
     */
    public final Object doService(ApiVo apiVo, String param, HttpServletResponse response) throws Exception {
        String error = "";
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {
            result = invokeWithProxyFallback(
                    (proxy, targetClass) -> executeService(apiVo, param, response, proxy, targetClass, true),
                    () -> executeService(apiVo, param, response, this, this.getClass(), false)
            );
        } catch (Exception e) {
            Throwable target = unwrapInvocationTarget(e);
            error = resolveErrorMessage(e);
            if (target instanceof Exception) {
                throw (Exception) target;
            } else if (target instanceof Error) {
                throw (Error) target;
            }
        } finally {
            long endTime = System.currentTimeMillis();
            if (shouldAudit(apiVo, true)) {
                saveAudit(apiVo, param, result, error, startTime, endTime);
            }
        }

        return result;
    }


    @Override
    public final JSONObject help() {
        return getHelpWithExample(this.example(), String.class);
    }

    /**
     * 执行 raw 接口的实际服务逻辑。
     */
    private Object executeService(ApiVo apiVo, String param, HttpServletResponse response, Object component, Class<?> targetClass,
                                  boolean useDeclaredCacheControlType) throws Exception {
        validAuth(targetClass);
        validIsReSubmitForRaw(targetClass, apiVo.getToken(), param, String.class);
        Object result = invokeComponentMethod(component, "myDoService", new Class[]{String.class}, param);
        if (Config.ENABLE_INTERFACE_VERIFY()) {
            validOutput(targetClass, result, JSONObject.class);
        }
        if (response != null) {
            CacheControlVo cacheControlVo = getCacheControl(String.class);
            if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
                String headerValue = useDeclaredCacheControlType
                        ? cacheControlVo.getCacheControlType().getValue() + cacheControlVo.getMaxAge()
                        : "max-age=" + cacheControlVo.getMaxAge();
                response.setHeader("Cache-Control", headerValue);
            }
        }
        return result;
    }
}
