package codedriver.framework.reminder.dto;


import codedriver.framework.reminder.dto.param.GlobalReminderParamVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Objects;

/**
 * @program: balantflow
 * @description: 实时动态插件实体类
 * @create: 2019-09-10 15:10
 **/
public class GlobalReminderVo implements Comparable<GlobalReminderVo>{
    //数据库对应
    private Long id;
    private String name;
    private String pluginId;
    private String description;
    private String config;
    private String moduleId;
    private int isActive;

    private String moduleName;
    private String moduleIcon;
    private String moduleDesc;
    private String configValue;
    private String userId;
    private String userName;

    private GlobalReminderSubscribeVo reminderSubscribeVo;
    private List<GlobalReminderParamVo> reminderParamList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfig() {
        if (reminderParamList != null && reminderParamList.size() > 0){
            return JSON.toJSONString(reminderParamList);
        }
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfigValue() {
        if (config != null && config != ""){
            JSONArray configArray = JSONArray.parseArray(config);
            JSONObject returnObj = new JSONObject();
            for (int i = 0 ;i < configArray.size(); i++){
                JSONObject data = configArray.getJSONObject(i);
                returnObj.put(data.getString("name"), data.getString("defaultValue"));
            }
            return returnObj.toString();
        }
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<GlobalReminderParamVo> getReminderParamList() {
        return reminderParamList;
    }

    public void setReminderParamList(List<GlobalReminderParamVo> reminderParamList) {
        this.reminderParamList = reminderParamList;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public GlobalReminderSubscribeVo getReminderSubscribeVo() {
        return reminderSubscribeVo;
    }

    public void setReminderSubscribeVo(GlobalReminderSubscribeVo reminderSubscribeVo) {
        this.reminderSubscribeVo = reminderSubscribeVo;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleIcon() {
        return moduleIcon;
    }

    public void setModuleIcon(String moduleIcon) {
        this.moduleIcon = moduleIcon;
    }

    public String getModuleDesc() {
        return moduleDesc;
    }

    public void setModuleDesc(String moduleDesc) {
        this.moduleDesc = moduleDesc;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int compareTo(GlobalReminderVo obj) {
        return this.id.compareTo(obj.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GlobalReminderVo)){
            return false;
        }
        GlobalReminderVo reminderVo = (GlobalReminderVo) o;
        return Objects.equals(id, reminderVo.id);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result + id.hashCode();
        return result;
    }
}
