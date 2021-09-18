/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.dependency.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dependency.constvalue.CalleeType;
import codedriver.framework.dependency.core.DependencyHandlerBase;
import codedriver.framework.dependency.core.ICalleeType;
import codedriver.framework.form.dao.mapper.FormMapper;
import codedriver.framework.form.dto.FormAttributeMatrixVo;
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
public class MatrixFormAttributeDependencyHandler extends DependencyHandlerBase {

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
     * 被调用者字段
     *
     * @return
     */
    @Override
    protected String getCalleeField() {
        return "matrix_uuid";
    }

    /**
     * 调用者字段
     *
     * @return
     */
    @Override
    protected String getCallerField() {
        return "form_verison_uuid";
    }

    @Override
    protected List<String> getCallerFieldList() {
        return null;
    }

    /**
     * 解析数据，拼装跳转url，返回引用下拉列表一个选项数据结构
     *
     * @param caller 调用者值
     * @return
     */
    @Override
    protected ValueTextVo parse(Object caller) {
        if (caller == null) {
            return null;
        }
        if (caller instanceof FormAttributeMatrixVo) {
            FormAttributeMatrixVo formAttributeMatrixVo = (FormAttributeMatrixVo) caller;
            ValueTextVo valueTextVo = new ValueTextVo();
            valueTextVo.setValue(formAttributeMatrixVo.getFormAttributeUuid());
            String text = String.format("<a href=\"/%s/framework.html#/form-edit?uuid=%s&currentVersionUuid=%s\" target=\"_blank\">%s</a>",
                    TenantContext.get().getTenantUuid(),
                    formAttributeMatrixVo.getFormUuid(),
                    formAttributeMatrixVo.getFormVersionUuid(),
                    formAttributeMatrixVo.getFormName() + "-" + formAttributeMatrixVo.getVersion() + "-" + formAttributeMatrixVo.getFormAttributeLabel());
            valueTextVo.setText(text);
            return valueTextVo;
        }
        return null;
    }

    /**
     * 被调用方名
     *
     * @return
     */
    @Override
    public ICalleeType getCalleeType() {
        return CalleeType.MATRIX;
    }

    /**
     * 查询引用列表数据
     *
     * @param callee   被调用者值（如：服务时间窗口uuid）
     * @param startNum 开始行号
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public List<ValueTextVo> getCallerList(Object callee, int startNum, int pageSize) {
        List<ValueTextVo> resultList = new ArrayList<>();
        List<FormAttributeMatrixVo> callerList = formMapper.getFormAttributeMatrixByMatrixUuid((String) callee, startNum, pageSize);
        for (FormAttributeMatrixVo caller : callerList) {
            ValueTextVo valueTextVo = parse(caller);
            if (valueTextVo != null) {
                resultList.add(valueTextVo);
            }
        }
        return resultList;
    }
}