/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.store.mysql;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class CodeDriverRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        if (TenantContext.get() != null && StringUtils.isNotBlank(TenantContext.get().getTenantUuid())) {
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
