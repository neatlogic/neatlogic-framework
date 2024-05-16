/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.dto.api.CacheControlVo;
import neatlogic.framework.exception.core.ApiFieldValidNotFoundException;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiVo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ApiComponentBase extends ApiValidateAndHelpBase implements MyApiComponent {

    @Resource
    private ApiMapper apiMapper;

    public int needAudit() {
        return 0;
    }

    public final FieldValidResultVo doValid(ApiVo apiVo, JSONObject paramObj, String validField) throws Exception {

        Method[] methods = new Method[]{};
        Object target = null;
        boolean isHasValid = false;
        FieldValidResultVo resultVo = null;
        try {
            IApiComponent restComponent = PrivateApiComponentFactory.getInstance(apiVo.getHandler());
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

    public final Object doService(ApiVo apiVo, JSONObject paramObj, HttpServletResponse response) throws Exception {
        String error = "";
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {

            try {
                Object proxy = AopContext.currentProxy();
                Class<?> targetClass = AopUtils.getTargetClass(proxy);
                validApi(targetClass, paramObj, apiVo, JSONObject.class);
                boolean canRun = false;
                if (apiVo.getIsActive().equals(0)) {
                    Method method = proxy.getClass().getMethod("myDoTest", JSONObject.class);
                    result = method.invoke(proxy, paramObj);
                    if (result == null) {
                        canRun = true;
                    }
                } else {
                    canRun = true;
                }
                if (canRun) {
                    validIsReSubmit(targetClass, apiVo.getToken(), paramObj, JSONObject.class);
                    Method method = proxy.getClass().getMethod("myDoService", JSONObject.class);
                    result = method.invoke(proxy, paramObj);
                    if (Config.ENABLE_INTERFACE_VERIFY()) {
                        validOutput(targetClass, result, JSONObject.class);
                    }
                    //设置Cache-Control
                    if (response != null) {
                        CacheControlVo cacheControlVo = getCacheControl(JSONObject.class);
                        if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
                            response.setHeader("Cache-Control", cacheControlVo.getCacheControlType().getValue() +"="+ cacheControlVo.getMaxAge());
                        }
                    }
                }
            } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException |
                    SecurityException ex) {
                validApi(this.getClass(), paramObj, apiVo, JSONObject.class);
                boolean canRun = false;
                if (apiVo.getIsActive().equals(0)) {
                    result = myDoTest(paramObj);
                    if (result == null) {
                        canRun = true;
                    }
                } else {
                    canRun = true;
                }
                if (canRun) {
                    validIsReSubmit(this.getClass(), apiVo.getToken(), paramObj, JSONObject.class);
                    result = myDoService(paramObj);
                    if (Config.ENABLE_INTERFACE_VERIFY()) {
                        validOutput(this.getClass(), result, JSONObject.class);
                    }
                    //设置Cache-Control
                    if (response != null) {
                        CacheControlVo cacheControlVo = getCacheControl(JSONObject.class);
                        if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
                            response.setHeader("Cache-Control", "max-age=" + cacheControlVo.getMaxAge());
                        }
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
                    saveAudit(apiVo, paramObj, result, error, startTime, endTime);
                }
            }

        }

        return result;
    }

    @Override
    public final JSONObject help() {
        JSONObject helpObj = getApiComponentHelp(JSONObject.class);
        JSONObject example = this.example();
        if (MapUtils.isNotEmpty(example)) {
            helpObj.put("example", example);
        }
        return helpObj;
    }

}
