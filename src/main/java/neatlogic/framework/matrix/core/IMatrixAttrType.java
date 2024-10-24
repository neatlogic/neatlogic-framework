/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    String getValueWhenExport(String value);

    /**
     * 根据导入的值转换成系统的值
     *
     * @param valueMap 值map
     * @return 返回存在重复值的key
     */
    Set<String> getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap);

}
