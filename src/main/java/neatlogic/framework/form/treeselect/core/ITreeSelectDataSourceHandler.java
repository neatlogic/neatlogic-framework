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

package neatlogic.framework.form.treeselect.core;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ITreeSelectDataSourceHandler {

    /**
     * 获取组件英文名
     *
     * @return 组件英文名
     */
    String getHandler();

    /**
     * 获取组件中文名
     *
     * @return 组件中文名
     */
    String getHandlerName();

    /**
     * 获取数据源配置
     * @return 配置
     */
    JSONObject getConfig();

    List<String> valueConversionTextPathList(Object value);
}
