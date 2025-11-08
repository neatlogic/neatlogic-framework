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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RequestSqlAuditVo {
    // 没有使用缓存的总耗时
    private long notUseCacheTotalTimeCost = 0;
    // 总耗时
    private long totalTimeCost = 0;
    // 相同IDSql审计列表
    private final List<SameIdSqlAuditVo> sameIdSqlAuditList = Collections.synchronizedList(new ArrayList<>());

    public long getNotUseCacheTotalTimeCost() {
        return this.notUseCacheTotalTimeCost;
    }

    public long getTotalTimeCost() {
        return this.totalTimeCost;
    }

    public List<SameIdSqlAuditVo> getSameIdSqlAuditList() {
        return this.sameIdSqlAuditList;
    }

    public void addSqlAudit(SqlAuditVo sqlAuditVo) {
        if (sqlAuditVo != null) {
            int notUseCacheCount = 0;
            long notUseCacheTimeCost = 0;
            long timeCost = sqlAuditVo.getTimeCost();
            this.totalTimeCost += timeCost;
            if (StringUtils.isBlank(sqlAuditVo.getUseCacheLevel())) {
                notUseCacheCount = 1;
                notUseCacheTimeCost = timeCost;
                this.notUseCacheTotalTimeCost += timeCost;
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
            sameIdSqlAuditVo.setTotalTimeCost(sameIdSqlAuditVo.getTotalTimeCost() + timeCost);
            sameIdSqlAuditVo.setNotUseCacheTotalTimeCost(sameIdSqlAuditVo.getNotUseCacheTotalTimeCost() + notUseCacheTimeCost);
            sameIdSqlAuditVo.setNotUseCacheCount(sameIdSqlAuditVo.getNotUseCacheCount() + notUseCacheCount);
            sameIdSqlAuditVo.getTimeCostList().add(timeCost);
            sameIdSqlAuditVo.getUseCacheLevelList().add(sqlAuditVo.getUseCacheLevel());
            sameIdSqlAuditVo.getSqlList().add(sqlAuditVo.getSql());
        }
    }

    public static class SameIdSqlAuditVo {
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
    }
}
