/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.globallock.core;

import codedriver.framework.dto.globallock.GlobalLockVo;
import codedriver.framework.exception.type.ParamIrregularException;
import codedriver.framework.globallock.GlobalLockManager;
import codedriver.framework.util.TableResultUtil;
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
    public boolean getIsBeenLocked(JSONObject paramJson) {
        return getMyIsBeenLocked(paramJson);
    }

    @Override
    public JSONObject unLock(Long lockId, JSONObject paramJson) {
        if (lockId == null) {
            throw new ParamIrregularException("lockId");
        }
        return myUnLock(lockId, paramJson);
    }

    protected JSONObject myUnLock(Long lockId, JSONObject paramJson) {
        GlobalLockManager.unLock(lockId, paramJson);
        return null;
    }

    @Override
    public boolean getIsCanInsertLock(List<GlobalLockVo> globalLockVoList, GlobalLockVo globalLockVo) {
        return getMyIsCanInsertLock(globalLockVoList, globalLockVo);
    }

    protected boolean getMyIsCanInsertLock(List<GlobalLockVo> globalLockVoList, GlobalLockVo globalLockVo) {
        return true;
    }

    protected boolean getMyIsBeenLocked(JSONObject paramJson) {
        return false;
    }


    @Override
    public JSONObject getSearchResult(List<GlobalLockVo> globalLockVoList, GlobalLockVo globalLockVo) {
        return TableResultUtil.getResult(globalLockVoList, globalLockVo);
    }

    @Override
    public void initSearchParam(GlobalLockVo globalLockVo) {

    }
}
