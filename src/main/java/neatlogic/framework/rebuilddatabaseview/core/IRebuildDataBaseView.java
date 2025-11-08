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

package neatlogic.framework.rebuilddatabaseview.core;

import org.springframework.util.ClassUtils;

import java.util.List;

public interface IRebuildDataBaseView {

    default String getHandler() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    String getDescription();

    /**
     * 只有视图不存在时才创建视图
     * @return
     */
    List<ViewStatusInfo> createViewIfNotExists();

    /**
     * 如果视图存在则删除，重新创建视图
     * @return
     */
    List<ViewStatusInfo> createOrReplaceView();

    int getSort();
}
