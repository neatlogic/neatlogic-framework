/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
