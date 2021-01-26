package codedriver.framework.restful.core;

import codedriver.framework.common.config.Config;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ApiComponentBase extends ApiValidateAndHelpBase implements MyApiComponent {

    @Autowired
    private ApiMapper apiMapper;

    public int needAudit() {
        return 0;
    }


    public final Object doService(ApiVo apiVo, JSONObject paramObj) throws Exception {
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
                    Method method = proxy.getClass().getMethod("myDoService", JSONObject.class);
                    result = method.invoke(proxy, paramObj);
                    if (Config.ENABLE_INTERFACE_VERIFY()) {
                        validOutput(targetClass, result, JSONObject.class);
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
                    result = myDoService(paramObj);
                    if (Config.ENABLE_INTERFACE_VERIFY()) {
                        validOutput(this.getClass(), result, JSONObject.class);
                    }
                }
            } catch (Exception ex) {
                if (ex.getCause() != null && ex.getCause() instanceof ApiRuntimeException) {
                    throw new ApiRuntimeException(ex.getCause().getMessage());
                } else {
                    throw ex;
                }
            }
        } catch (Exception e) {
            Throwable target = e;
            //如果是反射抛得异常，则需要拆包，把真实得异常类找出来
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
