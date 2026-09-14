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

package neatlogic.framework.globallock.exception;

import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** 释放失败时保留原始异常链及本次操作标识，不改变事务重试和回滚语义。 */
public class GlobalLockReleaseException extends ApiRuntimeException {
    /** 已读取到锁时附带资源信息，读取失败时仍可按锁 ID 和操作 ID 定位。 */
    public GlobalLockReleaseException(Long lockId, GlobalLockVo lock, String operationId, String action, Throwable cause) {
        super("globallock.error.releasefailed", cause, action, lockId, lock == null ? null : lock.getKey(),
                lock == null ? null : lock.getUuid(), lock == null ? null : lock.getHandler(), operationId, reason(cause));
    }

    /** 提取根异常类型和消息；SQL 异常附加错误码，防止空消息或循环 cause 隐藏根因。 */
    private static String reason(Throwable cause) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Throwable root = cause;
        visited.add(root);
        while (root.getCause() != null && visited.add(root.getCause())) root = root.getCause();
        String detail = root.getClass().getName() + ": " + String.valueOf(root.getMessage());
        if (root instanceof SQLException) {
            SQLException sql = (SQLException) root;
            detail += " [SQLState=" + sql.getSQLState() + ", errorCode=" + sql.getErrorCode() + "]";
        }
        return detail;
    }
}
