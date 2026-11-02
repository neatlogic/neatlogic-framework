package neatlogic.framework.auth.core;

import java.lang.annotation.*;

/** 每个具体权限标签必须独立声明所属模块，不能从父类继承或由包名推导。 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthModule {
    /** 返回权限所属模块标识，与权限组可以不同。 */
    String value();
}
