/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dependency.core;

/**
 * 被调用者类型接口
 *
 * @author: linbq
 * @since: 2021/4/2 10:29
 **/
public interface IFromType {
    /**
     * 被调用者类型值
     *
     * @return
     */
    public String getValue();

    /**
     * 被调用者类型名
     *
     * @return
     */
    public String getText();
}
