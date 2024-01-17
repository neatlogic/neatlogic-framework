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

import neatlogic.framework.dependency.constvalue.FrameworkFromType;
import neatlogic.framework.dependency.core.CustomTableDependencyHandlerBase;
import neatlogic.framework.dependency.core.IFromType;
import neatlogic.framework.dependency.dto.DependencyInfoVo;
import neatlogic.framework.form.dao.mapper.FormMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 表单引用矩阵处理器
 *
 * @author: linbq
 * @since: 2021/4/1 11:42
 **/
//@Service
@Deprecated
public class MatrixFormAttributeDependencyHandler extends CustomTableDependencyHandlerBase {

    @Resource
    private FormMapper formMapper;

    /**
     * 表名
     *
     * @return
     */
    @Override
    protected String getTableName() {
        return "form_attribute_matrix";
    }

    /**
     * 被引用者（上游）字段
     *
     * @return
     */
    @Override
    protected String getFromField() {
        return "matrix_uuid";
    }

    /**
     * 引用者（下游）字段
     *
     * @return
     */
    @Override
    protected String getToField() {
        return "form_verison_uuid";
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
        if (dependencyObj == null) {
            return null;
        }
//        if (dependencyObj instanceof FormAttributeMatrixVo) {
//            FormAttributeMatrixVo formAttributeMatrixVo = (FormAttributeMatrixVo) dependencyObj;
//            JSONObject dependencyInfoConfig = new JSONObject();
//            dependencyInfoConfig.put("formUuid", formAttributeMatrixVo.getFormUuid());
////            dependencyInfoConfig.put("formName", formAttributeMatrixVo.getFormName());
////            dependencyInfoConfig.put("formVersion", formAttributeMatrixVo.getVersion());
//            dependencyInfoConfig.put("formVersionUuid", formAttributeMatrixVo.getFormVersionUuid());
////            dependencyInfoConfig.put("attributeLabel", formAttributeMatrixVo.getFormAttributeLabel());
//            List<String> pathList = new ArrayList<>();
//            pathList.add("表单管理");
//            pathList.add(formAttributeMatrixVo.getFormName());
//            pathList.add(formAttributeMatrixVo.getVersion());
//            String lastName = formAttributeMatrixVo.getFormAttributeLabel();
////            String pathFormat = "表单-${DATA.formName}-${DATA.formVersion}-${DATA.attributeLabel}";
//            String urlFormat = "/" + TenantContext.get().getTenantUuid() + "/framework.html#/form-edit?uuid=${DATA.formUuid}&currentVersionUuid=${DATA.formVersionUuid}";
//            return new DependencyInfoVo(formAttributeMatrixVo.getFormAttributeUuid(), dependencyInfoConfig, lastName, pathList, urlFormat, this.getGroupName());
//        }
        return null;
    }

    /**
     * 被引用者（上游）类型
     *
     * @return
     */
    @Override
    public IFromType getFromType() {
        return FrameworkFromType.MATRIX;
    }

    /**
     * 查询引用列表数据
     *
     * @param from   被引用者（上游）值（如：服务时间窗口uuid）
     * @param startNum 开始行号
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public List<DependencyInfoVo> getDependencyList(Object from, int startNum, int pageSize) {
        List<DependencyInfoVo> resultList = new ArrayList<>();
//        List<FormAttributeMatrixVo> callerList = formMapper.getFormAttributeMatrixByMatrixUuid((String) from, startNum, pageSize);
//        for (FormAttributeMatrixVo caller : callerList) {
//            DependencyInfoVo valueTextVo = parse(caller);
//            if (valueTextVo != null) {
//                resultList.add(valueTextVo);
//            }
//        }
        return resultList;
    }
}
