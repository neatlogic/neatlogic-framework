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

package neatlogic.framework.file.core.layout;

import ch.qos.logback.core.spi.LifeCycle;

public interface Layout<E> extends LifeCycle {
    /**
     * 转换一个事件（Object类型），并在适当格式化后将其作为String返回。
     *
     * <p>接收对象并返回String是格式化事件的最不复杂的方法。然而，它具有显著的CPU效率。
     * </p>
     *
     * @param event 要格式化的事件
     * @return
     */
    String doLayout(E event);

    /**
     * 返回此布局的文件头。返回的值可能为空。
     * @return The header.
     */
    String getFileHeader();

    /**
     * 返回此布局的文件页脚。返回的值可能为空。
     * @return The footer.
     */
    String getFileFooter();

    /**
     * 返回适用于实现的内容类型。
     *
     * @return
     */
    String getContentType();
}
