/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

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

package neatlogic.module.framework.dependency.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dependency.constvalue.FrameworkFromType;
import neatlogic.framework.dependency.core.CustomTableDependencyHandlerBase;
import neatlogic.framework.dependency.core.IFromType;
import neatlogic.framework.dependency.dto.DependencyInfoVo;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.MatrixVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 矩阵引用cmdb模型
 *
 * @author: linbq
 * @since: 2021/4/6 15:21
 **/
@Service
public class CmdbCiMatrixDependencyHandler extends CustomTableDependencyHandlerBase {

    @Resource
    private MatrixMapper matrixMapper;

    /**
     * 表名
     *
     * @return
     */
    @Override
    protected String getTableName() {
        return "matrix_ci";
    }

    /**
     * 被引用者（上游）字段
     *
     * @return
     */
    @Override
    protected String getFromField() {
        return "ci_id";
    }

    /**
     * 引用者（下游）字段
     *
     * @return
     */
    @Override
    protected String getToField() {
        return "matrix_uuid";
    }

    @Override
    protected List<String> getToFieldList() {
        return null;
    }

    /**
     * 解析数据，拼装跳转url，返回引用下拉列表一个选项数据结构
     *
     * @param dependencyObj 引用关系数据
     * @return
     */
    @Override
    protected DependencyInfoVo parse(Object dependencyObj) {
        if (dependencyObj instanceof Map) {
            Map<String, Object> map = (Map) dependencyObj;
            String matrixUuid =  (String) map.get("matrix_uuid");
            MatrixVo matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
            if (matrixVo != null) {
                JSONObject dependencyInfoConfig = new JSONObject();
                dependencyInfoConfig.put("matrixUuid", matrixVo.getUuid());
                dependencyInfoConfig.put("matrixName", matrixVo.getName());
                dependencyInfoConfig.put("matrixType", matrixVo.getType());
                List<String> pathList = new ArrayList<>();
                pathList.add("矩阵管理");
                String lastName = matrixVo.getName();
//                String pathFormat = "矩阵-${DATA.matrixName}";
                String urlFormat = "/" + TenantContext.get().getTenantUuid() + "/framework.html#/matrix-view-edit?uuid=${DATA.matrixUuid}&name=${DATA.matrixName}&type=${DATA.matrixType}";
                return new DependencyInfoVo(matrixVo.getUuid(), dependencyInfoConfig, lastName, pathList, urlFormat, this.getGroupName());
            }
        }
        return null;
    }

    /**
     * 被引用者（上游）类型
     *
     * @return
     */
    @Override
    public IFromType getFromType() {
        return FrameworkFromType.CMDBCI;
    }
}
