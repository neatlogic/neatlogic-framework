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

package neatlogic.framework.healthcheck;

import neatlogic.framework.dao.plugin.SqlCostInterceptor;
import neatlogic.framework.dto.healthcheck.RequestSqlAuditVo;
import neatlogic.framework.dto.healthcheck.SqlAuditVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlAuditManager {
    private static final List<SqlAuditVo> sqlAuditList = new ArrayList<>();
    // URL监控按HTTP请求维度独立缓存，避免和原SQL ID明细表互相混用
    private static final List<RequestSqlAuditVo> requestSqlAuditList = new ArrayList<>();
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

    public static void addRequestSqlAudit(RequestSqlAuditVo requestSqlAuditVo) {
        synchronized (requestSqlAuditList) {
            for (RequestSqlAuditVo requestSqlAudit : requestSqlAuditList) {
                if (Objects.equals(requestSqlAudit.getId(), requestSqlAuditVo.getId())) {
                    return;
                }
            }
            // URL监控同样限制最大缓存条数，避免长时间开启监控导致内存持续增长
            if (requestSqlAuditList.size() == MAX_SIZE) {
                requestSqlAuditList.remove(0);
            }
            RequestSqlAuditVo requestSqlAudit = new RequestSqlAuditVo(requestSqlAuditVo.getId(), requestSqlAuditVo.getUrl(), requestSqlAuditVo.getThreadName());
            for (RequestSqlAuditVo.SameIdSqlAuditVo sameIdSqlAuditVo : requestSqlAuditVo.getSameIdSqlAuditList()) {
                for (SqlAuditVo sqlAuditVo : sameIdSqlAuditVo.getSqlAuditList()) {
                    requestSqlAudit.addSqlAudit(sqlAuditVo);
                }
            }
            RequestSqlAuditVo.countAndSort(requestSqlAudit);
            requestSqlAuditList.add(requestSqlAudit);
        }
    }

    public static void removeRequestSqlAudit(String url) {
        synchronized (requestSqlAuditList) {
            if (SqlCostInterceptor.UrlMap.getUrlList().contains("*")) {
                return;
            }
            // 删除URL监控项后，清理已经不在监控列表中的请求级审计记录
            requestSqlAuditList.removeIf(d -> !SqlCostInterceptor.UrlMap.getUrlList().contains(d.getUrl()));
        }
    }

    public static void clearRequestSqlAudit() {
        synchronized (requestSqlAuditList) {
            requestSqlAuditList.clear();
        }
    }

    public static List<RequestSqlAuditVo> getRequestSqlAuditList() {
        return requestSqlAuditList;
    }
}
