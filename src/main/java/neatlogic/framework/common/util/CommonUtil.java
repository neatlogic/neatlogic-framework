package neatlogic.framework.common.util;

import neatlogic.framework.common.constvalue.DeviceType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Laiwt on 2020-07-07.
 */
public class CommonUtil {


    /**
     *  JDK8 去重方法
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static  <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * @Description: 获取访问设备类型
     * @Author: 89770
     * @Date: 2021/2/5 14:24
     * @Params: []
     * @Returns: java.lang.String
     **/
    public static String getDevice(){
        String device = DeviceType.PC.getValue();
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (DeviceType.MOBILE.getValue().equals(request.getHeader("Device"))) {
                device = DeviceType.MOBILE.getValue();
            }
        }
        return device;
    }

}
