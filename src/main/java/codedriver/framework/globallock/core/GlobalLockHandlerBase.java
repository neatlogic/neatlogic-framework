/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.globallock.core;

import codedriver.framework.dto.globallock.GlobalLockVo;
import codedriver.framework.exception.type.ParamIrregularException;
import codedriver.framework.globallock.GlobalLockManager;
import com.alibaba.fastjson.JSONObject;

public abstract class GlobalLockHandlerBase implements IGlobalLockHandler {
    @Override
    public void retryNotify(Long lockId, JSONObject paramJson) {
        if(lockId == null){
            throw new ParamIrregularException("lockId");
        }
        MyRetryNotify(lockId, paramJson);
    }

    protected void MyRetryNotify(Long lockId, JSONObject paramJson) {

    }

    @Override
    public void doNotify(GlobalLockVo globalLockVo, JSONObject paramJson) {
        myDoNotify(globalLockVo, paramJson);
    }

    protected void myDoNotify(GlobalLockVo globalLockVo, JSONObject paramJson) {

    }

    @Override
    public void cancelLock(Long lockId, JSONObject paramJson) {
        if(lockId == null){
            throw new ParamIrregularException("lockId");
        }
        myCancelLock(lockId, paramJson);
    }

    protected void myCancelLock(Long lockId, JSONObject paramJson) {
        GlobalLockManager.cancelLock(lockId, paramJson);
    }
}
