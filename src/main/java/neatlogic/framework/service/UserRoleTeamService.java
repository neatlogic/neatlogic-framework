package neatlogic.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.restful.groupsearch.core.GroupSearchGroupVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchVo;

import java.util.List;

public interface UserRoleTeamService {
    List<GroupSearchGroupVo> searchUserRoleTeam(GroupSearchVo groupSearchVo);
}
