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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dependency.constvalue.FrameworkFromType;
import neatlogic.framework.dependency.core.FixedTableDependencyHandlerBase;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表单属性引用集成属性关系处理器
 * @author linbq
 * @since 2022/1/7 15:50
 **/
//@Component
@Deprecated
public class Integration2FormAttrDependencyHandler extends FixedTableDependencyHandlerBase {

    @Resource
    private FormMapper formMapper;

    @Override
    protected DependencyInfoVo parse(DependencyVo dependencyVo) {
        JSONObject config = dependencyVo.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            String formVersionUuid = config.getString("formVersionUuid");
            if (StringUtils.isNotBlank(formVersionUuid)) {
                FormVersionVo formVersionVo = formMapper.getFormVersionByUuid(formVersionUuid);
                if (formVersionVo != null) {
                    FormVo formVo = formMapper.getFormByUuid(formVersionVo.getFormUuid());
                    if (formVo != null) {
                        List<FormAttributeVo> formAttributeList = formVersionVo.getFormAttributeList();
                        if (CollectionUtils.isNotEmpty(formAttributeList)) {
                            for (FormAttributeVo formAttributeVo : formAttributeList) {
                                if (Objects.equals(formAttributeVo.getUuid(), dependencyVo.getTo())) {
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
        return FrameworkFromType.INTEGRATION;
    }
}
