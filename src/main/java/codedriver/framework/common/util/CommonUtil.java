package codedriver.framework.common.util;

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

}
