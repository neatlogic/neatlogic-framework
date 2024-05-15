/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.form.attribute.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.form.dto.AttributeDataVo;

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

}
