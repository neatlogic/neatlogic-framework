package codedriver.framework.restful.annotation;

import java.lang.annotation.*;

/**
 * 用来标识API 豁免license认证
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExemptLicense {

}
