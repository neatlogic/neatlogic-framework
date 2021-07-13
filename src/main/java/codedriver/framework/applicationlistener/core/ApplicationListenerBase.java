/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.applicationlistener.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dao.mapper.TenantMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

public abstract class ApplicationListenerBase implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private TenantMapper tenantMapper;


    @PostConstruct
    public final void init() {
        TenantContext.init();
        TenantContext tenantContext = TenantContext.get();
        String tenant = tenantContext.getTenantUuid();
        tenantContext.switchTenant(tenant);
        myInit();
    }

    protected abstract void myInit();

}
