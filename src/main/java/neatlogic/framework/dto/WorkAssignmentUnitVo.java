package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.restful.annotation.EntityField;

/**
 * @Title: WorkUnitVo
 * @Package neatlogic.framework.dto
 * @Description: 工作分配单元
 * @Author: linbq
 * @Date: 2021/1/14 18:37

 **/
public class WorkAssignmentUnitVo {
    @EntityField(name = "common.type", type = ApiParamType.STRING)
    private String initType;
    @EntityField(name = "uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "common.name", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "term.framework.user.viplevel", type = ApiParamType.ENUM)
    private Integer vipLevel;
    @EntityField(name = "common.avatar", type = ApiParamType.STRING)
    private String avatar;
    @EntityField(name = "term.framework.pinyin", type = ApiParamType.STRING)
    private String pinyin;
    @EntityField(name = "common.isdeleted", type = ApiParamType.ENUM)
    private Integer isDelete;

    public WorkAssignmentUnitVo(UserVo userVo){
        this.initType = GroupSearch.USER.getValue();
        this.uuid = userVo.getUuid();
        this.name = userVo.getUserName();
        this.vipLevel = userVo.getVipLevel();
        this.avatar = userVo.getAvatar();
        this.pinyin = userVo.getPinyin();
        this.isDelete = userVo.getIsDelete();
    }
    public WorkAssignmentUnitVo(TeamVo teamVo){
        this.initType = GroupSearch.TEAM.getValue();
        this.uuid = teamVo.getUuid();
        this.name = teamVo.getName();
        this.isDelete = teamVo.getIsDelete();
    }
    public WorkAssignmentUnitVo(RoleVo roleVo){
        this.initType = GroupSearch.ROLE.getValue();
        this.uuid = roleVo.getUuid();
        this.name = roleVo.getName();
        this.isDelete = roleVo.getIsDelete();
    }
    public String getInitType() {
        return initType;
    }

    public void setInitType(String initType) {
        this.initType = initType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
