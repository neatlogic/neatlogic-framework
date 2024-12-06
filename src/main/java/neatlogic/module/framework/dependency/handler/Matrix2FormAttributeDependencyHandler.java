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
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class Matrix2FormAttributeDependencyHandler extends DefaultDependencyHandlerBase {

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
        String uuid = config.getString("uuid");
        FormAttributeVo formAttribute = FormUtil.getFormAttribute(formVersionVo.getFormConfig(), uuid, sceneUuid);
        if (formAttribute == null) {
            formAttribute = FormUtil.getFormAttribute(formVersionVo.getFormConfig(), dependencyVo.getTo(), sceneUuid);
            if (formAttribute == null) {
                return null;
            }
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
