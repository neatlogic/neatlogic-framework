/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
