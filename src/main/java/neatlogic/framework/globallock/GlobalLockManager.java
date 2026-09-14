/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.globallock;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.globallock.constvalue.*;
import neatlogic.framework.common.RootConfiguration;
import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.globallock.exception.GlobalLockIdentityMismatchException;
import neatlogic.framework.globallock.exception.GlobalLockReleaseException;
import neatlogic.framework.util.$;
import neatlogic.framework.globallock.core.GlobalLockHandlerFactory;
import neatlogic.framework.globallock.core.IGlobalLockHandler;
import neatlogic.framework.globallock.dao.mapper.GlobalLockMapper;
import neatlogic.framework.util.SnowflakeUtil;
import neatlogic.framework.exception.type.ParamIrregularException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.alibaba.fastjson.JSONArray;
import javax.annotation.Resource;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** 管理全局锁的事务、批量释放、提交后通知及异常记录。 */
@Service
@RootConfiguration
public class GlobalLockManager {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLockManager.class);

    private static GlobalLockMapper globalLockMapper;
    private static GlobalLockOperationManager operations;

    @Resource
    public void setOperations(GlobalLockOperationManager manager) { operations = manager; }


    @Autowired
    public GlobalLockManager(GlobalLockMapper _globalLockMapper) {
        globalLockMapper = _globalLockMapper;
    }

    /**
     * 加入锁队列
     *
     * @param globalLockVo 锁入参
     */
    public static void insertLock(GlobalLockVo globalLockVo) {
        final Long requestedId = globalLockVo.getId();
        GlobalLockTransaction.execute(() -> {
            // 每次完整重试均恢复请求身份，不能沿用上一次未提交的复用结果。
            globalLockVo.setId(requestedId);
            globalLockVo.setIsLock(0);
            globalLockVo.setWaitReason(null);
            GlobalLockHandlerFactory.getHandler(globalLockVo.getHandler()).validateAcquisition(globalLockVo);
            // 已存在的协调行直接取排他锁，避免并发 INSERT IGNORE 先持有共享锁再升级造成死锁。
            if (globalLockMapper.getGlobalLockPkByUuidForUpdate(globalLockVo.getUuid()) == null) {
                globalLockMapper.insertLockPk(globalLockVo.getUuid());
                globalLockMapper.getGlobalLockPkByUuidForUpdate(globalLockVo.getUuid());
            }
            // 以实际插入行数判定临时记录归属，复用已读取队列校验身份，减少一次往返。
            boolean inserted = globalLockMapper.insertLockIfAbsent(globalLockVo) == 1;
            List<GlobalLockVo> queue = globalLockMapper.getGlobalLockByUuid(globalLockVo.getUuid());
            GlobalLockVo existing = queue.stream().filter(item -> Objects.equals(item.getId(), requestedId)).findFirst().orElse(null);
            if (existing == null) {
                // 相同 ID 异常关联其他资源时才额外按主键读取，不能绕过身份校验。
                existing = globalLockMapper.getGlobalLockById(requestedId);
                if (existing == null) throw new ParamIrregularException("lockId", $.t("globallock.error.enqueuemissing",
                        requestedId, globalLockVo.getKey(), globalLockVo.getUuid(), globalLockVo.getHandler()));
            }
            validateIdentity(existing, globalLockVo);
            if (!GlobalLockHandlerFactory.getHandler(globalLockVo.getHandler()).getIsCanInsertLock(queue, globalLockVo)) {
                // 仅撤销当前事务实际创建的临时记录，不能回滚调用方事务。
                if (inserted) globalLockMapper.deleteLock(requestedId);
            }
        });
    }

    /**
     * 获取锁
     *
     * @param globalLockVo 锁入参
     * @return lockId
     */
    public static GlobalLockVo getLock(GlobalLockVo globalLockVo) {
        //预先加入锁队列
        insertLock(globalLockVo);
        if (globalLockVo.getIsLock() != 1) {
            lock(globalLockVo);
        }
        return globalLockVo;
    }

    /**
     * 锁
     *
     * @param globalLockVo 锁入参
     * @return lockId
     */
    private static GlobalLockVo lock(GlobalLockVo globalLockVo) {
        GlobalLockTransaction.execute(() -> {
            globalLockVo.setIsLock(0);
            globalLockVo.setWaitReason(null);
            GlobalLockHandlerFactory.getHandler(globalLockVo.getHandler()).validateAcquisition(globalLockVo);
            globalLockMapper.getGlobalLockPkByUuidForUpdate(globalLockVo.getUuid());
            // 入队到获锁之间可能发生取消，不能将已删除的记录返回为已获锁。
            List<GlobalLockVo> queue = globalLockMapper.getGlobalLockByUuid(globalLockVo.getUuid());
            GlobalLockVo current = queue.stream().filter(item -> Objects.equals(item.getId(), globalLockVo.getId())).findFirst().orElse(null);
            globalLockVo.setIsLock(0);
            if (current != null) validateIdentity(current, globalLockVo);
            if (current != null && current.getIsLock() == 1) {
                globalLockVo.setIsLock(1);
                return;
            }
            if (current != null && GlobalLockHandlerFactory.getHandler(globalLockVo.getHandler())
                    .getIsCanLock(queue, globalLockVo)) {
                if (globalLockMapper.updateToLockById(globalLockVo.getId()) == 1) {
                    globalLockVo.setIsLock(1);
                }
            }
        });
        return globalLockVo;
    }

    /**
     * 取消等待锁
     *
     * @param lockId 锁id
     */
    public static void cancelLock(Long lockId) {
        release(lockId, new JSONObject(), false);
    }

    /** 释放持有锁，等待最外层事务提交后再通知等待者。 */
    public static void unLock(Long lockId, JSONObject paramJson) {
        release(lockId, paramJson, true);
    }

    /** 执行交互式解锁，进度操作须已绑定用户并取得执行权。 */
    public static void release(Long lockId, JSONObject param, boolean notify) {
        JSONObject progress = param.getString("operationId") == null ? null : operations.get(param.getString("operationId"));
        if (progress != null) {
            GlobalLockVo original = globalLockMapper.getGlobalLockById(lockId);
            progress.put("releaseStatus", GlobalLockReleaseStatus.RELEASING.getValue());
            if (original != null) progress.put("lock", GlobalLockOperationManager.identity(original));
            operations.save(progress, GlobalLockOperationStatus.RUNNING);
        }
        long revision = SnowflakeUtil.uniqueLong();
        // 复用事务中已读取的标识供异常诊断，不为错误文案新增数据库查询。
        final GlobalLockVo[] releaseTarget = {null};
        try {
            GlobalLockTransaction.execute(() -> {
                releaseTarget[0] = null;
                if (progress != null) progress.put("releaseStatus", GlobalLockReleaseStatus.RELEASING.getValue());
                GlobalLockVo candidate = globalLockMapper.getGlobalLockById(lockId);
                releaseTarget[0] = candidate;
                if (candidate != null) globalLockMapper.getGlobalLockPkByUuidForUpdate(candidate.getUuid());
                // 未找到记录时本轮幂等结束，不能删除未受协调行锁保护的迟到插入。
                GlobalLockVo current = candidate == null ? null : globalLockMapper.getGlobalLockById(lockId);
                if (current != null) {
                    // 防止同一 ID 的异常复用导致删除未受当前协调锁保护的其他资源。
                    if (!Objects.equals(candidate.getUuid(), current.getUuid())) {
                        throw new GlobalLockIdentityMismatchException(candidate, current, "uuid", candidate.getUuid(), current.getUuid());
                    }
                    globalLockMapper.deleteLock(lockId);
                }
                if (progress != null) {
                    progress.put("releaseStatus", GlobalLockReleaseStatus.RELEASED.getValue());
                    operations.committedRelease(progress);
                }
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override public void afterCommit() {
                        // 通知异常不能改变已经提交成功的删除结果。
                        try {
                            if (notify && current != null && current.getIsLock() == 1) notifyWaiters(current.getUuid(), param, progress);
                            if (progress != null) progress.put("finished", true);
                            operations.save(progress, GlobalLockOperationStatus.DONE);
                        } catch (Exception ex) {
                            logger.error("Post-commit release processing failed, lockId={}", lockId, ex);
                            if (progress != null) progress.put("notificationError", errorText(ex));
                            operations.save(progress, GlobalLockOperationStatus.DONE);
                        }
                    }
                    @Override public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK && progress != null) {
                            progress.put("releaseStatus", GlobalLockReleaseStatus.FAILED.getValue());
                            operations.save(progress, GlobalLockOperationStatus.FAILED);
                        }
                    }
                });
            });
        } catch (Exception ex) {
            logger.error("Release lock {} failed", lockId, ex);
            // 已知业务异常直接透传；未知异常补全操作上下文并保留 cause，进度与接口使用同一诊断信息。
            ApiRuntimeException failure = ex instanceof ApiRuntimeException ? (ApiRuntimeException) ex
                    : new GlobalLockReleaseException(lockId, releaseTarget[0], param.getString("operationId"), notify ? "unlock" : "cancel", ex);
            recordAfterRollback(Collections.singletonList(lockId), failure, revision);
            if (progress != null) { progress.put("releaseStatus", GlobalLockReleaseStatus.FAILED.getValue()); progress.put("releaseError", errorText(failure)); }
            // 外层事务结束后，才能在独立事务中更新同一锁记录。
            afterRollback(() -> operations.save(progress, GlobalLockOperationStatus.FAILED));
            throw failure;
        }
    }

    /** 加入调用方事务，按处理器及归属批量释放；调用方先保护业务生命周期。 */
    public static void releaseOwnedLocks(List<String> handlers, String ownerId) {
        if (handlers == null || handlers.isEmpty() || ownerId == null) {
            throw new ParamIrregularException("handlers/ownerId", $.t("globallock.error.ownerrequired", handlers, ownerId));
        }
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new ParamIrregularException("transaction", $.t("globallock.error.transactionrequired", handlers, ownerId));
        }
        List<GlobalLockVo> owned = globalLockMapper.getOwnerLockCandidates(handlers, ownerId).stream()
                .filter(lock -> handlers.contains(lock.getHandler()) && ownsLock(lock, ownerId)).collect(Collectors.toList());
        List<Long> ids = owned.stream().map(GlobalLockVo::getId).collect(Collectors.toList());
        List<String> resources = owned.stream().map(GlobalLockVo::getUuid).distinct().sorted().collect(Collectors.toList());
        long revision = SnowflakeUtil.uniqueLong();
        try {
            for (String uuid : resources) globalLockMapper.getGlobalLockPkByUuidForUpdate(uuid);
            // 获得资源行锁后重新检查，记录可能已被并发的手动取消操作删除。
            for (Long id : ids) {
                GlobalLockVo lock = globalLockMapper.getGlobalLockById(id);
                if (lock != null && ownsLock(lock, ownerId)) globalLockMapper.deleteLock(id);
            }
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    for (String uuid : resources) {
                        try { notifyWaiters(uuid, new JSONObject(), null); }
                        catch (Exception ex) { logger.error("Notify after owner cleanup ownerId={}, uuid={}", ownerId, uuid, ex); }
                    }
                }
            });
        } catch (Exception ex) {
            logger.error("Owner lock cleanup ownerId={} failed", ownerId, ex);
            recordAfterRollback(ids, ex, revision);
            throw ex;
        }
    }

    /** 由业务处理器确认归属，元数据异常时不得猜测并删除。 */
    private static boolean ownsLock(GlobalLockVo lock, String ownerId) {
        try { return GlobalLockHandlerFactory.getHandler(lock.getHandler()).ownsLock(lock, ownerId); }
        catch (Exception ex) { logger.error("Invalid lock metadata id={}", lock.getId(), ex); return false; }
    }

    /** 每个候选等待者最多通知一次，失败保留记录，首个通知成功后停止。 */
    private static void notifyWaiters(String uuid, JSONObject param, JSONObject progress) {
        List<GlobalLockVo> candidates = globalLockMapper.getWaitingLocks(uuid);
        JSONArray entries = new JSONArray();
        for (GlobalLockVo candidate : candidates) {
            JSONObject item = GlobalLockOperationManager.identity(candidate);
            item.put("status", GlobalLockWaiterStatus.PENDING.getValue()); entries.add(item);
        }
        if (progress != null) { progress.put("waiters", entries); progress.put("total", entries.size()); }
        boolean delivered = false;
        for (int i = 0; i < candidates.size(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            if (delivered) { entry.put("status", GlobalLockWaiterStatus.NOT_NOTIFIED.getValue()); continue; }
            Long revision = null;
            GlobalLockVo candidate = candidates.get(i);
            try {
                GlobalLockVo current = globalLockMapper.getGlobalLockById(candidate.getId());
                if (current == null || current.getIsLock() != 0) { entry.put("status", GlobalLockWaiterStatus.SKIPPED.getValue()); }
                else {
                    revision = nextNotificationRevision(current.getId());
                    if (revision == null) {
                        entry.put("status", GlobalLockWaiterStatus.SKIPPED.getValue());
                        if (progress != null) progress.put("processed", i + 1);
                        operations.save(progress, GlobalLockOperationStatus.RUNNING);
                        continue;
                    }
                    entry.put("status", GlobalLockWaiterStatus.NOTIFYING.getValue()); operations.save(progress, GlobalLockOperationStatus.RUNNING);
                    GlobalLockHandlerFactory.getHandler(current.getHandler()).doNotify(current, param);
                    entry.put("status", GlobalLockWaiterStatus.SUCCESS.getValue()); delivered = true;
                    saveError(current.getId(), true, null, revision);
                }
            } catch (Exception ex) {
                logger.error("Notify failed uuid={}, waiter={}, metadata={}", uuid, candidate.getId(), candidate.getHandlerParamStr(), ex);
                entry.put("status", GlobalLockWaiterStatus.FAILED.getValue()); entry.put("error", errorText(ex));
                if (revision != null) saveError(candidate.getId(), true, ex, revision);
            }
            if (progress != null) progress.put("processed", i + 1);
            operations.save(progress, GlobalLockOperationStatus.RUNNING);
        }
        if (progress != null) {
            boolean failed = entries.stream().anyMatch(item -> GlobalLockWaiterStatus.FAILED.getValue().equals(((JSONObject) item).getString("status")));
            progress.put("notificationStatus", (delivered ? GlobalLockNotificationStatus.SUCCESS : (failed ? GlobalLockNotificationStatus.FAILED : GlobalLockNotificationStatus.EMPTY)).getValue());
        }
        operations.save(progress, GlobalLockOperationStatus.RUNNING);
    }

    /** 网络通知前在数据库中分配顺序号，保证多台 Web 顺序一致，网络调用期间不持有行锁。 */
    private static Long nextNotificationRevision(Long id) {
        TransactionStatus tx = TransactionUtil.openNewTx();
        try {
            // 复核后可能已经获锁，不能为持有者启动一次新的过期通知。
            if (globalLockMapper.incrementNotifyRevision(id) != 1) {
                TransactionUtil.commitTx(tx);
                return null;
            }
            Long revision = globalLockMapper.getNotifyRevision(id);
            TransactionUtil.commitTx(tx);
            return revision;
        } catch (RuntimeException ex) {
            logger.error("Allocate notification revision lockId={} failed", id, ex);
            if (!tx.isCompleted()) TransactionUtil.rollbackTx(tx);
            throw ex;
        }
    }

    /** 生成异常诊断信息，保留发生时间、消息及原始异常堆栈。 */
    private static String errorText(Throwable ex) {
        StringWriter writer = new StringWriter(); ex.printStackTrace(new PrintWriter(writer));
        JSONObject error = new JSONObject(); error.put("time", System.currentTimeMillis());
        error.put("message", ex.getMessage()); error.put("detail", writer.toString());
        return error.toJSONString();
    }

    /** 持有资源行锁的事务结束后，才能独立写入异常信息。 */
    private static void recordAfterRollback(List<Long> ids, Throwable ex, long revision) {
        afterRollback(() -> { for (Long id : ids) saveError(id, false, ex, revision); });
    }

    /** 存在外层事务时，将异常记录工作延迟到事务完成回滚后。 */
    private static void afterRollback(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCompletion(int status) { if (status == STATUS_ROLLED_BACK) action.run(); }
            });
        } else action.run();
    }

    /** 按尝试顺序更新异常，保存失败只记日志，绝不重新插入已删除的锁。 */
    private static void saveError(Long id, boolean notify, Throwable error, long revision) {
        TransactionStatus tx = null;
        try {
            tx = TransactionUtil.openNewTx();
            globalLockMapper.updateLockError(id, notify, error == null ? null : errorText(error), revision);
            TransactionUtil.commitTx(tx);
        } catch (Exception ex) {
            logger.error("Persist lock error id={} failed", id, ex);
            if (tx != null && !tx.isCompleted()) TransactionUtil.rollbackTx(tx);
        }
    }

    /**
     * 重试获取锁
     *
     * @param globalLockVo 锁 id
     */
    public static GlobalLockVo retryLock(GlobalLockVo globalLockVo) {
        GlobalLockVo globalLockTmp = globalLockMapper.getGlobalLockById(globalLockVo.getId());
        if (globalLockTmp == null) {
            insertLock(globalLockVo);
        } else {
            validateIdentity(globalLockTmp, globalLockVo);
        }
        return lock(globalLockVo);
    }

    /** 相同 ID 的幂等请求也必须属于原资源和执行实例，不能覆盖其他实例。 */
    private static void validateIdentity(GlobalLockVo existing, GlobalLockVo request) {
        if (!Objects.equals(existing.getUuid(), request.getUuid())) {
            throw new GlobalLockIdentityMismatchException(existing, request, "uuid", existing.getUuid(), request.getUuid());
        }
        if (!Objects.equals(existing.getHandler(), request.getHandler())) {
            throw new GlobalLockIdentityMismatchException(existing, request, "handler", existing.getHandler(), request.getHandler());
        }
        GlobalLockHandlerFactory.getHandler(request.getHandler()).validateIdentity(existing, request);
    }


    public static boolean getIsBeenLocked(GlobalLockVo globalLockVo) {
        return globalLockMapper.getGlobalLockByUuid(globalLockVo.getUuid()).stream().anyMatch(o -> Objects.equals(o.getIsLock(), 1));
    }

    public static JSONObject searchGlobalLock(GlobalLockVo globalLockVo) {
        IGlobalLockHandler globalLockHandler = GlobalLockHandlerFactory.getHandler(globalLockVo.getHandler());
        globalLockHandler.initSearchParam(globalLockVo);
        List<GlobalLockVo> globalLockVoList = new ArrayList<>();
        int count = globalLockMapper.getLockCount(globalLockVo);
        if (count > 0) {
            globalLockVo.setRowNum(count);
            globalLockVoList = globalLockMapper.searchLock(globalLockVo);
        }
        return globalLockHandler.getSearchResult(globalLockVoList, globalLockVo);
    }

}
