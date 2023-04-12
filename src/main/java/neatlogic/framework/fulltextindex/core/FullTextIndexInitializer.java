/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        FULLTEXTINDEX_MODULE_MAP.add("rdm");
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        String moduleId = context.getId();
        if (FULLTEXTINDEX_MODULE_MAP.contains(moduleId)) {
            CachedThreadPool.execute(new NeatLogicThread("FULLTEXTINDEX-SCHEMA-BUILDER") {
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
