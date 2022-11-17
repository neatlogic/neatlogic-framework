/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.attribute.core;

public interface INewAttribute extends IFormAttributeHandler {

    default String getHandlerName() {
        return null;
    }
    default String getIcon() {
        return null;
    }

    default String getDataType() {
        return null;
    }

    default boolean isShowable() {
        return false;
    }

    default boolean isValueable() {
        return false;
    }

    default boolean isFilterable() {
        return false;
    }

    default boolean isExtendable() {
        return false;
    }

    default boolean isForTemplate() {
        return false;
    }

    default String getModule() {
        return null;
    }

    default int getSort() {
        return 0;
    }
}
