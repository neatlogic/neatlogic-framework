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
