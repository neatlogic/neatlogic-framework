package neatlogic.framework.restful.core;

import neatlogic.framework.dto.FieldValidResultVo;
import com.alibaba.fastjson.JSONObject;

/**
 * @Title: IValid
 * @Package: neatlogic.framework.restful.core
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/2/20 10:33
 **/
public interface IValid {
    FieldValidResultVo valid(JSONObject obj) throws CloneNotSupportedException;
}
