/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.form.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;

import java.util.List;

public interface IFormCrossoverService extends ICrossoverService {

    JSONObject getMyDetailedDataForSelectHandler(AttributeDataVo attributeDataVo, JSONObject configObj);

    FormAttributeVo getFormAttributeByUuid(String uuid);

    /**
     * 获取表单组件列表
     *
     * @param formUuid 表单UUID
     * @param formName 表单名
     * @param tag      标签
     * @return 组件列表
     */
    List<FormAttributeVo> getFormAttributeList(String formUuid, String formName, String tag);

    /**
     * 获取表单组件列表
     *
     * @param formUuid 表单UUID
     * @param formName 表单名
     * @param tag      标签
     * @return 组件列表
     */
    List<FormAttributeVo> getFormAttributeListNew(String formUuid, String formName, String tag);

    /**
     * 通过简单值获取标准值，例如下拉框的简单值为a或A，返回标准值为{"value":"a", "text":"A"}
     * @param simpleValue
     * @param configObj
     * @return
     */
    Object getSelectStandardValueBySimpleValue(Object simpleValue, JSONObject configObj);
}
