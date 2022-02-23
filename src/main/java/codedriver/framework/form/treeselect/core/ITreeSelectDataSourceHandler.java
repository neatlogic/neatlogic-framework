/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.treeselect.core;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ITreeSelectDataSourceHandler {

    /**
     * 获取组件英文名
     *
     * @return 组件英文名
     */
    String getHandler();

    /**
     * 获取组件中文名
     *
     * @return 组件中文名
     */
    String getHandlerName();

    /**
     * 获取数据源配置
     * @return 配置
     */
    JSONObject getConfig();

    List<String> valueConversionTextPathList(Object value);
}
