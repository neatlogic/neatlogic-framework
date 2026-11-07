package neatlogic.framework.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 统一保存权限的角色、所属分组及包含链来源，展示文案由调用方处理。 */
@JSONType(serialzeFeatures = SerializerFeature.WriteMapNullValue)
public class SourcePathVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @EntityField(name = "角色uuid", type = ApiParamType.STRING)
    private String roleUuid;
    @EntityField(name = "角色名称", type = ApiParamType.STRING)
    private String roleName;
    @EntityField(name = "用户所属分组uuid", type = ApiParamType.STRING)
    private String teamUuid;
    @EntityField(name = "用户所属分组完整名称路径", type = ApiParamType.JSONARRAY)
    private List<String> teamPathNameList;
    @EntityField(name = "用户所属分组左编码", type = ApiParamType.INTEGER)
    private Integer teamLft;
    // 普通来源为null并参与JSON输出；包含来源按根权限到目标权限保存完整链。
    @EntityField(name = "权限包含路径", type = ApiParamType.JSONARRAY)
    private List<JSONObject> includeAuthPathList;

    /** 支持强类型来源的JSON反序列化及按字段构造。 */
    public SourcePathVo() {
    }

    /** 保留原五参构造，复制分组路径，防止同分组多条权限记录共享可变列表。 */
    public SourcePathVo(String roleUuid, String roleName, String teamUuid, List<String> teamPathNameList, Integer teamLft) {
        this.roleUuid = roleUuid;
        this.roleName = roleName;
        this.teamUuid = teamUuid;
        this.teamPathNameList = teamPathNameList == null ? null : new ArrayList<>(teamPathNameList);
        this.teamLft = teamLft;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getTeamUuid() {
        return teamUuid;
    }

    public void setTeamUuid(String teamUuid) {
        this.teamUuid = teamUuid;
    }

    public List<String> getTeamPathNameList() {
        return teamPathNameList;
    }

    public void setTeamPathNameList(List<String> teamPathNameList) {
        this.teamPathNameList = teamPathNameList;
    }

    public Integer getTeamLft() {
        return teamLft;
    }

    public void setTeamLft(Integer teamLft) {
        this.teamLft = teamLft;
    }

    public List<JSONObject> getIncludeAuthPathList() {
        return includeAuthPathList;
    }

    public void setIncludeAuthPathList(List<JSONObject> includeAuthPathList) {
        this.includeAuthPathList = includeAuthPathList;
    }
}
