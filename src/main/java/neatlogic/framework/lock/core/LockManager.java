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