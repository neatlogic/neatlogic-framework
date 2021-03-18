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

    public static void invoke(String lock, ISerialMethod method) {
        lock = Md5Util.encryptMD5(lock);
        boolean needOpenNewTx = false;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            needOpenNewTx = true;
        }
        TransactionStatus transactionStatus = null;
        if (needOpenNewTx) {
            transactionStatus = TransactionUtil.openTx();
        }
        lockMapper.insertLock(lock);
        lockMapper.getLockByIdForUpdate(lock);
        try {
            method.execute();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        lockMapper.deleteLock(lock);
        if (needOpenNewTx) {
            TransactionUtil.commitTx(transactionStatus);
        }
    }

}