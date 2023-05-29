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

package neatlogic.framework.notify.crossover;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.notify.core.INotifyPolicyHandler;
import neatlogic.framework.notify.dto.InvokeNotifyPolicyConfigVo;

public interface INotifyServiceCrossoverService extends ICrossoverService {

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
