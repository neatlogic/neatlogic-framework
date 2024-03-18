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

package neatlogic.module.framework.groupsearch.handler;

import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.restful.groupsearch.core.GroupSearchOptionVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchVo;
import neatlogic.framework.restful.groupsearch.core.IGroupSearchHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonGroupHandler implements IGroupSearchHandler {
    @Override
    public String getName() {
        return GroupSearch.COMMON.getValue();
    }

    @Override
    public String getLabel() {
        return GroupSearch.COMMON.getText();
    }

    @Override
    public String getHeader() {
        return GroupSearch.COMMON.getValuePlugin();
    }

    @Override
    public List<GroupSearchOptionVo> search(GroupSearchVo groupSearchVo) {
        List<String> includeStrList = groupSearchVo.getIncludeList();
        List<String> excludeStrList = groupSearchVo.getExcludeList();
        if (CollectionUtils.isEmpty(includeStrList)) {
            includeStrList = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(excludeStrList)) {
            excludeStrList = new ArrayList<>();
        }
        List<GroupSearchOptionVo> groupSearchOptionList = new ArrayList<>();
        for (UserType s : UserType.values()) {
            if ((!excludeStrList.contains(getHeader() + s.getValue()) && (s.getIsDefaultShow() || includeStrList.contains(getHeader() + s.getValue()))) && s.getText().contains(groupSearchVo.getKeyword())) {
                GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
                groupSearchOptionVo.setValue(getHeader() + s.getValue());
                groupSearchOptionVo.setText(s.getText());
                groupSearchOptionList.add(groupSearchOptionVo);
            }
        }
        return groupSearchOptionList;
    }

    @Override
    public List<GroupSearchOptionVo> reload(GroupSearchVo groupSearchVo) {
        List<GroupSearchOptionVo> groupSearchOptionList = new ArrayList<>();
        for (String value : groupSearchVo.getValueList()) {
            if (value.startsWith(getHeader())) {
                value = value.replace(getHeader(), StringUtils.EMPTY);
                GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
                groupSearchOptionVo.setValue(getHeader() + value);
                groupSearchOptionVo.setText(UserType.getText(value));
                groupSearchOptionList.add(groupSearchOptionVo);
            }
        }
        return groupSearchOptionList;
    }

    @Override
    public int getSort() {
        return 0;
    }

    @Override
    public Boolean isLimit() {
        return false;
    }
}
