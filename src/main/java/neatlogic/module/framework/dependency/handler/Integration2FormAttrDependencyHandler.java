/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
                        String mainSceneUuid = formVersionVo.getFormConfig().getString("uuid");
                        formVersionVo.setSceneUuid(mainSceneUuid);
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
