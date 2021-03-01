package codedriver.framework.restful.core;

import codedriver.framework.asynchronization.threadpool.ScheduledThreadPool;
import codedriver.framework.common.config.Config;
import codedriver.framework.dto.FieldValidResultVo;
import codedriver.framework.exception.core.ApiFieldValidNotFoundException;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.exception.resubmit.ResubmitException;
import codedriver.framework.restful.annotation.ResubmitInterval;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentFactory;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;
import codedriver.framework.util.Md5Util;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ApiComponentBase extends ApiValidateAndHelpBase implements MyApiComponent {
    private static final Map<String, Integer> SUBMIT_MAP = new ConcurrentHashMap<>();

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
            } catch (IllegalStateException | IllegalArgumentException | SecurityException ex) {
                target = restComponent;
                methods = restComponent.getClass().getMethods();
            } finally {
                for (Method method : methods) {
                    //System.out.println(method.getName());
                    if (method.getGenericReturnType().getTypeName().equals(IValid.class.getTypeName()) && method.getName().equals(validField)) {
                        isHasValid = true;
                        //特殊入参校验：重复、特殊规则等
                        IValid validComponent = (IValid) method.invoke(target, null);
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

    public final Object doService(ApiVo apiVo, JSONObject paramObj) throws Exception {
        String error = "";
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {
            Method m = this.getClass().getDeclaredMethod("myDoService", JSONObject.class);
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
                    if (m.isAnnotationPresent(ResubmitInterval.class)) {
                        for (Annotation anno : m.getDeclaredAnnotations()) {
                            if (anno.annotationType().equals(ResubmitInterval.class)) {
                                ResubmitInterval resubmitInterval = (ResubmitInterval) anno;
                                if (resubmitInterval.value() > 0) {
                                    String key = Md5Util.encryptMD5(paramObj.toString());
                                    if (SUBMIT_MAP.containsKey(apiVo.getToken() + "_" + key)) {
                                        throw new ResubmitException(apiVo.getToken());
                                    } else {
                                        SUBMIT_MAP.put(apiVo.getToken() + "_" + key, resubmitInterval.value());
                                        ScheduledThreadPool.execute(new RemoveSubmitKeyThread(apiVo.getToken() + "_" + key) {
                                            @Override
                                            protected void execute() {
                                                SUBMIT_MAP.remove(this.getKey());
                                            }
                                        }, resubmitInterval.value());
                                    }
                                }
                                break;
                            }
                        }
                    }
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
