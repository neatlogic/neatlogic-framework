/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.condition.handler;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.datawarehouse.condition.IDatasourceConditionHandler;
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
                }
                if (StringUtils.isNotBlank(v)) {
                    return "`" + fieldId + "` = '" + v + "'";
                }
            } else if (value instanceof JSONArray) {
                JSONArray valueList = (JSONArray) value;
                String vs = "";
                for (int i = 0; i < valueList.size(); i++) {
                    if (StringUtils.isNotBlank(vs)) {
                        vs += ",";
                    }
                    String v = valueList.getString(i);
                    if (v.startsWith(GroupSearch.ROLE.getValuePlugin())) {
                        v = v.replace(GroupSearch.ROLE.getValuePlugin(), StringUtils.EMPTY);
                    } else if (v.startsWith(GroupSearch.USER.getValuePlugin())) {
                        v = v.replace(GroupSearch.USER.getValuePlugin(), StringUtils.EMPTY);
                    } else if (v.startsWith(GroupSearch.TEAM.getValuePlugin())) {
                        v = v.replace(GroupSearch.TEAM.getValuePlugin(), StringUtils.EMPTY);
                    }
                    vs += "'" + v + "'";
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
