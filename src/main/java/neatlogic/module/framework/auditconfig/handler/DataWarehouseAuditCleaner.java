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

package neatlogic.module.framework.auditconfig.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auditconfig.core.AuditCleanerBase;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceAuditMapper;
import neatlogic.framework.healthcheck.dao.mapper.DatabaseFragmentMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DataWarehouseAuditCleaner extends AuditCleanerBase {
    @Resource
    private DataWarehouseDataSourceAuditMapper dataWarehouseDataSourceAuditMapper;
    @Resource
    private DatabaseFragmentMapper databaseFragmentMapper;

    @Override
    public String getName() {
        return "DATAWAREHOUSE-AUDIT";
    }

    @Override
    protected void myClean(int dayBefore) {
        dataWarehouseDataSourceAuditMapper.deleteAuditByDayBefore(dayBefore);
        databaseFragmentMapper.rebuildTable(TenantContext.get().getDbName(), "datawarehouse_datasource_audit");
    }
}
