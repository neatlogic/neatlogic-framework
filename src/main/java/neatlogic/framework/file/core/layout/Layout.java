/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
