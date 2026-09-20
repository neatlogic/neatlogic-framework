/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 */
package neatlogic.framework.globallock;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;

import java.util.Locale;

/** 为隔离测试提供显式请求语言，不依赖 JVM 全局 Locale。 */
final class GlobalLockI18nFixture implements AutoCloseable {
    private final RequestContext requestContext;

    /** 初始化默认中文请求上下文。 */
    GlobalLockI18nFixture() {
        requestContext = RequestContext.init((RequestContext) null);
        requestContext.setLocale(Locale.CHINESE);
    }

    /** 切换当前测试请求语言。 */
    void setLocale(Locale locale) {
        requestContext.setLocale(locale);
    }

    /** 释放测试线程上下文。 */
    @Override
    public void close() {
        requestContext.release();
    }
}
