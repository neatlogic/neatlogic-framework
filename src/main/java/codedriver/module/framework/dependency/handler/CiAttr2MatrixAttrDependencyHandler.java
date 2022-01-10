/*
 * Copyright(c) 2022 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.dependency.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dependency.constvalue.CalleeType;
import codedriver.framework.dependency.core.FixedTableDependencyHandlerBase;
import codedriver.framework.dependency.core.ICalleeType;
import codedriver.framework.dependency.dto.DependencyVo;
import codedriver.framework.matrix.dao.mapper.MatrixAttributeMapper;
import codedriver.framework.matrix.dao.mapper.MatrixMapper;
import codedriver.framework.matrix.dto.MatrixAttributeVo;
import codedriver.framework.matrix.dto.MatrixVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


/**
 * @author linbq
 * @since 2022/1/7 15:48
 **/
@Component
public class CiAttr2MatrixAttrDependencyHandler extends FixedTableDependencyHandlerBase {

    @Resource
    private MatrixMapper matrixMapper;
    @Resource
    private MatrixAttributeMapper matrixAttributeMapper;

    @Override
    protected ValueTextVo parse(DependencyVo caller) {
        JSONObject config = caller.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isNotBlank(matrixUuid)) {
                MatrixVo matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
                if (matrixVo != null) {
                    String toName = null;
                    String type = matrixVo.getType();
                    if ("custom".equals(type)) {
                        List<MatrixAttributeVo> matrixAttributeList = matrixAttributeMapper.getMatrixAttributeByMatrixUuid(matrixUuid);
                        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
                            for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
                                if (Objects.equals(matrixAttributeVo.getUuid(), caller.getTo())) {
                                    toName = matrixAttributeVo.getName();
                                }
                            }
                        }
                    } else if ("cmdbci".equals(type)) {

                    }
                    ValueTextVo valueTextVo = new ValueTextVo();
                    valueTextVo.setValue(matrixVo.getUuid());
                    valueTextVo.setText(String.format("<a href=\"/%s/framework.html#/matrix-view-edit?uuid=%s&name=%s&type=%s\" target=\"_blank\">矩阵-%s-%s</a>", TenantContext.get().getTenantUuid(), matrixVo.getUuid(), matrixVo.getName(), matrixVo.getType(), matrixVo.getName(), toName));
                    return valueTextVo;
                }
            }
        }
        return null;
    }

    @Override
    public ICalleeType getCalleeType() {
        return CalleeType.CMDBCIATTR;
    }
}
