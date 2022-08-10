/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core.privateapi;

import codedriver.framework.restful.core.IBinaryStreamApiComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author
 * @Time Aug 26,2020
 * @Description: 内部接口
 */
public interface IPrivateBinaryStreamApiComponent extends IBinaryStreamApiComponent {
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
