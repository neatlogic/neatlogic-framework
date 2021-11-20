/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.healthcheck;

import codedriver.framework.dao.plugin.SqlCostInterceptor;
import codedriver.framework.dto.healthcheck.SqlAuditVo;

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
