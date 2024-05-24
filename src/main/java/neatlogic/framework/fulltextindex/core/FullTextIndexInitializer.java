/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        String moduleId = context.getId();
        if (FULLTEXT_INDEX_MODULE_MAP.contains(moduleId)) {
            CachedThreadPool.execute(new NeatLogicThread("FULLTEXT-INDEX-SCHEMA-BUILDER") {
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
