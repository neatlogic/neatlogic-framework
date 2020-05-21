package codedriver.framework.counter.dto;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

import java.util.Objects;

/**
 * @program: balantflow
 * @description: 消息统计插件实体类
 * @create: 2019-09-10 15:13
 **/
public class GlobalCounterVo extends BasePageVo implements Comparable<GlobalCounterVo> {
    //数据库对应

    @EntityField( name = "插件名称", type = ApiParamType.STRING)
    private String name;
    @EntityField( name = "插件ID", type = ApiParamType.STRING)
    private String pluginId;
    @EntityField( name = "模块描述", type = ApiParamType.STRING)
    private String description;
    @EntityField( name = "模块ID", type = ApiParamType.STRING)
    private String moduleId;
    //扩充字段
    @EntityField( name = "预览图路径", type = ApiParamType.STRING)
    private String previewPath;
    @EntityField( name = "展示模板路径", type = ApiParamType.STRING)
    private String showTemplate;
    @EntityField( name = "排序编码", type = ApiParamType.INTEGER)
    private Integer sort = Integer.MAX_VALUE;
    private String moduleDesc;
    private String moduleIcon;
    private String moduleName;
    @EntityField( name = "模块订阅者信息", type = ApiParamType.JSONOBJECT)
    private GlobalCounterSubscribeVo counterSubscribeVo;

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleDesc() {
        return moduleDesc;
    }

    public void setModuleDesc(String moduleDesc) {
        this.moduleDesc = moduleDesc;
    }

    public String getModuleIcon() {
        return moduleIcon;
    }

    public void setModuleIcon(String moduleIcon) {
        this.moduleIcon = moduleIcon;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public GlobalCounterSubscribeVo getCounterSubscribeVo() {
        return counterSubscribeVo;
    }

    public void setCounterSubscribeVo(GlobalCounterSubscribeVo counterSubscribeVo) {
        this.counterSubscribeVo = counterSubscribeVo;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public String getShowTemplate() {
        return showTemplate;
    }

    public void setShowTemplate(String showTemplate) {
        this.showTemplate = showTemplate;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public int compareTo(GlobalCounterVo o) {
        return this.getSort().compareTo(o.getSort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GlobalCounterVo)){
            return false;
        }
        GlobalCounterVo counterVo = (GlobalCounterVo) o;
        return Objects.equals(sort, counterVo.sort);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result + sort.hashCode();
        return result;
    }
}
