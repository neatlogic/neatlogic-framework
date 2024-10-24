/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.asynchronization.threadlocal;

import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class LicensePolicyContext {
    private static final ThreadLocal<LicensePolicyContext> instance = new ThreadLocal<>();

    private Map<String,Map<String,Long>> sqlPolicyValue = new HashMap<>();

    public static LicensePolicyContext init( Map<String,Map<String,Long>> sqlPolicyValue ) {
        LicensePolicyContext context = new LicensePolicyContext();
        if (MapUtils.isNotEmpty(sqlPolicyValue)) {
            context.sqlPolicyValue = sqlPolicyValue;
        }
        instance.set(context);
        return context;
    }

    public static LicensePolicyContext get() {
        return instance.get();
    }

    public void release() {
        instance.remove();
    }

    private LicensePolicyContext() {

    }

    public Map<String, Map<String, Long>> getSqlPolicyValue() {
        return sqlPolicyValue;
    }

    public void setSqlPolicyValue(Map<String, Map<String, Long>> sqlPolicyValue) {
        this.sqlPolicyValue = sqlPolicyValue;
    }
}
