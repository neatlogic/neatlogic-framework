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
import neatlogic.framework.exception.core.ApiFieldValidNotFoundException;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 对象型接口基类。
 * 基于公共模板封装参数校验、重提校验、审计和帮助信息，并保留对象型接口独有的字段校验与 myDoTest 语义。
 */
public abstract class ApiComponentBase extends ApiComponentTemplateBase implements MyApiComponent {

    /**
     * 对象型接口支持字段级校验，沿用原有 valid 方法反射查找 IValid 的语义。
     */
    public final FieldValidResultVo doValid(ApiVo apiVo, JSONObject paramObj, String validField) throws Exception {

        Method[] methods = new Method[]{};
        Object target = null;
        boolean isHasValid = false;
        FieldValidResultVo resultVo = null;
        try {
            IApiComponent restComponent = PrivateApiComponentFactory.getComponent(apiVo.getHandler(), ApiType.OBJECT, IApiComponent.class);
            try {
                Object proxy = AopContext.currentProxy();
                //获取代理的真实bean
                target = ((Advised) proxy).getTargetSource().getTarget();
                methods = AopUtils.getTargetClass(proxy).getMethods();
            } catch (Exception ex) {
                target = restComponent;
                methods = restComponent.getClass().getMethods();
            } finally {
                for (Method method : methods) {
                    //System.out.println(method.getName());
                    if (method.getGenericReturnType().getTypeName().equals(IValid.class.getTypeName()) && method.getName().equals(validField)) {
                        isHasValid = true;
                        //特殊入参校验：重复、特殊规则等
                        IValid validComponent = (IValid) method.invoke(target);
                        resultVo = validComponent.valid(paramObj);
                        break;
                    }
                }
                //如果不存在该校验方法则抛异常
                if (!isHasValid) {
                    throw new ApiFieldValidNotFoundException(validField);
                }
            }
        } catch (Exception e) {
            Throwable targetException = e;
            //如果是反射抛得异常，则需要拆包，把真实得异常类找出来
            while (targetException instanceof InvocationTargetException) {
                targetException = ((InvocationTargetException) targetException).getTargetException();
            }
            throw (Exception) targetException;
        }
        return resultVo;
    }

    /**
     * 对象型接口执行模板。
     * 保留非激活接口走 myDoTest、代理分支与本地分支 Cache-Control 写法不同等历史语义。
     */
    public final Object doService(ApiVo apiVo, JSONObject paramObj, HttpServletResponse response) throws Exception {
        String error = "";
        Object result = null;
        String param = getAuditParam(apiVo, paramObj, true);
        long startTime = System.currentTimeMillis();
        try {
            result = invokeWithProxyFallback(
                    (proxy, targetClass) -> executeService(apiVo, paramObj, response, proxy, targetClass, true),
                    () -> executeService(apiVo, paramObj, response, this, this.getClass(), false)
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
                saveAudit(apiVo, JSONObject.parseObject(param), result, error, startTime, endTime);
            }

        }

        return result;
    }

    @Override
    public final JSONObject help() {
        return getHelpWithExample(this.example(), JSONObject.class);
    }

    /**
     * 执行对象型接口的实际服务逻辑。
     */
    private Object executeService(ApiVo apiVo, JSONObject paramObj, HttpServletResponse response, Object component, Class<?> targetClass,
                                  boolean useDeclaredCacheControlType) throws Exception {
        validApi(targetClass, paramObj, apiVo, JSONObject.class);
        Object result = null;
        boolean canRun = false;
        if (apiVo.getIsActive().equals(0)) {
            result = invokeComponentMethod(component, "myDoTest", new Class[]{JSONObject.class}, paramObj);
            canRun = (result == null);
        } else {
            canRun = true;
        }
        if (canRun) {
            validIsReSubmit(targetClass, apiVo.getToken(), paramObj, JSONObject.class);
            result = invokeComponentMethod(component, "myDoService", new Class[]{JSONObject.class}, paramObj);
            if (Config.ENABLE_INTERFACE_VERIFY()) {
                validOutput(targetClass, result, JSONObject.class);
            }
            if (response != null) {
                CacheControlVo cacheControlVo = getCacheControl(JSONObject.class);
                if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
                    String headerValue = useDeclaredCacheControlType
                            ? cacheControlVo.getCacheControlType().getValue() + "=" + cacheControlVo.getMaxAge()
                            : "max-age=" + cacheControlVo.getMaxAge();
                    response.setHeader("Cache-Control", headerValue);
                }
            }
        }
        return result;
    }
}
