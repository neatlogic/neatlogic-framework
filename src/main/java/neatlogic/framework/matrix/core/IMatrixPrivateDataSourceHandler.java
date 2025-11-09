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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import neatlogic.framework.util.UuidUtil;

import java.util.List;
import java.util.Map;

/**
 * 矩阵私有类型数据源接口
 */
public interface IMatrixPrivateDataSourceHandler {

    String getUuid();

    String getName();

    String getLabel();

    /**
     * 获取私有矩阵结构
     * */
    List<MatrixAttributeVo> getAttributeList();

    /**
     * 获取私有矩阵插件的数据，当isSearchable为1的时候需要考虑数据动态过滤，如表单内引用
     * */
    List<Map<String, String>> searchTableData(MatrixDataVo dataVo);

    /**
     * 把私有矩阵插件定义的列转成属性对象
     * */
    default void setAttribute(List<MatrixAttributeVo> matrixAttributeList , JSONArray attributeArray){
        int index = 0 ;
        for (Object obj : attributeArray) {
            JSONObject jsonObj = (JSONObject) obj;
            String label = jsonObj.getString("label");
            String name ;
            if (!jsonObj.containsKey("name")) {
                name = label;
            }else{
                name = jsonObj.getString("name");
            }
            int isPrimaryKey = 0;
            if (jsonObj.containsKey("isPrimaryKey")){
                isPrimaryKey = jsonObj.getInteger("isPrimaryKey");
            }
            int isSearchable = 0 ;
            if (jsonObj.containsKey("isSearchable")){
                isSearchable = jsonObj.getInteger("isSearchable");
            }
            MatrixAttributeVo matrixAttributeVo = new MatrixAttributeVo();
            matrixAttributeVo.setMatrixUuid(getUuid());
            String key = getLabel() + "-" + label;
            matrixAttributeVo.setUuid(UuidUtil.getCustomUUID(key));
            matrixAttributeVo.setName(name);
            matrixAttributeVo.setUniqueIdentifier(label);
            matrixAttributeVo.setLabel(label);
            matrixAttributeVo.setType(MatrixAttributeType.INPUT.getValue());
            matrixAttributeVo.setIsRequired(1);
            matrixAttributeVo.setIsDeletable(0);
            matrixAttributeVo.setSort(index);
            matrixAttributeVo.setPrimaryKey(isPrimaryKey);
            matrixAttributeVo.setIsSearchable(isSearchable);
            matrixAttributeList.add(matrixAttributeVo);
            index ++ ;
        }

    }
}
