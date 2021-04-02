/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.handler;

import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dependency.constvalue.CalleeType;
import codedriver.framework.dependency.core.DependencyHandlerBase;
import codedriver.framework.dependency.core.ICalleeType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: linbq
 * @since: 2021/4/1 11:42
 **/
@Service
public class MatrixFormAttributeDependencyHandler extends DependencyHandlerBase {
    @Override
    public String getTableName() {
        return "process_matrix_form_component";
    }

    @Override
    public String getCalleeField() {
        return "matrix_uuid";
    }

    @Override
    public String getCallerField() {
        return "form_verison_uuid";
    }

    @Override
    public ICalleeType getCalleeType() {
        return CalleeType.MATRIX;
    }

    @Override
    public List<ValueTextVo> getCallerList(Object callee, int startNum, int pageSize) {
        return null;
    }

    @Override
    protected ValueTextVo parse(Object caller) {
        return null;
    }
}
