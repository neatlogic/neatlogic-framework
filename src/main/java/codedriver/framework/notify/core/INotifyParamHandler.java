/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

/**
 * @author linbq
 * @since 2021/10/15 16:35
 **/
public interface INotifyParamHandler {
    String getValue();

    Object getText(Object object);
}
