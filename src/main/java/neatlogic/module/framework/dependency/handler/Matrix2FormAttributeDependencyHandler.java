package neatlogic.module.framework.dependency.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dependency.constvalue.FrameworkFromType;
import neatlogic.framework.dependency.core.DefaultDependencyHandlerBase;
import neatlogic.framework.dependency.core.IFromType;
import neatlogic.framework.dependency.dto.DependencyInfoVo;
import neatlogic.framework.dependency.dto.DependencyVo;
import neatlogic.framework.form.dao.mapper.FormMapper;
import neatlogic.framework.form.dto.FormAttributeParentVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.dto.FormVersionVo;
import neatlogic.framework.form.dto.FormVo;
import neatlogic.framework.util.FormUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class Matrix2FormAttributeDependencyHandler extends DefaultDependencyHandlerBase {

    @Resource
    private FormMapper formMapper;

    @Override
    protected DependencyInfoVo parse(DependencyVo dependencyVo) {
        JSONObject config = dependencyVo.getConfig();
        if (MapUtils.isEmpty(config)) {
            return null;
        }
        String formVersionUuid = config.getString("formVersionUuid");
        if (StringUtils.isBlank(formVersionUuid)) {
            return null;
        }
        FormVersionVo formVersionVo = formMapper.getFormVersionByUuid(formVersionUuid);
        if (formVersionVo == null) {
            return null;
        }
        FormVo formVo = formMapper.getFormByUuid(formVersionVo.getFormUuid());
        if (formVo == null) {
            return null;
        }
        JSONObject dependencyInfoConfig = new JSONObject();
        dependencyInfoConfig.put("formUuid", formVo.getUuid());
        dependencyInfoConfig.put("formVersionUuid", formVersionVo.getUuid());
        List<String> pathList = new ArrayList<>();
        pathList.add("表单管理");
        pathList.add(formVo.getName());
        pathList.add(formVersionVo.getVersion().toString());
        String sceneUuid = config.getString("sceneUuid");
        FormAttributeVo formAttribute = FormUtil.getFormAttribute(formVersionVo.getFormConfig(), dependencyVo.getTo(), sceneUuid);
        if (formAttribute == null) {
            return null;
        }
        List<String> parentNameList = new ArrayList<>();
        FormAttributeParentVo parent = formAttribute.getParent();
        while (parent != null) {
            parentNameList.add(parent.getName());
            parent = parent.getParent();
        }
        for (int i = parentNameList.size() - 1; i >= 0; i--) {
            pathList.add(parentNameList.get(i));
        }
        String lastName = formAttribute.getLabel();
        String urlFormat = "/" + TenantContext.get().getTenantUuid() + "/framework.html#/form-edit?uuid=${DATA.formUuid}&currentVersionUuid=${DATA.formVersionUuid}";
        return new DependencyInfoVo(dependencyVo.getTo(), dependencyInfoConfig, lastName, pathList, urlFormat, this.getGroupName());
    }

    @Override
    public IFromType getFromType() {
        return FrameworkFromType.MATRIX;
    }
}
