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

import neatlogic.framework.transaction.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/** 仅对明确的锁竞争回滚重试完整事务，加入外层事务时由调用方负责恢复。 */
final class GlobalLockTransaction {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLockTransaction.class);

    private GlobalLockTransaction() { }

    /** 最多重试三次；提交阶段结果不明确时禁止重放，避免重复副作用。 */
    static void execute(Runnable work) {
        for (int attempt = 0; ; attempt++) {
            TransactionStatus tx = TransactionUtil.openTx();
            boolean committing = false;
            try {
                work.run();
                committing = true;
                TransactionUtil.commitTx(tx);
                return;
            } catch (RuntimeException ex) {
                logger.error("Resource lock transaction failed, attempt={}", attempt, ex);
                if (!tx.isCompleted()) TransactionUtil.rollbackTx(tx);
                if (committing || !tx.isNewTransaction() || attempt >= 3 || !isLockConflict(ex)
                        || Thread.currentThread().isInterrupted()) throw ex;
                try {
                    Thread.sleep((50L << attempt) + ThreadLocalRandom.current().nextInt(51));
                } catch (InterruptedException interrupted) {
                    logger.error("Resource lock retry interrupted", interrupted);
                    Thread.currentThread().interrupt();
                    ex.addSuppressed(interrupted);
                    throw ex;
                }
            } catch (Error error) {
                logger.error("Resource lock transaction aborted", error);
                if (!tx.isCompleted()) TransactionUtil.rollbackTx(tx);
                throw error;
            }
        }
    }

    /** 按数据库错误码判定，不将连接中断或业务异常误当成可重试锁竞争。 */
    static boolean isLockConflict(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException) {
                int code = ((SQLException) cause).getErrorCode();
                if (code == 1213 || code == 1205) return true;
            }
        }
        return false;
    }
}
