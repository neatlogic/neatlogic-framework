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

package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;

/**
 * 此类用于提供两个接口方法，让实现类支持事务增强
 */
public interface MyApiComponent extends IApiComponent {
    Object myDoService(JSONObject paramObj) throws Exception;

    default Object myDoTest(JSONObject paramObj) {
        return null;
    }
}
