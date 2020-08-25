package codedriver.framework.counter.dto;

/**
 * @program: balantflow
 * @description: 用户排序数据表
 * @create: 2019-08-16 10:22
 **/
public class GlobalCounterUserSortVo {

    private String pluginId;
    private String userUuid;
    private Integer sort;

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
