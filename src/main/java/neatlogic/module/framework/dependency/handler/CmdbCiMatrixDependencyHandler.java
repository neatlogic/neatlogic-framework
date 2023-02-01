/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
