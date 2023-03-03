package neatlogic.framework.auth.core;

import java.lang.annotation.*;

/**
 * @Title: AuthActions
 * @Package: neatlogic.framework.auth.core
 * @Description: TODO
 * @author: chenqiwei
 * @date: 2021/3/203:19 下午
 **/
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthActions {
    AuthAction[] value();
}
