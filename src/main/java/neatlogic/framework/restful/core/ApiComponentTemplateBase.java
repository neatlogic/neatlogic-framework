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
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.restful.dto.ApiVo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 收敛各类 API 组件基类的公共模板逻辑，保留各类型自己的调用语义和返回约束。
 */
public abstract class ApiComponentTemplateBase extends ApiValidateAndHelpBase {

    /**
     * 代理调用分支，适用于需要走 AOP 增强的场景。
     */
    @FunctionalInterface
    protected interface ProxyInvocation {
        Object execute(Object proxy, Class<?> targetClass) throws Exception;
    }

    /**
     * 本地调用分支，用于当前线程拿不到代理对象时回退到原始实例执行。
     */
    @FunctionalInterface
    protected interface LocalInvocation {
        Object execute() throws Exception;
    }

    /**
     * 默认不需要审计，由具体组件按需覆盖。
     */
    public int needAudit() {
        return 0;
    }

    /**
     * 统一封装“优先走代理、失败后回退本地实例”的调用模式，
     * 同时保留原有的 ApiRuntimeException 包装语义。
     */
    protected final Object invokeWithProxyFallback(ProxyInvocation proxyInvocation, LocalInvocation localInvocation) throws Exception {
        try {
            Object proxy = AopContext.currentProxy();
            return proxyInvocation.execute(proxy, AopUtils.getTargetClass(proxy));
        } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
            return localInvocation.execute();
        } catch (Exception ex) {
            if (ex.getCause() instanceof ApiRuntimeException) {
                throw new ApiRuntimeException(ex.getCause().getMessage(), ex.getCause());
            }
            throw ex;
        }
    }

    /**
     * 统一反射调用组件方法，避免各基类重复拼接方法签名。
     */
    protected final Object invokeComponentMethod(Object component, String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = component.getClass().getMethod(methodName, parameterTypes);
        return method.invoke(component, args);
    }

    /**
     * 拆开反射包装异常，返回真实业务异常。
     */
    protected final Throwable unwrapInvocationTarget(Throwable throwable) {
        Throwable target = throwable;
        while (target instanceof InvocationTargetException) {
            target = ((InvocationTargetException) target).getTargetException();
        }
        return target;
    }

    /**
     * 统一生成审计错误信息，兼容 message 为空时的堆栈输出。
     */
    protected final String resolveErrorMessage(Exception e) {
        return e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
    }

    /**
     * 判断当前接口是否需要审计。
     */
    protected final boolean shouldAudit(ApiVo apiVo) {
        return shouldAudit(apiVo, false);
    }

    /**
     * 判断当前接口是否需要审计，并允许调用方指定是否跳过 master 模块。
     */
    protected final boolean shouldAudit(ApiVo apiVo, boolean skipMasterModule) {
        if (skipMasterModule && apiVo.getModuleId().equals("master")) {
            return false;
        }
        return apiVo.getNeedAudit() != null && apiVo.getNeedAudit().equals(1);
    }

    /**
     * 为 JSON 入参接口生成审计参数快照。
     */
    protected final String getAuditParam(ApiVo apiVo, JSONObject paramObj) {
        return getAuditParam(apiVo, paramObj, false);
    }

    /**
     * 为 JSON 入参接口生成审计参数快照，并允许调用方指定是否跳过 master 模块。
     */
    protected final String getAuditParam(ApiVo apiVo, JSONObject paramObj, boolean skipMasterModule) {
        if (shouldAudit(apiVo, skipMasterModule)) {
            return paramObj.toJSONString();
        }
        return null;
    }

    /**
     * 统一拼装帮助信息，并在支持 example 的接口类型上附带示例数据。
     */
    protected final JSONObject getHelpWithExample(JSONObject example, Class<?>... parameterTypes) {
        JSONObject helpObj = getApiComponentHelp(parameterTypes);
        if (MapUtils.isNotEmpty(example)) {
            helpObj.put("example", example);
        }
        return helpObj;
    }
}
