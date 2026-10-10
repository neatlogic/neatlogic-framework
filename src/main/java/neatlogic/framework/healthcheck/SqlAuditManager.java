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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.ToLongFunction;

/**
 * 保存当前进程的SQL监控记录，并统一控制两类记录的保留策略。
 */
public class SqlAuditManager {
    public static final String SAVE_MODE_RECENT = "recent";
    public static final String SAVE_MODE_SLOWEST = "slowest";
    private static final Object AUDIT_LOCK = new Object();
    private static final List<SqlAuditVo> sqlAuditList = new ArrayList<>();
    // URL监控按HTTP请求维度独立缓存，避免和原SQL ID明细表互相混用
    private static final List<RequestSqlAuditVo> requestSqlAuditList = new ArrayList<>();
    private static final int MAX_SIZE = 1000;
    private static String saveMode = SAVE_MODE_RECENT;

    /**
     * 保存单次SQL执行记录，重复SQL仍按每次执行分别保留。
     */
    public static void addSqlAudit(SqlAuditVo sqlAuditVo) {
        synchronized (AUDIT_LOCK) {
            addAudit(sqlAuditList, sqlAuditVo, SqlAuditManager::getTimeCost);
        }
    }

    /**
     * 清除已经不在监控列表中的SQL记录，沿用SQL ID末段匹配方式。
     *
     * @param sqlId 已删除的监控项
     */
    public static void removeSqlAudit(String sqlId) {
        synchronized (AUDIT_LOCK) {
            sqlAuditList.removeIf(d -> {
                String id = d.getId().substring(d.getId().lastIndexOf(".") + 1);
                return !SqlCostInterceptor.SqlIdMap.getSqlIdList().contains(id);
            });
        }
    }

    /**
     * 清除SQL明细，保留当前保存模式。
     */
    public static void clearSqlAudit() {
        synchronized (AUDIT_LOCK) {
            sqlAuditList.clear();
        }
    }

    /**
     * 返回独立列表快照，避免调用方排序或删除影响内部记录。
     */
    public static List<SqlAuditVo> getSqlAuditList() {
        synchronized (AUDIT_LOCK) {
            return copyAuditList(sqlAuditList, SqlAuditManager::getTimeCost);
        }
    }

    /**
     * 请求完成后先计算全部SQL统计，再按请求累计SQL耗时参与保留比较。
     */
    public static void addRequestSqlAudit(RequestSqlAuditVo requestSqlAuditVo) {
        synchronized (AUDIT_LOCK) {
            for (RequestSqlAuditVo requestSqlAudit : requestSqlAuditList) {
                if (Objects.equals(requestSqlAudit.getId(), requestSqlAuditVo.getId())) {
                    return;
                }
            }
            RequestSqlAuditVo requestSqlAudit = RequestSqlAuditVo.newInstanceAndCountAndSort(requestSqlAuditVo);
            addAudit(requestSqlAuditList, requestSqlAudit, RequestSqlAuditVo::getTotalTimeCost);
        }
    }

    /**
     * 删除URL监控项后，清理已不在监控范围内的请求记录。
     */
    public static void removeRequestSqlAudit(String url) {
        synchronized (AUDIT_LOCK) {
            if (SqlCostInterceptor.UrlMap.getUrlList().contains("*")) {
                return;
            }
            requestSqlAuditList.removeIf(d -> !SqlCostInterceptor.UrlMap.getUrlList().contains(d.getUrl()));
        }
    }

    /**
     * 清除请求记录，保留当前保存模式。
     */
    public static void clearRequestSqlAudit() {
        synchronized (AUDIT_LOCK) {
            requestSqlAuditList.clear();
        }
    }

    /**
     * 原子清除两类记录，保留当前保存模式。
     */
    public static void clearAllSqlAudit() {
        synchronized (AUDIT_LOCK) {
            sqlAuditList.clear();
            requestSqlAuditList.clear();
        }
    }

