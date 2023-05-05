/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.matrix.core;

import com.alibaba.fastjson.JSON;
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
