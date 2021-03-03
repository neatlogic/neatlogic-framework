package codedriver.framework.restful.annotation;

import java.lang.annotation.*;

/**
 * @Title: ResubmitInterval
 * @Package: codedriver.framework.restful.annotation
 * @Description: 重复提交限制
 * @author: chenqiwei
 * @date: 2021/3/18:51 上午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResubmitInterval {
    int value();
}
