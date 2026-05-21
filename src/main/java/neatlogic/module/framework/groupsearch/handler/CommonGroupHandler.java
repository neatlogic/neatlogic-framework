/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
            if ((!excludeStrList.contains(getHeader() + s.getValue()) && (s.getIsDefaultShow() || includeStrList.contains(getHeader() + s.getValue()))) && (StringUtils.isBlank(groupSearchVo.getKeyword()) || s.getText().contains(groupSearchVo.getKeyword()))) {
                GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
                groupSearchOptionVo.setValue(getHeader() + s.getValue());
                groupSearchOptionVo.setText(s.getText());
                groupSearchOptionList.add(groupSearchOptionVo);
            }
        }
        groupSearchVo.setPageSize(groupSearchOptionList.size());
        groupSearchVo.setRowNum(groupSearchOptionList.size());
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
