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

package neatlogic.framework.lcs.linehandler.core;

import neatlogic.framework.lcs.BaseLineVo;

public interface ILineHandler {

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
     * 获取组件mainBody content|config
     *
     * @param line 行对象
     * @return mainBody content|config
     */
    String getMainBody(BaseLineVo line);

    /**
     * 设置组件mainBody content|config
     *
     * @param line     行对象
     * @param mainBody content|config
     */
    void setMainBody(BaseLineVo line, String mainBody);

    /**
     * 内容是否需要对比
     * @return
     */
    boolean needCompare();

}
