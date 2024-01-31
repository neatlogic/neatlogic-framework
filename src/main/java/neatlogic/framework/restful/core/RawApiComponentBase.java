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
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.dto.api.CacheControlVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiVo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class RawApiComponentBase extends ApiValidateAndHelpBase implements MyRawApiComponent {

    @Resource
    private ApiMapper apiMapper;

    public int needAudit() {
        return 0;
    }

    public final FieldValidResultVo doValid(ApiVo apiVo, String param, String validField) throws Exception {
        //raw接口没有字段，因此无需验证逻辑
        return null;
    }

    public final Object doService(ApiVo apiVo, String param, HttpServletResponse response) throws Exception {
        String error = "";
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {

            try {
                Object proxy = AopContext.currentProxy();
                Class<?> targetClass = AopUtils.getTargetClass(proxy);
                validApiFowRaw(targetClass);
                validIsReSubmitForRaw(targetClass, apiVo.getToken(), param, String.class);
                Method method = proxy.getClass().getMethod("myDoService", String.class);
                result = method.invoke(proxy, param);
                if (Config.ENABLE_INTERFACE_VERIFY()) {
                    validOutput(targetClass, result, JSONObject.class);
                }
                //设置Cache-Control
                if (response != null) {
                    CacheControlVo cacheControlVo = getCacheControl(String.class);
                    if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
                        response.setHeader("Cache-Control", cacheControlVo.getCacheControlType().getValue() + cacheControlVo.getMaxAge());
                    }
                }
            } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException |
                     SecurityException ex) {
                validApiFowRaw(this.getClass());
                validIsReSubmitForRaw(this.getClass(), apiVo.getToken(), param, String.class);
                result = myDoService(param);
                if (Config.ENABLE_INTERFACE_VERIFY()) {
                    validOutput(this.getClass(), result, JSONObject.class);
                }
                //设置Cache-Control
                if (response != null) {
                    CacheControlVo cacheControlVo = getCacheControl(String.class);
                    if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
                        response.setHeader("Cache-Control", "max-age=" + cacheControlVo.getMaxAge());
                    }
                }
            } catch (Exception ex) {
                if (ex.getCause() != null && ex.getCause() instanceof ApiRuntimeException) {
                    throw new ApiRuntimeException(ex.getCause().getMessage(), ex.getCause());
                } else {
                    throw ex;
                }
            }
        } catch (Exception e) {
            Throwable target = e;
            if (TenantContext.get() != null) {
                TenantContext.get().setUseDefaultDatasource(false);//防止上游异常导致后续审计没有还原原来的租户
            }
            //如果是反射抛得异常，则需循环拆包，把真实得异常类找出来
            while (target instanceof InvocationTargetException) {
                target = ((InvocationTargetException) target).getTargetException();
            }
            error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
            if (target instanceof Exception) {
                throw (Exception) target;
            } else if (target instanceof Error) {
                throw (Error) target;
            }
        } finally {
            long endTime = System.currentTimeMillis();
            if (!apiVo.getModuleId().equals("master")) {
                ApiVo apiConfigVo = apiMapper.getApiByToken(apiVo.getToken());
                // 如果没有配置，则使用默认配置
                if (apiConfigVo == null) {
                    apiConfigVo = apiVo;
                }
                if (apiConfigVo.getNeedAudit() != null && apiConfigVo.getNeedAudit().equals(1)) {
                    saveAudit(apiVo, param, result, error, startTime, endTime);
                }
            }

        }

        return result;
    }


    @Override
    public final JSONObject help() {
        JSONObject helpObj = getApiComponentHelp(String.class);
        JSONObject example = this.example();
        if (MapUtils.isNotEmpty(example)) {
            helpObj.put("example", example);
        }
        return helpObj;
    }

}
