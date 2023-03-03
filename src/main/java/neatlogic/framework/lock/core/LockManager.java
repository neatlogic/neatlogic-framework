/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.lock.core;

import neatlogic.framework.common.RootConfiguration;
import neatlogic.framework.lock.dao.mapper.LockMapper;
import neatlogic.framework.transaction.util.TransactionUtil;
import neatlogic.framework.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.SQLException;

@Service
@RootConfiguration
public class LockManager {
    private static final Logger logger = LoggerFactory.getLogger(LockManager.class);

    private static LockMapper lockMapper;

    @Autowired
    public LockManager(LockMapper _lockMapper) {
        lockMapper = _lockMapper;
    }

    public static String getLockById(String lock) {
        lock = Md5Util.encryptMD5(lock);
        lockMapper.insertLock(lock);
        return lockMapper.getLockByIdForUpdate(lock);
    }

    /**
     * 执行串行操作
     *
     * @param lock   锁唯一名
     * @param method 逻辑
     */
    public static void invoke(String lock, ISerialMethod method) {
        lock = Md5Util.encryptMD5(lock);
        while (true) {
            boolean needOpenNewTx = TransactionSynchronizationManager.isSynchronizationActive();
            TransactionStatus transactionStatus = null;
            if (needOpenNewTx) {
                transactionStatus = TransactionUtil.openTx();
            }
            try {
                lockMapper.insertLock(lock);
                lockMapper.getLockByIdForUpdate(lock);
            } catch (Exception ex) {
                TransactionUtil.rollbackTx(transactionStatus);
                if (ex.getCause() instanceof SQLException) {
                    if (ex.getCause().getMessage().contains("Lock wait timeout exceeded")) {
                        logger.error("锁：" + lock + "等待超时，重新开始获取");
                        continue;
                    }
                }
                logger.error(ex.getMessage(), ex);
                break;
            }
            try {
                method.execute();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            lockMapper.deleteLock(lock);
            if (needOpenNewTx) {
                TransactionUtil.commitTx(transactionStatus);
            }
            break;

        }
    }


}