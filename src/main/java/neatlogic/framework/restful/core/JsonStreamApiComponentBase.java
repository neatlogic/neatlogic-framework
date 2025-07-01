package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.restful.dto.ApiVo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;

public abstract class JsonStreamApiComponentBase extends ApiValidateAndHelpBase implements MyJsonStreamApiComponent {
    // private static Logger logger =
    // LoggerFactory.getLogger(JsonStreamApiComponentBase.class);

    public int needAudit() {
        return 0;
    }

    @Override
    public final Object doService(ApiVo apiVo, JSONObject paramObj, JSONReader jsonReader) throws Exception {
        String error = "";
        Object result = null;
        long startTime = System.currentTimeMillis();
        // audit.setParam(jsonObj.toString(4));
        try {
            try {
                Object proxy = AopContext.currentProxy();
                Class<?> targetClass = AopUtils.getTargetClass(proxy);
                validApi(targetClass, paramObj, apiVo, JSONObject.class, JSONReader.class);
                validIsReSubmit(targetClass, apiVo.getToken(), paramObj, JSONObject.class, JSONReader.class);
                Method method = proxy.getClass().getMethod("myDoService", JSONObject.class, JSONReader.class);
                result = method.invoke(proxy, paramObj, jsonReader);
            } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
                validApi(this.getClass(), paramObj, apiVo, JSONObject.class, JSONReader.class);
                validIsReSubmit(this.getClass(), apiVo.getToken(), paramObj, JSONObject.class, JSONReader.class);
                result = myDoService(paramObj, jsonReader);
            } catch (Exception ex) {
                if (ex.getCause() != null && ex.getCause() instanceof ApiRuntimeException) {
                    throw new ApiRuntimeException(ex.getCause().getMessage(), ex.getCause());
                } else {
                    throw ex;
                }
            }
        } catch (Exception e) {
            error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            if (apiVo.getNeedAudit() != null && apiVo.getNeedAudit().equals(1)) {
                saveAudit(apiVo, paramObj, result, error, startTime, endTime);
            }
        }
        return result;
    }

    /*public final String getId() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }*/

    @Override
    public final JSONObject help() {
        return getApiComponentHelp(JSONObject.class, JSONReader.class);
    }

}
