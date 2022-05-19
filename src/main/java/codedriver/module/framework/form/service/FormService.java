/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.service;

import codedriver.framework.form.dto.FormAttributeVo;

public interface FormService {
    /**
     * 保存表单属性与其他功能的引用关系
     * @param formAttributeVo
     */
    void saveDependency(FormAttributeVo formAttributeVo);
}