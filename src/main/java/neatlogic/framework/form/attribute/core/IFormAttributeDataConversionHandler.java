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

package neatlogic.framework.form.attribute.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;

/**
 * @Author:laiwt
 * @Time:2021年5月25日
 * @ClassName: IFormAttributeDataConversionHandler
 * @Description: 表单组件数据转换接口
 */
public interface IFormAttributeDataConversionHandler {

    /**
     * 组件英文名
     *
     * @return
     */
    String getHandler();

    /**
     * @param attributeDataVo
     * @param configObj
     * @return Object
     * @Time:2020年7月10日
     * @Description: 将表单属性值转换成对应的text
     */
    Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * 获取简单值，例如下拉框值为{"value":"a", "text":"A"}，返回值为a
     * @param originalValue
     * @return
     */
    default Object getSimpleValue(Object originalValue) {
        return originalValue;
    }

    /**
     * 获取增强可读性值，注意用于表格选择组件、表格输入组件、表单子组件
     * @param originalValue
     * @param formAttributeVo
     * @return
     */
    default Object getEnhanceReadabilityValue(Object originalValue, FormAttributeVo formAttributeVo) {
        return originalValue;
    }

    /**
     * 通过简单值获取标准值，例如下拉框的简单值为a或A，返回标准值为{"value":"a", "text":"A"}
     * @param simpleValue
     * @return
     */
    default Object getStandardValueBySimpleValue(Object simpleValue, JSONObject configObj) {
        return simpleValue;
    }

    /**
     * 数据转换，用于邮件模板展示表单信息
     *
     * @param attributeDataVo
     * @param configObj
     * @return
     */
    Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * @Description: 将text转换成表单属性值，暂时用于批量导入工单
     * @Author: laiwt
     * @Date: 2021/1/28 17:06
     * @Params: [values, config]
     * @Returns: java.lang.Object
     **/
    Object textConversionValue(Object text, JSONObject config);

    JSONObject getDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * 数据转换，用户工单导出
     *
     * @param attributeDataVo
     * @param configObj
     * @return
     */
    Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * 导出excel时，当前表单组件需要占据的表头单元格长度
     * 对于普通组件，如文本框、日期等，默认占一格
     * 对于表格输入组件等表格类组件，占据的单元格长度视具体配置而定
     *
     * @param configObj
     * @return
     */
    default int getExcelHeadLength(JSONObject configObj) {
        return 1;
    }

    /**
     * 导出excel时，当前表单组件数据需要占据的行数
     * 对于普通组件，如文本框、日期等，默认占一行
     * 对于表格输入组件等表格类组件，占据的行数视数据而定
     *
     * @param attributeDataVo
     * @param configObj
     * @return
     */
    default int getExcelRowCount(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return 1;
    }

    /**
     * 密码加密
     * @param source 属性原始数据
     * @param configObj 属性配置信息
     * @return 返回加密后的数据
     */
    default Object passwordEncryption(Object source, JSONObject configObj) {// , Object oldSource
        return source;
    }

//    /**
//     * 密码解密
//     * @param source 属性原始数据
//     * @param configObj 属性配置信息
//     * @return 返回解密后的数据
//     */
//    default String passwordDecryption(Object source, JSONObject configObj, String attributeUuid, JSONObject otherParamConfig) {
//        return null;
//    }

    /**
     * 密码解密
     * @param source 属性原始数据
     * @param attributeUuid 属性UUID
     * @param otherParamConfig 其他参数信息
     * @return 返回解密后的数据
     */
    default JSONObject passwordDecryption(Object source, String attributeUuid, JSONObject otherParamConfig) {
        return null;
    }

    /**
     * 掩藏密码
     * @return
     */
//    default Object passwordMask(Object source, JSONObject configObj) {
//        return source;
//    }
}
