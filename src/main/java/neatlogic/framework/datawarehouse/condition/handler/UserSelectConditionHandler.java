/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
