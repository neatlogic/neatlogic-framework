/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.datawarehouse.condition.handler;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.datawarehouse.condition.IDatasourceConditionHandler;
import org.apache.commons.lang3.StringUtils;

public class UserSelectConditionHandler implements IDatasourceConditionHandler {
    @Override
    public String getExpression(Long fieldId, Object value) {
        if (value != null) {
            if (value instanceof String) {
                String v = value.toString();
                if (v.startsWith(GroupSearch.ROLE.getValuePlugin())) {
                    v = v.replace(GroupSearch.ROLE.getValuePlugin(), StringUtils.EMPTY);
                } else if (v.startsWith(GroupSearch.USER.getValuePlugin())) {
                    v = v.replace(GroupSearch.USER.getValuePlugin(), StringUtils.EMPTY);
                } else if (v.startsWith(GroupSearch.TEAM.getValuePlugin())) {
                    v = v.replace(GroupSearch.TEAM.getValuePlugin(), StringUtils.EMPTY);
                } else if (v.startsWith(GroupSearch.COMMON.getValuePlugin())) {
                    //暂时只支持当前登陆用户
                    v = v.replace(GroupSearch.COMMON.getValuePlugin(), StringUtils.EMPTY);
                    if (v.equals(UserType.LOGIN_USER.getValue())) {
                        v = UserContext.get().getUserUuid();
                    } else {
                        v = "";
                    }
                }
                if (StringUtils.isNotBlank(v)) {
                    return "`" + fieldId + "` = '" + v + "'";
                }
            } else if (value instanceof JSONArray) {
                JSONArray valueList = (JSONArray) value;
                String vs = "";
                for (int i = 0; i < valueList.size(); i++) {
                    String v = valueList.getString(i);
                    if (v.startsWith(GroupSearch.ROLE.getValuePlugin())) {
                        v = v.replace(GroupSearch.ROLE.getValuePlugin(), StringUtils.EMPTY);
                    } else if (v.startsWith(GroupSearch.USER.getValuePlugin())) {
                        v = v.replace(GroupSearch.USER.getValuePlugin(), StringUtils.EMPTY);
                    } else if (v.startsWith(GroupSearch.TEAM.getValuePlugin())) {
                        v = v.replace(GroupSearch.TEAM.getValuePlugin(), StringUtils.EMPTY);
                    } else if (v.startsWith(GroupSearch.COMMON.getValuePlugin())) {
                        //暂时只支持当前登陆用户
                        v = v.replace(GroupSearch.COMMON.getValuePlugin(), StringUtils.EMPTY);
                        if (v.equals(UserType.LOGIN_USER.getValue())) {
                            v = UserContext.get().getUserUuid();
                        } else {
                            v = "";
                        }
                    }
                    if (StringUtils.isNotBlank(v)) {
                        if (StringUtils.isNotBlank(vs)) {
                            vs += ",";
                        }
                        vs += "'" + v + "'";
                    }
                }
                if (StringUtils.isNotBlank(vs)) {
                    return "`" + fieldId + "` in (" + vs + ")";
                }
            }
        }
        return null;
    }

    public String getName() {
        return "userselect";
    }
}
