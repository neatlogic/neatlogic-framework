/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.TenantMapper;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.fulltextindex.dao.mapper.FullTextIndexSchemaMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RootComponent
public class FullTextIndexInitializer extends ModuleInitializedListenerBase {
    private static final Set<String> FULLTEXTINDEX_MODULE_MAP = new HashSet<>();
    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private FullTextIndexSchemaMapper fullTextIndexSchemaMapper;

    private List<TenantVo> tenantList = new ArrayList<>();

    @Override
    protected void myInit() {
        tenantList = tenantMapper.getAllActiveTenant();
        FULLTEXTINDEX_MODULE_MAP.add("process");
        FULLTEXTINDEX_MODULE_MAP.add("knowledge");
        FULLTEXTINDEX_MODULE_MAP.add("cmdb");
        FULLTEXTINDEX_MODULE_MAP.add("autoexec");
    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        String moduleId = context.getId();
        if (FULLTEXTINDEX_MODULE_MAP.contains(moduleId)) {
            CachedThreadPool.execute(new CodeDriverThread("FULLTEXTINDEX-SCHEMA-BUILDER") {
                @Override
                protected void execute() {
                    for (TenantVo tenantVo : tenantList) {
                        TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
                        fullTextIndexSchemaMapper.createFullTextIndexTargetTable(moduleId);
                        fullTextIndexSchemaMapper.createFullTextIndexContentTable(moduleId);
                        fullTextIndexSchemaMapper.createFullTextIndexOffsetTable(moduleId);
                        fullTextIndexSchemaMapper.createFullTextIndexFieldTable(moduleId);
                    }
                }
            });

        }
    }
}
