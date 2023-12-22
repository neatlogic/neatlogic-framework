package neatlogic.module.framework.dependency.handler;

import com.alibaba.fastjson.JSONArray;
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
import neatlogic.module.framework.form.service.FormService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Matrix2FormAttributeDependencyHandler extends FixedTableDependencyHandlerBase {

    @Resource
    private FormMapper formMapper;

    @Resource
    private FormService formService;

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
        String sceneName = getSceneName(formVersionVo, config.getString("sceneUuid"));
        if (StringUtils.isNotBlank(sceneName)) {
            pathList.add(sceneName);
        }
        String lastName = getFormAttributeLabel(formVersionVo, dependencyVo.getTo());
        String urlFormat = "/" + TenantContext.get().getTenantUuid() + "/framework.html#/form-edit?uuid=${DATA.formUuid}&currentVersionUuid=${DATA.formVersionUuid}";
        return new DependencyInfoVo(dependencyVo.getTo(), dependencyInfoConfig, lastName, pathList, urlFormat, this.getGroupName());
    }

    private String getSceneName(FormVersionVo formVersion, String sceneUuid) {
        JSONObject formConfig = formVersion.getFormConfig();
        String uuid = formConfig.getString("uuid");
        if (Objects.equals(uuid, sceneUuid)) {
//            String name = formConfig.getString("name");
//            return name;
            return null;
        }
        JSONArray sceneList = formConfig.getJSONArray("sceneList");
        if (CollectionUtils.isNotEmpty(sceneList)) {
            for (int i = 0; i < sceneList.size(); i++) {
                JSONObject sceneObj = sceneList.getJSONObject(i);
                if (MapUtils.isEmpty(sceneObj)) {
                    continue;
                }
                uuid = sceneObj.getString("uuid");
                if (Objects.equals(uuid, sceneUuid)) {
                    String name = sceneObj.getString("name");
                    return name;
                }
            }
        }
        return null;
    }

    private String getFormAttributeLabel(FormVersionVo formVersion, String formAttributeUuid) {
        List<FormAttributeVo> allFormAttributeList = formService.getAllFormAttributeList(formVersion.getFormConfig());
        for (FormAttributeVo formAttribute : allFormAttributeList) {
            if (Objects.equals(formAttribute.getUuid(), formAttributeUuid)) {
                return formAttribute.getLabel();
            }
        }
        return "";
    }

    @Override
    public IFromType getFromType() {
        return FrameworkFromType.MATRIX;
    }
}