    /**
     * 返回请求记录的独立列表快照。
     */
    public static List<RequestSqlAuditVo> getRequestSqlAuditList() {
        synchronized (AUDIT_LOCK) {
            return copyAuditList(requestSqlAuditList, RequestSqlAuditVo::getTotalTimeCost);
        }
    }

    /**
     * 切换当前进程的保存模式，已有记录及写入顺序均保持不变。
     */
    public static String updateSaveMode(String mode) {
        synchronized (AUDIT_LOCK) {
            if (!SAVE_MODE_RECENT.equals(mode) && !SAVE_MODE_SLOWEST.equals(mode)) {
                throw new IllegalArgumentException("不支持的SQL保存模式：" + mode);
            }
            saveMode = mode;
            return saveMode;
        }
    }

    /**
     * 获取当前进程的保存模式。
     */
    public static String getSaveMode() {
        synchronized (AUDIT_LOCK) {
            return saveMode;
        }
    }

    /**
     * 在同一锁内复制模式与两类记录，保证一次查询读取一致状态。
     */
    public static Snapshot getSnapshot() {
        synchronized (AUDIT_LOCK) {
            return new Snapshot(saveMode, copyAuditList(sqlAuditList, SqlAuditManager::getTimeCost),
                    copyAuditList(requestSqlAuditList, RequestSqlAuditVo::getTotalTimeCost));
        }
    }

    /**
     * 列表始终保持写入顺序；慢SQL模式满额时只淘汰最短且最早的记录。
     */
    private static <T> void addAudit(List<T> list, T audit, ToLongFunction<T> timeCost) {
        if (list.size() >= MAX_SIZE) {
            int removeIndex = 0;
            if (SAVE_MODE_SLOWEST.equals(saveMode)) {
                long minimumTimeCost = timeCost.applyAsLong(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    long currentTimeCost = timeCost.applyAsLong(list.get(i));
                    // 相同耗时不移动索引，淘汰同耗时记录中最早写入的一条。
                    if (currentTimeCost < minimumTimeCost) {
                        minimumTimeCost = currentTimeCost;
                        removeIndex = i;
                    }
                }
                if (timeCost.applyAsLong(audit) < minimumTimeCost) {
                    return;
                }
            }
            list.remove(removeIndex);
        }
        list.add(audit);
    }

    /**
     * 仅对复制后的列表排序；先反转写入顺序，使稳定排序在同耗时下优先展示新记录。
     */
    private static <T> List<T> copyAuditList(List<T> list, ToLongFunction<T> timeCost) {
        List<T> copy = new ArrayList<>(list);
        if (SAVE_MODE_SLOWEST.equals(saveMode)) {
            Collections.reverse(copy);
            copy.sort((first, second) -> Long.compare(timeCost.applyAsLong(second), timeCost.applyAsLong(first)));
        }
        return copy;
    }

    /**
     * 未设置耗时的记录按零耗时处理，避免影响监控记录保存。
     */
    private static long getTimeCost(SqlAuditVo sqlAuditVo) {
        if (sqlAuditVo.getTimeCost() != null) {
            return sqlAuditVo.getTimeCost();
        }
        return 0;
    }

    /**
     * 一次查询对应的模式与两类记录快照，列表修改不影响内部缓存。
     */
    public static class Snapshot {
        private final String saveMode;
        private final List<SqlAuditVo> sqlAuditList;
        private final List<RequestSqlAuditVo> requestSqlAuditList;

        private Snapshot(String saveMode, List<SqlAuditVo> sqlAuditList, List<RequestSqlAuditVo> requestSqlAuditList) {
            this.saveMode = saveMode;
            this.sqlAuditList = sqlAuditList;
            this.requestSqlAuditList = requestSqlAuditList;
        }

        public String getSaveMode() {
            return saveMode;
        }

        public List<SqlAuditVo> getSqlAuditList() {
            return sqlAuditList;
        }

        public List<RequestSqlAuditVo> getRequestSqlAuditList() {
            return requestSqlAuditList;
        }
    }
}
