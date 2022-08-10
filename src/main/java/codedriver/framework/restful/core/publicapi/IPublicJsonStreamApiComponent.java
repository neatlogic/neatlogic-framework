/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.IJsonStreamApiComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IPublicJsonStreamApiComponent extends IJsonStreamApiComponent{

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: 接口唯一标识，也是访问URI
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getToken();
}
