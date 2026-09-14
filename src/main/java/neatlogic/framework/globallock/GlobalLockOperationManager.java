/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.globallock;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.globallock.constvalue.GlobalLockOperationStatus;
import neatlogic.framework.globallock.constvalue.GlobalLockReleaseStatus;
import neatlogic.framework.globallock.constvalue.GlobalLockWaiterStatus;
import neatlogic.framework.globallock.constvalue.GlobalLockNotificationStatus;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.RootConfiguration;
import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.util.$;
import neatlogic.framework.globallock.dao.mapper.GlobalLockOperationMapper;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;
import neatlogic.framework.globallock.core.GlobalLockHandlerFactory;

/** 保存多台 Web 共享的交互式解锁进度，不保留完整锁快照。 */
@Service
@RootConfiguration
public class GlobalLockOperationManager {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLockOperationManager.class);
    @Resource private GlobalLockOperationMapper mapper;

    /** 创建绑定当前用户的操作记录，此步骤不修改资源锁。 */
    public JSONObject prepare(Long lockId, String action) {
        if (lockId == null || !("unlock".equals(action) || "cancel".equals(action))) {
            throw new ParamIrregularException("lockId/action", $.t("globallock.error.prepareinvalid", lockId, action));
        }
        String id = UUID.randomUUID().toString().replace("-", "");
        JSONObject data = new JSONObject();
        data.put("operationId", id); data.put("lockId", lockId.toString());
        data.put("releaseStatus", GlobalLockReleaseStatus.PENDING.getValue()); data.put("processed", 0); data.put("total", 0);
        TransactionStatus tx = TransactionUtil.openNewTx();
        try {
            mapper.purge();
            mapper.insert(id, UserContext.get().getUserUuid(true), lockId, action, storageContent(data), GlobalLockOperationStatus.PREPARED);
            TransactionUtil.commitTx(tx);
            return withStatusText(data);
        } catch (RuntimeException ex) {
            logger.error("Prepare lock operation failed", ex);
            if (!tx.isCompleted()) TransactionUtil.rollbackTx(tx);
            throw ex;
        }
    }

    /** 查询操作进度；记录已过期或当前用户不可访问时返回空。 */
    public JSONObject get(String id) {
        Map<String, Object> row = mapper.get(id, UserContext.get().getUserUuid(true));
        if (row == null) return null;
        JSONObject data = JSONObject.parseObject((String) row.get("content"));
        data.put("state", row.get("state")); data.put("updatedAt", row.get("updatedAt"));
        return withStatusText(data);
    }

    /** 校验绑定的动作和锁，以原子更新保证操作只能取得一次执行权。 */
    public boolean claim(String id, Long lockId, String action) {
        TransactionStatus tx = TransactionUtil.openNewTx();
        try {
            Map<String, Object> row = mapper.get(id, UserContext.get().getUserUuid(true));
            // 查询受当前用户约束，不读取或泄露其他用户的操作记录来区分不存在与无权访问。
            if (row == null) throw new ParamIrregularException("operationId",
                    $.t("globallock.error.operationunavailable", id, lockId, action));
            if (!String.valueOf(row.get("lockId")).equals(String.valueOf(lockId))
                    || !Objects.equals(action, row.get("action"))) {
                throw new ParamIrregularException("operationId", $.t("globallock.error.operationmismatch",
                        id, row.get("lockId"), row.get("action"), lockId, action));
            }
            boolean claimed = mapper.claim(id, UserContext.get().getUserUuid(true), lockId, action, GlobalLockOperationStatus.PREPARED, GlobalLockOperationStatus.RUNNING) == 1;
            TransactionUtil.commitTx(tx);
            return claimed;
        } catch (RuntimeException ex) {
            logger.error("Claim lock operation failed", ex);
            if (!tx.isCompleted()) TransactionUtil.rollbackTx(tx);
            throw ex;
        }
    }

    /** 加入删除锁的事务，使删除结果与已解锁进度原子提交。 */
    public void committedRelease(JSONObject progress) {
        if (progress != null) mapper.update(progress.getString("operationId"), GlobalLockOperationStatus.RUNNING, storageContent(progress));
    }

    /** 独立保存进度，保存失败不得将已经提交的解锁操作改为失败。 */
    public void save(JSONObject progress, GlobalLockOperationStatus state) {
        if (progress == null) return;
        TransactionStatus tx = null;
        try {
            tx = TransactionUtil.openNewTx();
            mapper.update(progress.getString("operationId"), state, storageContent(progress));
            TransactionUtil.commitTx(tx);
        } catch (Exception ex) {
            logger.error("Persist lock operation {} failed", progress.getString("operationId"), ex);
            if (tx != null && !tx.isCompleted()) TransactionUtil.rollbackTx(tx);
        }
    }

    /** 返回时按当前请求语言补全文案，持久化数据只保留状态值。 */
    private static JSONObject withStatusText(JSONObject progress) {
        progress.put("stateText", GlobalLockOperationStatus.getText(progress.getString("state")));
        progress.put("releaseStatusText", GlobalLockReleaseStatus.getText(progress.getString("releaseStatus")));
        progress.put("notificationStatusText", GlobalLockNotificationStatus.getText(progress.getString("notificationStatus")));
        if (progress.getJSONArray("waiters") != null) {
            for (Object item : progress.getJSONArray("waiters")) {
                JSONObject waiter = (JSONObject) item;
                waiter.put("statusText", GlobalLockWaiterStatus.getText(waiter.getString("status")));
            }
        }
        return progress;
    }

    /** 去掉请求语言相关字段，避免其他语言的查询读到过期翻译。 */
    private static String storageContent(JSONObject progress) {
        JSONObject stored = JSONObject.parseObject(progress.toJSONString());
        stored.remove("stateText");
        stored.remove("releaseStatusText");
        stored.remove("notificationStatusText");
        if (stored.getJSONArray("waiters") != null) {
            for (Object item : stored.getJSONArray("waiters")) ((JSONObject) item).remove("statusText");
        }
        return stored.toJSONString();
    }

    /** 仅提取过程展示所需的标识信息，完整详情仍查询当前锁记录。 */
    public static JSONObject identity(GlobalLockVo lock) {
        JSONObject item = new JSONObject();
        item.put("lockId", lock.getId().toString()); item.put("key", lock.getKey());
        try {
            JSONObject extension = GlobalLockHandlerFactory.getHandler(lock.getHandler()).getLockIdentity(lock);
            if (extension != null) item.putAll(extension);
        } catch (Exception ex) {
            // 业务展示失败不能影响释放和后续通知。
            logger.error("Enrich lock identity {} failed", lock.getId(), ex);
        }
        item.put("lockId", lock.getId().toString()); item.put("key", lock.getKey());
        return item;
    }
}
