package neatlogic.framework.restful.core;

import neatlogic.framework.dto.FieldValidResultVo;
import com.alibaba.fastjson.JSONObject;

/**
 * @Title: IValid
 * @Package: neatlogic.framework.restful.core
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/2/20 10:33
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface IValid {
    FieldValidResultVo valid(JSONObject obj);
}
