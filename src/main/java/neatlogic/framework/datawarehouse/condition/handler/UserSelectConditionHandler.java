/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.datawarehouse.condition.handler;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.datawarehouse.condition.IDatasourceConditionHandler;
import com.alibaba.fastjson.JSONArray;
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
