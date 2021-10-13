/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.IApiComponent;

/**
 * 外部接口
 */
public interface IPublicApiComponent extends IApiComponent {
    /**
     * 接口唯一标识，也是访问URI,不声明则需要接口管理添加实例才能使用。如果声明token则直接内部使用，单实例。
     */
    String getToken();
}
