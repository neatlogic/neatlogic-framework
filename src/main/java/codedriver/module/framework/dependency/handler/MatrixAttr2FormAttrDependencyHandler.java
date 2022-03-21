/*
 * Copyright(c) 2022 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.dependency.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dependency.constvalue.FrameworkFromType;
import codedriver.framework.dependency.core.FixedTableDependencyHandlerBase;
import codedriver.framework.dependency.core.IFromType;
import codedriver.framework.dependency.dto.DependencyInfoVo;
import codedriver.framework.dependency.dto.DependencyVo;
import codedriver.framework.form.dao.mapper.FormMapper;
import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.form.dto.FormVersionVo;
import codedriver.framework.form.dto.FormVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 表单属性引用矩阵属性关系处理器
 * @author linbq
 * @since 2022/1/7 15:50
 **/
@Component
public class MatrixAttr2FormAttrDependencyHandler extends FixedTableDependencyHandlerBase {

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
                                    dependencyInfoConfig.put("formName", formVo.getName());
                                    dependencyInfoConfig.put("formVersion", formVersionVo.getVersion());
                                    dependencyInfoConfig.put("formVersionUuid", formVersionVo.getUuid());
                                    dependencyInfoConfig.put("attributeLabel", formAttributeVo.getLabel());
                                    String pathFormat = "表单-${DATA.formName}-${DATA.formVersion}-${DATA.attributeLabel}";
                                    String urlFormat = "/" + TenantContext.get().getTenantUuid() + "/framework.html#/form-edit?uuid=${DATA.formUuid}&currentVersionUuid=${DATA.formVersionUuid}";
                                    return new DependencyInfoVo(formAttributeVo.getUuid(), dependencyInfoConfig, pathFormat, urlFormat, this.getGroupName());
//                                    DependencyInfoVo dependencyInfoVo = new DependencyInfoVo();
//                                    dependencyInfoVo.setValue(formAttributeVo.getUuid());
//                                    String text = String.format("<a href=\"/%s/framework.html#/form-edit?uuid=%s&currentVersionUuid=%s\" target=\"_blank\">%s</a>",
//                                            TenantContext.get().getTenantUuid(),
//                                            formVo.getUuid(),
//                                            formVersionVo.getUuid(),
//                                            formVo.getName() + "-" + formVersionVo.getVersion() + "-" + formAttributeVo.getLabel());
//                                    dependencyInfoVo.setText(text);
//                                    return dependencyInfoVo;
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
}
