/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.service;

import codedriver.framework.dependency.core.DependencyManager;
import codedriver.framework.form.attribute.core.FormAttributeHandlerFactory;
import codedriver.framework.form.attribute.core.IFormAttributeHandler;
import codedriver.framework.form.dao.mapper.FormMapper;
import codedriver.framework.form.dto.FormAttributeMatrixVo;
import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.form.exception.FormAttributeHandlerNotFoundException;
import codedriver.framework.form.service.IFormCrossoverService;
import codedriver.module.framework.dependency.handler.Integration2FormAttrDependencyHandler;
import codedriver.module.framework.dependency.handler.MatrixAttr2FormAttrDependencyHandler;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Service
public class FormServiceImpl implements FormService, IFormCrossoverService {

    @Resource
    private FormMapper formMapper;

    /**
     * 保存表单属性与其他功能的引用关系
     * @param formAttributeVo
     */
    @Override
    public void saveDependency(FormAttributeVo formAttributeVo) {
        String formUuid = formAttributeVo.getFormUuid();
        String formVersionUuid = formAttributeVo.getFormVersionUuid();
        IFormAttributeHandler formAttributeHandler = FormAttributeHandlerFactory.getHandler(formAttributeVo.getHandler());
        if (formAttributeHandler == null) {
            throw new FormAttributeHandlerNotFoundException(formAttributeVo.getHandler());
        }
        formAttributeHandler.makeupFormAttribute(formAttributeVo);
        Set<String> matrixUuidSet = formAttributeVo.getMatrixUuidSet();
        if (CollectionUtils.isNotEmpty(matrixUuidSet)) {
            for (String matrixUuid : matrixUuidSet) {
                FormAttributeMatrixVo formAttributeMatrixVo = new FormAttributeMatrixVo();
                formAttributeMatrixVo.setMatrixUuid(matrixUuid);
                formAttributeMatrixVo.setFormVersionUuid(formVersionUuid);
                formAttributeMatrixVo.setFormAttributeLabel(formAttributeVo.getLabel());
                formAttributeMatrixVo.setFormAttributeUuid(formAttributeVo.getUuid());
                formMapper.insertFormAttributeMatrix(formAttributeMatrixVo);
            }
        }

        Set<String> integrationUuidSet = formAttributeVo.getIntegrationUuidSet();
        if (CollectionUtils.isNotEmpty(integrationUuidSet)) {
            JSONObject config = new JSONObject();
            config.put("formUuid", formUuid);
            config.put("formVersionUuid", formVersionUuid);
            config.put("formAttributeUuid", formAttributeVo.getUuid());
            for (String integrationUuid : integrationUuidSet) {
                config.put("integrationUuid", integrationUuid);
                DependencyManager.insert(Integration2FormAttrDependencyHandler.class, integrationUuid, formAttributeVo.getUuid(), config);
            }
        }

        Map<String, Set<String>> matrixUuidAttributeUuidSetMap = formAttributeVo.getMatrixUuidAttributeUuidSetMap();
        if (MapUtils.isNotEmpty(matrixUuidAttributeUuidSetMap)) {
            JSONObject config = new JSONObject();
            config.put("formUuid", formUuid);
            config.put("formVersionUuid", formVersionUuid);
            for (Map.Entry<String, Set<String>> entry : matrixUuidAttributeUuidSetMap.entrySet()) {
                String matrixUuid = entry.getKey();
                config.put("matrixUuid", matrixUuid);
                Set<String> attributeUuidSet = entry.getValue();
                if (CollectionUtils.isNotEmpty(attributeUuidSet)) {
                    for (String attributeUuid : attributeUuidSet) {
                        DependencyManager.insert(MatrixAttr2FormAttrDependencyHandler.class, attributeUuid, formAttributeVo.getUuid(), config);
                    }
                }
            }
        }
    }
}
