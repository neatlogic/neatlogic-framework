/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;

/**
 * 此类用于提供两个接口方法，让实现类支持事务增强
 */
public interface MyApiComponent extends IApiComponent {
    Object myDoService(JSONObject paramObj) throws Exception;

    default Object myDoTest(JSONObject paramObj) {
        return null;
    }
}
