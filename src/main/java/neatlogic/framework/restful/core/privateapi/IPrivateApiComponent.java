/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.restful.core.privateapi;

import neatlogic.framework.restful.core.IApiComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IPrivateApiComponent extends IApiComponent {
    /**
     * 接口唯一标识，也是访问URI
     *
     * @return 接口唯一地址
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getToken();

}
