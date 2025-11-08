/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.dto.api.CacheControlVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.restful.dto.ApiVo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class RawApiComponentBase extends ApiValidateAndHelpBase implements MyRawApiComponent {

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
                validAuth(targetClass);
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
                validAuth(this.getClass());
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
                if (ex.getCause() instanceof ApiRuntimeException) {
                    throw new ApiRuntimeException(ex.getCause().getMessage(), ex.getCause());
                } else {
                    throw ex;
                }
            }
        } catch (Exception e) {
            Throwable target = e;
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
                if (apiVo.getNeedAudit() != null && apiVo.getNeedAudit().equals(1)) {
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
