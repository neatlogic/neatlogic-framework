package codedriver.framework.news.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

import java.util.Date;

/**
 * @Title: NewsHandlerVo
 * @Package codedriver.framework.news.dto
 * @Description: 消息类型处理器Vo
 * @Author: linbq
 * @Date: 2020/12/30 18:33
 **/
public class NewsHandlerVo implements Cloneable {
    @EntityField(name = "模块id", type = ApiParamType.STRING)
    private String moduleId;
    @EntityField(name = "模块名", type = ApiParamType.STRING)
    private String moduleName;
    @EntityField(name = "消息类型处理器全类名", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "消息类型处理器名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "描述", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "弹框方式", type = ApiParamType.STRING)
    private String popUp;

    private transient Date fcd;
    private transient String userUuid;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPopUp() {
        return popUp;
    }

    public void setPopUp(String popUp) {
        this.popUp = popUp;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Date getFcd() {
        return fcd;
    }

    public void setFcd(Date fcd) {
        this.fcd = fcd;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    @Override
    public NewsHandlerVo clone() throws CloneNotSupportedException {
        return (NewsHandlerVo) super.clone();
    }
}
