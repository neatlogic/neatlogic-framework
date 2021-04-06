/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dependency.constvalue.CalleeType;
import codedriver.framework.dependency.core.DependencyHandlerBase;
import codedriver.framework.dependency.core.ICalleeType;
import codedriver.framework.matrix.dao.mapper.MatrixMapper;
import codedriver.framework.matrix.dto.MatrixVo;

import javax.annotation.Resource;

/**
 * @author: linbq
 * @since: 2021/4/6 15:21
 **/
public class IntegrationMatrixDependencyHandler extends DependencyHandlerBase {

    @Resource
    private MatrixMapper matrixMapper;

    /**
     * 表名
     *
     * @return
     */
    @Override
    protected String getTableName() {
        return "process_matrix_external";
    }

    /**
     * 被调用者字段
     *
     * @return
     */
    @Override
    protected String getCalleeField() {
        return "integration_uuid";
    }

    /**
     * 调用者字段
     *
     * @return
     */
    @Override
    protected String getCallerField() {
        return "matrix_uuid";
    }

    /**
     * 解析数据，拼装跳转url
     *
     * @param caller
     * @return
     */
    @Override
    protected ValueTextVo parse(Object caller) {
        MatrixVo matrixVo = matrixMapper.getMatrixByUuid((String) caller);
        if(matrixVo != null){
            ValueTextVo valueTextVo = new ValueTextVo();
            valueTextVo.setValue(caller);
            valueTextVo.setText(String.format("<a href=\"/%s/framework.html#/matrix-edit?uuid=%s&name=%s&type=%s\">%s</a>", TenantContext.get().getTenantUuid(), matrixVo.getUuid(), matrixVo.getName(), matrixVo.getType(), matrixVo.getName()));
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
        return CalleeType.INTEGRATION;
    }
}
