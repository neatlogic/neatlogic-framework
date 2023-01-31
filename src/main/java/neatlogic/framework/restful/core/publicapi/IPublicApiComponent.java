/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.restful.core.publicapi;

import neatlogic.framework.restful.core.IApiComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 外部接口
 */
public interface IPublicApiComponent extends IApiComponent {
    /**
     * 接口唯一标识，也是访问URI,不声明则需要接口管理添加实例才能使用。如果声明token则直接内部使用，单实例。
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getToken();
}
