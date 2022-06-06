/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.globallock.core;

import codedriver.framework.dto.globallock.GlobalLockVo;
import codedriver.framework.exception.type.ParamIrregularException;
import codedriver.framework.globallock.GlobalLockManager;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public abstract class GlobalLockHandlerBase implements IGlobalLockHandler {

    @Override
    public void doNotify(GlobalLockVo globalLockVo, JSONObject paramJson) {
        myDoNotify(globalLockVo, paramJson);
    }

    protected void myDoNotify(GlobalLockVo globalLockVo, JSONObject paramJson) {

    }

    @Override
    public JSONObject cancelLock(Long lockId, JSONObject paramJson) {
        if(lockId == null){
            throw new ParamIrregularException("lockId");
        }
        return myCancelLock(lockId, paramJson);
    }

    protected JSONObject myCancelLock(Long lockId, JSONObject paramJson) {
        GlobalLockManager.cancelLock(lockId, paramJson);
        return null;
    }

    @Override
    public boolean getIsCanInsertLock(List<GlobalLockVo> globalLockVoList, GlobalLockVo globalLockVo) {
        return true;
    }
}
