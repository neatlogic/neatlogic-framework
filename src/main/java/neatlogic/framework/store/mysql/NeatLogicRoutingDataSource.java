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

package neatlogic.framework.store.mysql;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class NeatLogicRoutingDataSource extends AbstractRoutingDataSource {
    //private final Logger logger = LoggerFactory.getLogger(NeatLogicRoutingDataSource.class);

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
