/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.dto.FieldValidResultVo;
import codedriver.framework.dto.api.CacheControlVo;
import codedriver.framework.exception.core.ApiFieldValidNotFoundException;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentFactory;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;
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
                validApi(targetClass, paramObj, JSONObject.class);
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
                            response.setHeader("Cache-Control", cacheControlVo.getCacheControlType().getValue() + cacheControlVo.getMaxAge());
                        }
                    }
                }
            } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
                validApi(this.getClass(), paramObj, JSONObject.class);
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
                    throw new ApiRuntimeException(((ApiRuntimeException) ex.getCause()).getMessage(true), ex.getCause());
                } else {
                    throw ex;
                }
            }
        } catch (Exception e) {
            Throwable target = e;
            if( TenantContext.get() != null){
                TenantContext.get().setUseDefaultDatasource(false);//防止上游异常导致后续审计没有还原原来的租户
            }
            //如果是反射抛得异常，则需循环拆包，把真实得异常类找出来
            while (target instanceof InvocationTargetException) {
                target = ((InvocationTargetException) target).getTargetException();
            }
            error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
            throw (Exception) target;
        } finally {
            long endTime = System.currentTimeMillis();
            ApiVo apiConfigVo = apiMapper.getApiByToken(apiVo.getToken());
            // 如果没有配置，则使用默认配置
            if (apiConfigVo == null) {
                apiConfigVo = apiVo;
            }
            if (apiConfigVo.getNeedAudit() != null && apiConfigVo.getNeedAudit().equals(1)) {
                //TODO 对象冲突
                saveAudit(apiVo, paramObj, result, error, startTime, endTime);
            }

        }

        return result;
    }

    @Override
    public final JSONObject help() {
        return getApiComponentHelp(JSONObject.class);
    }

}
