package neatlogic.framework.restful.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 在 myDoService 上重复声明调用场景，供 API 页面及 MCP 模型说明共同使用。 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Examples.class)
public @interface Example {
    /** 场景标题的 i18n key。 */
    String title();

    /** 场景前提、参数组合及替换要求的 i18n key。 */
    String description() default "";

    /** 请求 JSON，对象或数组；不是包含标题的包装结构。 */
    String example();
}
