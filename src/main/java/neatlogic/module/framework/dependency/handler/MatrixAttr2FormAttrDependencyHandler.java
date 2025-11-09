/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.dependency.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dependency.constvalue.FrameworkFromType;
import neatlogic.framework.dependency.core.DefaultDependencyHandlerBase;
import neatlogic.framework.dependency.core.IFromType;
import neatlogic.framework.dependency.dto.DependencyInfoVo;
import neatlogic.framework.dependency.dto.DependencyVo;
import neatlogic.framework.form.dao.mapper.FormMapper;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.dto.FormVersionVo;
import neatlogic.framework.form.dto.FormVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表单属性引用矩阵属性关系处理器
 * @author linbq
 * @since 2022/1/7 15:50
 **/
@Component
public class MatrixAttr2FormAttrDependencyHandler extends DefaultDependencyHandlerBase {

    @Resource
    private FormMapper formMapper;

    @Override
    public int insert(Object from, Object to, JSONObject config) {
        return super.insert(from, getGenerateTo(to, config), config);
    }

    @Override
    public int delete(Object to, JSONObject config) {
        int count = super.delete(getGenerateTo(to, config), config);
        if (count == 0) {
            count = super.delete(to, config);
        }
        return count;
    }

    @Override
    protected DependencyInfoVo parse(DependencyVo dependencyVo) {
        JSONObject config = dependencyVo.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            String formVersionUuid = config.getString("formVersionUuid");
            String uuid = config.getString("uuid");
            if (StringUtils.isNotBlank(formVersionUuid)) {
                FormVersionVo formVersionVo = formMapper.getFormVersionByUuid(formVersionUuid);
                if (formVersionVo != null) {
                    FormVo formVo = formMapper.getFormByUuid(formVersionVo.getFormUuid());
                    if (formVo != null) {
                        String mainSceneUuid = formVersionVo.getFormConfig().getString("uuid");
                        formVersionVo.setSceneUuid(mainSceneUuid);
                        List<FormAttributeVo> formAttributeList = formVersionVo.getFormAttributeList();
                        if (CollectionUtils.isNotEmpty(formAttributeList)) {
                            for (FormAttributeVo formAttributeVo : formAttributeList) {
                                if (Objects.equals(formAttributeVo.getUuid(), dependencyVo.getTo()) || Objects.equals(formAttributeVo.getUuid(), uuid)) {
                                    JSONObject dependencyInfoConfig = new JSONObject();
                                    dependencyInfoConfig.put("formUuid", formVo.getUuid());
//                                    dependencyInfoConfig.put("formName", formVo.getName());
//                                    dependencyInfoConfig.put("formVersion", formVersionVo.getVersion());
                                    dependencyInfoConfig.put("formVersionUuid", formVersionVo.getUuid());
//                                    dependencyInfoConfig.put("attributeLabel", formAttributeVo.getLabel());
                                    List<String> pathList = new ArrayList<>();
                                    pathList.add("表单管理");
                                    pathList.add(formVo.getName());
                                    pathList.add(formVersionVo.getVersion().toString());
                                    String lastName = formAttributeVo.getLabel();
//                                    String pathFormat = "表单-${DATA.formName}-${DATA.formVersion}-${DATA.attributeLabel}";
                                    String urlFormat = "/" + TenantContext.get().getTenantUuid() + "/framework.html#/form-edit?uuid=${DATA.formUuid}&currentVersionUuid=${DATA.formVersionUuid}";
                                    return new DependencyInfoVo(formAttributeVo.getUuid(), dependencyInfoConfig, lastName, pathList, urlFormat, this.getGroupName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public IFromType getFromType() {
        return FrameworkFromType.MATRIXATTR;
    }

    /**
     * 由于不同表单或不同版本的属性uuid可能是相同的，所以这里需要重新生成to
     * @param to
     * @param config
     * @return
     */
    private String getGenerateTo(Object to, JSONObject config) {
        if (MapUtils.isNotEmpty(config)) {
            String str = to.toString();
            String formUuid = config.getString("formUuid");
            String formVersionUuid = config.getString("formVersionUuid");
            String sceneUuid = config.getString("sceneUuid");
            if (StringUtils.isNotBlank(sceneUuid)) {
                str += "&" + sceneUuid;
            }
            if (StringUtils.isNotBlank(formVersionUuid)) {
                str += "&" + formVersionUuid;
            }
            if (StringUtils.isNotBlank(formUuid)) {
                str += "&" + formUuid;
            }
            return DigestUtils.md5DigestAsHex(str.getBytes());
        }
        return to.toString();
    }
}
