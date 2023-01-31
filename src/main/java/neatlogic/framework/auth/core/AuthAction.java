package neatlogic.framework.auth.core;

import java.lang.annotation.*;

@Documented
@Inherited
@Repeatable(AuthActions.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthAction {
	Class<? extends AuthBase> action() ;
}

