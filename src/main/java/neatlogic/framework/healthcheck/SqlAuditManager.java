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

package neatlogic.framework.healthcheck;

import neatlogic.framework.dao.plugin.SqlCostInterceptor;
import neatlogic.framework.dto.healthcheck.SqlAuditVo;

import java.util.ArrayList;
import java.util.List;

public class SqlAuditManager {
    private static final List<SqlAuditVo> sqlAuditList = new ArrayList<>();
    private final static int MAX_SIZE = 1000;

    public static void addSqlAudit(SqlAuditVo sqlAuditVo) {
        synchronized (sqlAuditList) {
            if (sqlAuditList.size() == MAX_SIZE) {
                sqlAuditList.remove(0);
            }
            sqlAuditList.add(sqlAuditVo);
        }
    }

    /**
     * 清除监控记录
     *
     * @param sqlId 这个sqlid是真正的id最后一段
     */
    public static void removeSqlAudit(String sqlId) {
        synchronized (sqlAuditList) {
            sqlAuditList.removeIf(d -> {
                String id = d.getId().substring(d.getId().lastIndexOf(".") + 1);
                return !SqlCostInterceptor.SqlIdMap.getSqlIdList().contains(id);
            });
        }
    }

    public static void clearSqlAudit() {
        synchronized (sqlAuditList) {
            sqlAuditList.clear();
        }
    }

    public static List<SqlAuditVo> getSqlAuditList() {
        return sqlAuditList;
    }
}
