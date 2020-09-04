package codedriver.framework.restful.core.privateapi;

import codedriver.framework.restful.core.IBinaryStreamApiComponent;

/**  
* @Author 
* @Time Aug 26,2020
* @Description: 内部接口
*/  
public interface IPrivateBinaryStreamApiComponent extends IBinaryStreamApiComponent{
	/**
	 * @Author: chenqiwei
	 * @Time:Jun 19, 2020
	 * @Description: 接口唯一标识，也是访问URI
	 * @param @return
	 * @return String
	 */
	public String getToken();

}
