/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.service;

import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.form.dto.FormAttributeVo;
import com.alibaba.fastjson.JSONObject;

public interface IFormCrossoverService extends ICrossoverService {

    /**
     * 保存表单属性与其他功能的引用关系
     * @param formAttributeVo
     */
    void saveDependency(FormAttributeVo formAttributeVo);

    /**
     * 表格输入组件密码类型加密
     * @param data
     */
    JSONObject staticListPasswordEncrypt(JSONObject data, JSONObject config);
}
