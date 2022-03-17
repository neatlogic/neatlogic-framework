/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.dependency.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dependency.constvalue.FrameworkFromType;
import codedriver.framework.dependency.core.CustomTableDependencyHandlerBase;
import codedriver.framework.dependency.core.IFromType;
import codedriver.framework.dependency.dto.DependencyInfoVo;
import codedriver.framework.form.dao.mapper.FormMapper;
import codedriver.framework.form.dto.FormAttributeMatrixVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 表单引用矩阵处理器
 *
 * @author: linbq
 * @since: 2021/4/1 11:42
 **/
@Service
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
        if (dependencyObj instanceof FormAttributeMatrixVo) {
            FormAttributeMatrixVo formAttributeMatrixVo = (FormAttributeMatrixVo) dependencyObj;
            JSONObject dependencyInfoConfig = new JSONObject();
            dependencyInfoConfig.put("formUuid", formAttributeMatrixVo.getFormUuid());
            dependencyInfoConfig.put("formName", formAttributeMatrixVo.getFormName());
            dependencyInfoConfig.put("formVersion", formAttributeMatrixVo.getVersion());
            dependencyInfoConfig.put("formVersionUuid", formAttributeMatrixVo.getFormVersionUuid());
            dependencyInfoConfig.put("attributeLabel", formAttributeMatrixVo.getFormAttributeLabel());
            String pathFormat = "表单-${DATA.formName}-${DATA.formVersion}-${DATA.attributeLabel}";
            String urlFormat = "/" + TenantContext.get().getTenantUuid() + "/framework.html#/form-edit?uuid=${DATA.formUuid}&currentVersionUuid=${DATA.formVersionUuid}";
            return new DependencyInfoVo(formAttributeMatrixVo.getFormAttributeUuid(), dependencyInfoConfig, pathFormat, urlFormat, this.getGroupName());
//            DependencyInfoVo dependencyInfoVo = new DependencyInfoVo();
//            dependencyInfoVo.setValue(formAttributeMatrixVo.getFormAttributeUuid());
//            String text = String.format("<a href=\"/%s/framework.html#/form-edit?uuid=%s&currentVersionUuid=%s\" target=\"_blank\">%s</a>",
//                    TenantContext.get().getTenantUuid(),
//                    formAttributeMatrixVo.getFormUuid(),
//                    formAttributeMatrixVo.getFormVersionUuid(),
//                    formAttributeMatrixVo.getFormName() + "-" + formAttributeMatrixVo.getVersion() + "-" + formAttributeMatrixVo.getFormAttributeLabel());
//            dependencyInfoVo.setText(text);
//            return dependencyInfoVo;
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
        List<FormAttributeMatrixVo> callerList = formMapper.getFormAttributeMatrixByMatrixUuid((String) from, startNum, pageSize);
        for (FormAttributeMatrixVo caller : callerList) {
            DependencyInfoVo valueTextVo = parse(caller);
            if (valueTextVo != null) {
                resultList.add(valueTextVo);
            }
        }
        return resultList;
    }
}
