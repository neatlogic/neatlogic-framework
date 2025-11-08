/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.fulltextindex.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexSchemaMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RootComponent
public class FullTextIndexInitializer extends ModuleInitializedListenerBase {
    private static final Set<String> FULLTEXT_INDEX_MODULE_MAP = new HashSet<>();
    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private FullTextIndexSchemaMapper fullTextIndexSchemaMapper;

    private List<TenantVo> tenantList = new ArrayList<>();

    @Override
    protected void myInit() {
        tenantList = tenantMapper.getAllActiveTenant();
        FULLTEXT_INDEX_MODULE_MAP.add("process");
        FULLTEXT_INDEX_MODULE_MAP.add("knowledge");
        FULLTEXT_INDEX_MODULE_MAP.add("cmdb");
        FULLTEXT_INDEX_MODULE_MAP.add("autoexec");
        FULLTEXT_INDEX_MODULE_MAP.add("rdm");
        FULLTEXT_INDEX_MODULE_MAP.add("framework");
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        String moduleId = context.getId();
        if (FULLTEXT_INDEX_MODULE_MAP.contains(moduleId)) {
            CachedThreadPool.execute(new NeatLogicThread("FULLTEXT-INDEX-SCHEMA-BUILDER") {
                @Override
                protected void execute() {
                    for (TenantVo tenantVo : tenantList) {
                        TenantContext.get().switchTenant(tenantVo.getUuid());
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
