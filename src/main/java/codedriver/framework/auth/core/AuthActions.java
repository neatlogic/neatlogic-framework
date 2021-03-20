package codedriver.framework.auth.core;

import java.lang.annotation.*;

/**
 * @Title: AuthActions
 * @Package: codedriver.framework.auth.core
 * @Description: TODO
 * @author: chenqiwei
 * @date: 2021/3/203:19 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthActions {
    AuthAction[] value();
}
