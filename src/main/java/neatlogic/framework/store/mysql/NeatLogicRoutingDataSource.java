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

package neatlogic.framework.store.mysql;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class NeatLogicRoutingDataSource extends AbstractRoutingDataSource {
    private final Logger logger = LoggerFactory.getLogger(NeatLogicRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        if (TenantContext.get() != null && StringUtils.isNotBlank(TenantContext.get().getTenantUuid())) {
            //logger.debug("ThreadName:{} Tenant:{}", Thread.currentThread().getName(), TenantContext.get().getTenantUuid());
            String key = TenantContext.get().getTenantUuid();
            if (TenantContext.get().isData()) {
                key += "_DATA";
            }
            return key;
        } else {
            return null;
        }
    }
}
