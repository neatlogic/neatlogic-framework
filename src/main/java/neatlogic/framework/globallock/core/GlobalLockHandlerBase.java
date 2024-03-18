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

package neatlogic.framework.globallock.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.globallock.GlobalLockManager;
import neatlogic.framework.util.TableResultUtil;

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
