package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.IApiComponent;

/**  
* @Author 
* @Time Aug 26,2020
* @Description: 外部接口
*/  
public interface IPublicApiComponent extends IApiComponent{
    /**
     * 接口唯一标识，也是访问URI,不声明则需要接口管理添加实例才能使用。如果声明token则直接内部使用，单实例。
     * @return
     */
    public String getToken();
}
