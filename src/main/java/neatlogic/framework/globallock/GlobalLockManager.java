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
import neatlogic.framework.common.RootConfiguration;
import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.globallock.core.GlobalLockHandlerFactory;
import neatlogic.framework.globallock.core.IGlobalLockHandler;
import neatlogic.framework.globallock.dao.mapper.GlobalLockMapper;
import neatlogic.framework.lock.core.LockManager;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RootConfiguration
public class GlobalLockManager {
    private static final Logger logger = LoggerFactory.getLogger(LockManager.class);

    private static GlobalLockMapper globalLockMapper;

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
        TransactionStatus transactionStatus = TransactionUtil.openTx();
        try {
            globalLockMapper.insertLock(globalLockVo);
            //获取所有该key的锁和未上锁的队列 for update
            List<GlobalLockVo> globalLockVoList = globalLockMapper.getGlobalLockByUuidForUpdate(globalLockVo.getUuid());
            //执行mode 策略 验证是否允许上锁
            if (GlobalLockHandlerFactory.getHandler(globalLockVo.getHandler()).getIsCanInsertLock(globalLockVoList, globalLockVo)) {
                TransactionUtil.commitTx(transactionStatus);
            } else {
                TransactionUtil.rollbackTx(transactionStatus);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            //如果wait lock timeout 则仅加入队列，等待notify
            TransactionUtil.rollbackTx(transactionStatus);
            throw new ApiRuntimeException(ex.getMessage());
        }
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
        TransactionStatus transactionStatus = TransactionUtil.openTx();
        try {
            //获取所有该key的锁和未上锁的队列 for update
            List<GlobalLockVo> globalLockVoList = globalLockMapper.getGlobalLockByUuidForUpdate(globalLockVo.getUuid());
            //执行mode 策略 验证是否允许上锁
            if (GlobalLockHandlerFactory.getHandler(globalLockVo.getHandler()).getIsCanLock(globalLockVoList, globalLockVo)) {
                globalLockMapper.updateToLockById(globalLockVo.getId());
                globalLockVo.setIsLock(1);
            }
            TransactionUtil.commitTx(transactionStatus);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            //如果wait lock timeout 则仅加入队列，等待notify
            TransactionUtil.rollbackTx(transactionStatus);
            globalLockVo.setWaitReason(ex.getMessage());
        }
        return globalLockVo;
    }

    /**
     * 取消等待锁
     *
     * @param lockId 锁id
     */
    public static void cancelLock(Long lockId) {
        GlobalLockVo globalLockVo = globalLockMapper.getGlobalLockById(lockId);
        if (globalLockVo != null) {
            TransactionStatus transactionStatus = TransactionUtil.openTx();
            try {
                //获取所有该key的锁和未上锁的队列 for update
                globalLockMapper.getGlobalLockByUuidForUpdate(globalLockVo.getUuid());
                globalLockMapper.deleteLock(lockId);
                TransactionUtil.commitTx(transactionStatus);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                TransactionUtil.rollbackTx(transactionStatus);
                throw new ApiRuntimeException(ex.getMessage());
            }
        }
    }

    /**
     * 解锁
     *
     * @param lockId    锁id
     * @param paramJson 锁入参
     */
    public static void unLock(Long lockId, JSONObject paramJson) {
        GlobalLockVo globalLockVo = globalLockMapper.getGlobalLockById(lockId);
        if (globalLockVo != null) {
            TransactionStatus transactionStatus = TransactionUtil.openTx();
            try {
                //获取所有该key的锁和未上锁的队列 for update
                globalLockMapper.getGlobalLockByUuidForUpdate(globalLockVo.getUuid());
                globalLockMapper.deleteLock(lockId);
                //只有释放已经获得锁的才notify
                if(globalLockVo.getIsLock() == 1) {
                    //获取对应uuid队列中下一个lockId notify
                    GlobalLockVo nextGlobalLockVo = globalLockMapper.getNextGlobalLockByUuid(globalLockVo.getUuid());
                    if (nextGlobalLockVo != null) {
                        GlobalLockHandlerFactory.getHandler(globalLockVo.getHandler()).doNotify(nextGlobalLockVo, paramJson);
                    }
                }
                TransactionUtil.commitTx(transactionStatus);
            } catch (Exception ex) {
                TransactionUtil.rollbackTx(transactionStatus);
                //如果是反射抛得异常，则需循环拆包，把真实得异常类找出来
                Throwable target = ex;
                while (target instanceof InvocationTargetException) {
                    target = ((InvocationTargetException) target).getTargetException();
                }
                logger.error(target.getMessage(), target);
                throw new ApiRuntimeException(target.getMessage());
            }
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
        }
        globalLockVo = globalLockTmp;
        return lock(globalLockVo);
    }

    public static boolean getIsBeenLocked(GlobalLockVo globalLockVo) {
        return globalLockMapper.getGlobalLockByUuidForUpdate(globalLockVo.getUuid()).stream().anyMatch(o -> Objects.equals(o.getIsLock(), 1));
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
