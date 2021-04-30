/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.lock.core;

import codedriver.framework.common.RootConfiguration;
import codedriver.framework.lock.dao.mapper.LockMapper;
import codedriver.framework.transaction.util.TransactionUtil;
import codedriver.framework.util.Md5Util;
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