/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core.privateapi;

import codedriver.framework.restful.core.IApiComponent;

public interface IPrivateApiComponent extends IApiComponent {
    /**
     * 接口唯一标识，也是访问URI
     *
     * @return 接口唯一地址
     */
    String getToken();

}
