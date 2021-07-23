/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.dto;

/**
 * @author linbq
 * @since 2021/7/23 15:30
 **/
public class NotifyPolicyHandlerVo {
    private String handler;
    private String name;
    private String moduleGroup;
    private String authName;

    public NotifyPolicyHandlerVo(String handler, String name, String authName, String moduleGroup) {
        this.handler = handler;
        this.name = name;
        this.authName = authName;
        this.moduleGroup = moduleGroup;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModuleGroup() {
        return moduleGroup;
    }

    public void setModuleGroup(String moduleGroup) {
        this.moduleGroup = moduleGroup;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }
}
