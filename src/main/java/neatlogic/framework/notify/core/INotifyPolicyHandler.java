/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.notify.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.ConditionParamVo;
import neatlogic.framework.notify.dto.NotifyTriggerVo;
import org.springframework.util.ClassUtils;

import java.util.List;

public interface INotifyPolicyHandler {

    String getName();

    List<NotifyTriggerVo> getNotifyTriggerList();

    List<ValueTextVo> getParamTypeList();

    List<ConditionParamVo> getSystemParamList();

    List<ConditionParamVo> getSystemConditionOptionList();

    JSONObject getAuthorityConfig();

    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * 绑定权限，每种handler对应不同的权限
     */
    String getAuthName();

//    INotifyPolicyHandlerGroup getGroup();

    /**
     * 是否公开，默认公开
     */
    default boolean isPublic() {
        return true;
    }

    /**
     * 是否允许添加多个策略
     */
    default int isAllowMultiPolicy() {
        return 1;
    }

    default String getModuleGroup() {
        return null;
    }
}
