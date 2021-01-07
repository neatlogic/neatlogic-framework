package codedriver.framework.fullindex.annotation;

import codedriver.framework.elasticsearch.constvalue.ESKeyType;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FullIndexHash {
    ESKeyType type();

    String name();
}
