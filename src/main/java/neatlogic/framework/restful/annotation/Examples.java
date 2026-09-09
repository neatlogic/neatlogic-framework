package neatlogic.framework.restful.annotation;

import java.lang.annotation.*;

/** 可重复调用示例的容器，接口通常直接重复声明 @Example。 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Examples {
    /** 按源码声明顺序排列的场景。 */
    Example[] value();
}
