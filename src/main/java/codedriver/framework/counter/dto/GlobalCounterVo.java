package codedriver.framework.counter.dto;

import java.util.Objects;

/**
 * @program: balantflow
 * @description: 消息统计插件实体类
 * @create: 2019-09-10 15:13
 **/
public class GlobalCounterVo implements Comparable<GlobalCounterVo> {
    //数据库对应
    private Long id;
    private String name;
    private String pluginId;
    private String description;
    private String moduleId;
    private int isActive;
    //扩充字段
    private String userId;
    private String previewPath;
    private String showTemplate;
    private Integer sort = Integer.MAX_VALUE;
    private String moduleDesc;
    private String moduleIcon;
    private String moduleName;

    private GlobalCounterSubscribeVo counterSubscribeVo;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
