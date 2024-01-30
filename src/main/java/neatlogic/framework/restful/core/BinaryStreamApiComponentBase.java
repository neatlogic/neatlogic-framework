package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.CacheControlType;
import neatlogic.framework.dto.api.CacheControlVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiVo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public abstract class BinaryStreamApiComponentBase extends ApiValidateAndHelpBase implements MyBinaryStreamApiComponent {
    // private static Logger logger =
    // LoggerFactory.getLogger(BinaryStreamApiComponentBase.class);

    @Autowired
    private ApiMapper apiMapper;

    public int needAudit() {
        return 0;
    }

    @Override
    public final Object doService(ApiVo apiVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String error = "";
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {
            try {
                Object proxy = AopContext.currentProxy();
                Class<?> targetClass = AopUtils.getTargetClass(proxy);
                validApi(targetClass, paramObj, apiVo, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
                validIsReSubmit(targetClass, apiVo.getToken(), paramObj, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
                Method method = proxy.getClass().getMethod("myDoService", JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
                //设置Cache-Control，如果下载失败会在ApiDispatcher最后清除这个header
                CacheControlVo cacheControlVo = getCacheControl(JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
                if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
                    response.setHeader("Cache-Control", "max-age=" + cacheControlVo.getMaxAge());
                }
                result = method.invoke(proxy, paramObj, request, response);

            } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
                validApi(this.getClass(), paramObj, apiVo, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
                validIsReSubmit(this.getClass(), apiVo.getToken(), paramObj, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
                //设置Cache-Control，如果下载失败会在ApiDispatcher最后清除这个header
                CacheControlVo cacheControlVo = getCacheControl(JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
                if (cacheControlVo != null && cacheControlVo.getCacheControlType() != null) {
                    response.setHeader("Cache-Control", "max-age=" + cacheControlVo.getMaxAge());
                }
                result = myDoService(paramObj, request, response);

            } catch (Exception ex) {
                if (ex.getCause() != null && ex.getCause() instanceof ApiRuntimeException) {
                    throw new ApiRuntimeException(ex.getCause().getMessage(), ex.getCause());
                } else {
                    throw ex;
                }
            }
        } catch (Exception e) {
            response.setHeader("Cache-Control", CacheControlType.NOCACHE.getValue());//下次还需验证
            error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            ApiVo apiConfigVo = apiMapper.getApiByToken(apiVo.getToken());
            // 如果没有配置，则使用默认配置
            if (apiConfigVo == null) {
                apiConfigVo = apiVo;
            }
            if (apiConfigVo.getNeedAudit() != null && apiConfigVo.getNeedAudit().equals(1)) {
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
        return getApiComponentHelp(JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
    }

}
