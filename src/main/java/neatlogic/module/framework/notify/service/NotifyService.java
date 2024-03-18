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

package neatlogic.module.framework.notify.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.notify.core.INotifyPolicyHandler;
import neatlogic.framework.notify.dto.InvokeNotifyPolicyConfigVo;

public interface NotifyService {

    InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(JSONObject notifyPolicyConfig, Class< ? extends INotifyPolicyHandler> clazz);

    InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo, Class< ? extends INotifyPolicyHandler> clazz);

    InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(JSONObject notifyPolicyConfig);

    InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo);

    /**
     * 检查通知策略是否存在
     * 1.如果是采用默认模式，不用检查通知策略是否存在，直接返回false
     * 2.如果是采用自定义模式，需要检查通知策略是否存在，存在返回true，不存在则抛异常
     * @param invokeNotifyPolicyConfigVo 引用通知策略配置信息
     * @return 如果返回true，调用方可能需要执行依赖引用关系保存逻辑
     */
    boolean checkNotifyPolicyIsExists(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo);
}
