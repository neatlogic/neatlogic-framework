package codedriver.framework.reminder.dto;


import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.reminder.dto.param.GlobalReminderParamVo;
import codedriver.framework.restful.annotation.EntityField;
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
public class GlobalReminderVo extends BasePageVo implements Comparable<GlobalReminderVo>{
    //数据库对应
    @EntityField( name = "插件名称", type = ApiParamType.STRING)
    private String name;
    @EntityField( name = "插件ID", type = ApiParamType.STRING)
    private String pluginId;
    @EntityField( name = "插件描述", type = ApiParamType.STRING)
    private String description;
    @EntityField( name = "插件配置信息", type = ApiParamType.STRING)
    private String config;
    @EntityField( name = "模块ID", type = ApiParamType.STRING)
    private String moduleId;
    @EntityField( name = "模块名称", type = ApiParamType.STRING)
    private String moduleName;
    private String moduleIcon;
    private String moduleDesc;
    private String configValue;
    private String userId;
    private String userName;
    @EntityField( name = "插件订阅者信息", type = ApiParamType.JSONOBJECT)
    private GlobalReminderSubscribeVo reminderSubscribeVo;
    @EntityField( name = "插件默认参数集合", type = ApiParamType.JSONARRAY)
    private List<GlobalReminderParamVo> reminderParamList;

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
        return this.pluginId.compareTo(obj.getPluginId());
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
        return Objects.equals(pluginId, reminderVo.pluginId);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result + pluginId.hashCode();
        return result;
    }
}
