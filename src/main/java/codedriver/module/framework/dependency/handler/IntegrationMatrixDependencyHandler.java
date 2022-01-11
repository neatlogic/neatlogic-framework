/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.dependency.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dependency.constvalue.FromType;
import codedriver.framework.dependency.core.CustomTableDependencyHandlerBase;
import codedriver.framework.dependency.core.IFromType;
import codedriver.framework.dependency.dto.DependencyInfoVo;
import codedriver.framework.matrix.dao.mapper.MatrixMapper;
import codedriver.framework.matrix.dto.MatrixVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 矩阵引用集成处理器
 *
 * @author: linbq
 * @since: 2021/4/6 15:21
 **/
@Service
public class IntegrationMatrixDependencyHandler extends CustomTableDependencyHandlerBase {

    @Resource
    private MatrixMapper matrixMapper;

    /**
     * 表名
     *
     * @return
     */
    @Override
    protected String getTableName() {
        return "matrix_external";
    }

    /**
     * 被引用者（上游）字段
     *
     * @return
     */
    @Override
    protected String getFromField() {
        return "integration_uuid";
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
                DependencyInfoVo dependencyInfoVo = new DependencyInfoVo();
                dependencyInfoVo.setValue(matrixVo.getUuid());
                dependencyInfoVo.setText(String.format("<a href=\"/%s/framework.html#/matrix-external-edit?uuid=%s&name=%s&type=%s\" target=\"_blank\">矩阵-%s</a>", TenantContext.get().getTenantUuid(), matrixVo.getUuid(), matrixVo.getName(), matrixVo.getType(), matrixVo.getName()));
                return dependencyInfoVo;
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
        return FromType.INTEGRATION;
    }
}
