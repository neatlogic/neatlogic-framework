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

package neatlogic.framework.dao.node;

import neatlogic.framework.asynchronization.threadlocal.LicensePolicyContext;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.SqlNode;

public class LicensePolicySqlNode implements SqlNode {
    private final String licenseType;
    private final String column;

    public LicensePolicySqlNode(String licenseType, String column) {
        this.licenseType = licenseType;
        this.column = column;
    }

    @Override
    public boolean apply(DynamicContext context) {
        if (LicensePolicyContext.get() != null && MapUtils.isNotEmpty(LicensePolicyContext.get().getSqlPolicyValue()) && LicensePolicyContext.get().getSqlPolicyValue().get(licenseType) != null) {
            context.appendSql(String.format("and %s <= %d", column, LicensePolicyContext.get().getSqlPolicyValue().get(licenseType)));
        }
        return true;
    }
}
