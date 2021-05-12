package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.IBinaryStreamApiComponent;

/**  
* @Author 
* @Time Aug 26,2020
* @Description: 外部接口
*/  
public interface IPublicBinaryStreamApiComponent extends IBinaryStreamApiComponent{
    /**
     * 接口唯一标识，也是访问URI
     * @return token
     */
    public String getToken();
}
