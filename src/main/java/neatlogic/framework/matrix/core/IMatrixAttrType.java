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

package neatlogic.framework.matrix.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;

import java.util.Map;
import java.util.Set;

public interface IMatrixAttrType {
    /**
     * 处理器名
     */
    String getHandler();

    /**
     * 获取矩阵值回显
     */
    void getTextByValue(MatrixAttributeVo matrixAttribute, Object valueObj, JSONObject resultObj);


    /**
     * 导出excel时转换值
     *
     * @param value 值
     */
    String getValueWhenExport(Object value, MatrixAttributeVo attributeVo);

    /**
     * 根据导入的值转换成系统的值
     *
     * @param valueMap 值map
     * @return 返回存在重复值的key
     */
    Set<String> getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap);

}
