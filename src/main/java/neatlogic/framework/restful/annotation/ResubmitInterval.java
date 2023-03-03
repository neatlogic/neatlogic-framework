package neatlogic.framework.restful.annotation;

import java.lang.annotation.*;

/**
 * @Title: ResubmitInterval
 * @Package: neatlogic.framework.restful.annotation
 * @Description: 重复提交限制
 * @author: chenqiwei
 * @date: 2021/3/18:51 上午
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResubmitInterval {
    int value();
}
