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

package neatlogic.framework.restful.groupsearch.core;

import java.util.List;

public interface IGroupSearchHandler {
    String getName();

    String getLabel();

    default String getHeader() {
        return getName() + "#";
    }

    int getSort();

    /**
     * 是否受总数限制
     */
    Boolean isLimit();

    /**
     * 搜索用户时触发
     *
     * @param groupSearchVo 关键字
     * @return 用户列表
     */
    List<GroupSearchOptionVo> search(GroupSearchVo groupSearchVo);

    /**
     * 回显用户选择控件时触发
     *
     * @param groupSearchVo 关键字
     * @return 用户列表
     */
    List<GroupSearchOptionVo> reload(GroupSearchVo groupSearchVo);
}
