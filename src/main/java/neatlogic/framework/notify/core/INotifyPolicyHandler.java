/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.notify.core;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.ConditionParamVo;
import neatlogic.framework.notify.dto.NotifyTriggerTemplateVo;
import neatlogic.framework.notify.dto.NotifyTriggerVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ClassUtils;

import java.util.List;

public interface INotifyPolicyHandler {

    String getName();

    List<NotifyTriggerVo> getNotifyTriggerList();

    /**
     * 获取通知触发点模版列表
     */
    List<NotifyTriggerTemplateVo> getNotifyTriggerTemplateList(NotifyHandlerType type);

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
}
