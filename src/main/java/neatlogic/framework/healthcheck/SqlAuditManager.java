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
