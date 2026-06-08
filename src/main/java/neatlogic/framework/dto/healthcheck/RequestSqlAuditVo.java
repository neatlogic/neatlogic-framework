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

package neatlogic.framework.dto.healthcheck;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RequestSqlAuditVo implements Serializable {
    private Long id;
    // URL监控按一次HTTP请求聚合，这里保存当前请求的URL用于前端展示和搜索
    private String url;
    // URL监控表格需要展示请求发生时间，取当前请求第一条SQL的执行时间
    private Date runTime;
    // URL监控表格需要展示租户、用户和线程，便于定位具体请求来源
    private String tenant;
    private String userId;
    private String threadName;
    // URL监控表格需要展示本次请求执行了多少条SQL
    private int sqlCount = 0;
    // 没有使用缓存的总耗时
    private long notUseCacheTotalTimeCost = 0;
    // 总耗时
    private long totalTimeCost = 0;
    // 相同IDSql审计列表
    private final List<SameIdSqlAuditVo> sameIdSqlAuditList = Collections.synchronizedList(new ArrayList<>());

    public RequestSqlAuditVo(Long id, String url, String threadName) {
        this.id = id;
        this.url = url;
        this.threadName = threadName;
    }

    public Long getId() {
        return id;
    }

    public long getNotUseCacheTotalTimeCost() {
        return this.notUseCacheTotalTimeCost;
    }

    public void setNotUseCacheTotalTimeCost(long notUseCacheTotalTimeCost) {
        this.notUseCacheTotalTimeCost = notUseCacheTotalTimeCost;
    }

    public long getTotalTimeCost() {
        return this.totalTimeCost;
    }

    public void setTotalTimeCost(long totalTimeCost) {
        this.totalTimeCost = totalTimeCost;
    }

    public List<SameIdSqlAuditVo> getSameIdSqlAuditList() {
        return this.sameIdSqlAuditList;
    }

    public synchronized void addSqlAudit(SqlAuditVo sqlAuditVo) {
        if (sqlAuditVo != null) {
            // 第一次追加SQL时同步基础信息，确保URL监控表格每行都是完整的一次请求记录
            if (this.runTime == null) {
                this.runTime = sqlAuditVo.getRunTime();
            }
            if (StringUtils.isBlank(this.tenant) && StringUtils.isNotBlank(sqlAuditVo.getTenant())) {
                this.tenant = sqlAuditVo.getTenant();
            }
            if (StringUtils.isBlank(this.userId) && StringUtils.isNotBlank(sqlAuditVo.getUserId())) {
                this.userId = sqlAuditVo.getUserId();
            }
            SameIdSqlAuditVo sameIdSqlAuditVo = null;
            for (SameIdSqlAuditVo sameIdSqlAudit : this.sameIdSqlAuditList) {
                if (Objects.equals(sameIdSqlAudit.getId(), sqlAuditVo.getId())) {
                    sameIdSqlAuditVo = sameIdSqlAudit;
                }
            }
            if (sameIdSqlAuditVo == null) {
                sameIdSqlAuditVo = new SameIdSqlAuditVo(sqlAuditVo.getId());//, timeCost, notUseCacheTimeCost, notUseCacheCount, sqlAuditVo.getUseCacheLevel(), sqlAuditVo.getSql()
                this.sameIdSqlAuditList.add(sameIdSqlAuditVo);
            }
            sameIdSqlAuditVo.getSqlAuditList().add(sqlAuditVo);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getSqlCount() {
        return sqlCount;
    }

    public void setSqlCount(int sqlCount) {
        this.sqlCount = sqlCount;
    }

    public static class SameIdSqlAuditVo implements Serializable {
        // sqlID
        private final String id;
        // 耗时列表
        private final List<Long> timeCostList = Collections.synchronizedList(new ArrayList<>());
        // 总耗时
        private long totalTimeCost = 0;
        // 没有使用缓存的总耗时
        private long notUseCacheTotalTimeCost = 0;
        // 没有使用缓存的次数
        private int notUseCacheCount = 0;
        // 是否使用缓存列表
        private final List<String> useCacheLevelList = Collections.synchronizedList(new ArrayList<>());
        // sql语句列表
        private final List<String> sqlList = Collections.synchronizedList(new ArrayList<>());

        private final List<SqlAuditVo> sqlAuditList = Collections.synchronizedList(new ArrayList<>());

        public SameIdSqlAuditVo(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public List<Long> getTimeCostList() {
            return timeCostList;
        }

        public long getTotalTimeCost() {
            return totalTimeCost;
        }

        public void setTotalTimeCost(long totalTimeCost) {
            this.totalTimeCost = totalTimeCost;
        }

        public long getNotUseCacheTotalTimeCost() {
            return notUseCacheTotalTimeCost;
        }

        public void setNotUseCacheTotalTimeCost(long notUseCacheTotalTimeCost) {
            this.notUseCacheTotalTimeCost = notUseCacheTotalTimeCost;
        }

        public int getNotUseCacheCount() {
            return notUseCacheCount;
        }

        public void setNotUseCacheCount(int notUseCacheCount) {
            this.notUseCacheCount = notUseCacheCount;
        }

        public List<String> getUseCacheLevelList() {
            return useCacheLevelList;
        }

        public List<String> getSqlList() {
            return sqlList;
        }

        public List<SqlAuditVo> getSqlAuditList() {
            return sqlAuditList;
        }
    }

    public static RequestSqlAuditVo newInstanceAndCountAndSort(RequestSqlAuditVo requestSqlAuditVo) {
        if (requestSqlAuditVo != null) {
            RequestSqlAuditVo requestSqlAudit = new RequestSqlAuditVo(requestSqlAuditVo.getId(), requestSqlAuditVo.getUrl(), requestSqlAuditVo.getThreadName());
            for (RequestSqlAuditVo.SameIdSqlAuditVo sameIdSqlAuditVo : new ArrayList<>(requestSqlAuditVo.getSameIdSqlAuditList())) {
                for (SqlAuditVo sqlAuditVo : new ArrayList<>(sameIdSqlAuditVo.getSqlAuditList())) {
                    SqlAuditVo sqlAudit = new SqlAuditVo();
                    sqlAudit.setId(sqlAuditVo.getId());
                    sqlAudit.setTenant(sqlAuditVo.getTenant());
                    sqlAudit.setUserId(sqlAuditVo.getUserId());
                    sqlAudit.setTimeCost(sqlAuditVo.getTimeCost());
                    sqlAudit.setSql(sqlAuditVo.getSql());
                    sqlAudit.setRunTime(sqlAuditVo.getRunTime());
                    sqlAudit.setRecordCount(sqlAuditVo.getRecordCount());
                    sqlAudit.setUseCacheLevel(sqlAuditVo.getUseCacheLevel());
                    sqlAudit.setThreadName(sqlAuditVo.getThreadName());
                    requestSqlAudit.addSqlAudit(sqlAudit);
                }
            }
            int sqlCount = 0;
            long totalTimeCost = 0;
            long notUseCacheTotalTimeCost = 0;
            List<RequestSqlAuditVo.SameIdSqlAuditVo> sameIdSqlAuditList = requestSqlAudit.getSameIdSqlAuditList();
            for (RequestSqlAuditVo.SameIdSqlAuditVo sameIdSqlAuditVo : sameIdSqlAuditList) {
                List<SqlAuditVo> sqlAuditList = sameIdSqlAuditVo.getSqlAuditList();
                if (CollectionUtils.isNotEmpty(sqlAuditList)) {
                    sqlCount += sqlAuditList.size();
                    for (SqlAuditVo sqlAuditVo : sqlAuditList) {
                        long timeCost = sqlAuditVo.getTimeCost();
                        totalTimeCost += timeCost;
                        sameIdSqlAuditVo.setTotalTimeCost(sameIdSqlAuditVo.getTotalTimeCost() + timeCost);
                        if (StringUtils.isBlank(sqlAuditVo.getUseCacheLevel())) {
                            sameIdSqlAuditVo.setNotUseCacheTotalTimeCost(sameIdSqlAuditVo.getNotUseCacheTotalTimeCost() + timeCost);
                            sameIdSqlAuditVo.setNotUseCacheCount(sameIdSqlAuditVo.getNotUseCacheCount() + 1);
                            notUseCacheTotalTimeCost += timeCost;
                        }
                        sameIdSqlAuditVo.getTimeCostList().add(timeCost);
                        sameIdSqlAuditVo.getUseCacheLevelList().add(sqlAuditVo.getUseCacheLevel());
                        sameIdSqlAuditVo.getSqlList().add(sqlAuditVo.getSql());
                    }
                }
            }
            sameIdSqlAuditList.sort((o1, o2) -> Long.compare(o2.getTotalTimeCost(), o1.getTotalTimeCost()));
            requestSqlAudit.setSqlCount(sqlCount);
            requestSqlAudit.setTotalTimeCost(totalTimeCost);
            requestSqlAudit.setNotUseCacheTotalTimeCost(notUseCacheTotalTimeCost);
            return requestSqlAudit;
        }
        return null;
    }
}
