/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package neatlogic.framework.globallock.core;

import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.globallock.GlobalLockManager;
import neatlogic.framework.util.TableResultUtil;
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
