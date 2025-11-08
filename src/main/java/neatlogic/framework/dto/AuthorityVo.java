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

package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.GroupSearch;

import java.util.ArrayList;
import java.util.List;

public class AuthorityVo {
    private String type;
    private String uuid;
    private String action;
    public AuthorityVo() {

    }

    public AuthorityVo(String type, String uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorityVo that = (AuthorityVo) o;
        if (type == null) {
            if (that.type != null)
                return false;
        } else if (!type.equals(that.type))
            return false;
        if (action == null) {
            if (that.action != null)
                return false;
        } else if (!action.equals(that.action))
            return false;
        if (uuid == null) {
            return that.uuid == null;
        } else return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    public static List<String> getAuthorityList(List<AuthorityVo> authorityVoList) {
        List<String> authorityList = new ArrayList<>();
        for(AuthorityVo authorityVo : authorityVoList) {
            GroupSearch groupSearch = GroupSearch.getGroupSearch(authorityVo.getType());
            if(groupSearch != null) {
                authorityList.add(groupSearch.getValuePlugin() + authorityVo.getUuid());
            }
        }
        return authorityList;
    }

    public static List<AuthorityVo> getAuthorityVoList(List<String> authorityList, String action) {
        List<AuthorityVo> authorityVoList = new ArrayList<>();
        for(String authority : authorityList) {
            String[] split = authority.split("#");
            if(GroupSearch.getGroupSearch(split[0]) != null) {
                AuthorityVo authorityVo = new AuthorityVo();
                authorityVo.setType(split[0]);
                authorityVo.setUuid(split[1]);
                authorityVo.setAction(action);
                authorityVoList.add(authorityVo);
            }
        }
        return authorityVoList;
    }
}
