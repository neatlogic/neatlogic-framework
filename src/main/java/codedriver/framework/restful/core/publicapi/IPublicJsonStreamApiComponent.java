package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.IJsonStreamApiComponent;

public interface IPublicJsonStreamApiComponent extends IJsonStreamApiComponent{

    /**
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: 接口唯一标识，也是访问URI
     * @param @return
     * @return String
     */
    public String getToken();
}
