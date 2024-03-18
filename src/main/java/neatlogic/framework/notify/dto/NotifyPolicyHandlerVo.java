/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.notify.dto;

/**
 * @author linbq
 * @since 2021/7/23 15:30
 **/
@Deprecated
public class NotifyPolicyHandlerVo implements Cloneable{
    private String handler;
    private String name;
    private String module;
    private String moduleGroup;
    private String authName;
    private Integer isAllowMultiPolicy;

    public NotifyPolicyHandlerVo(String handler, String name, String authName, String module, String moduleGroup, Integer isAllowMultiPolicy) {
        this.handler = handler;
        this.name = name;
        this.authName = authName;
        this.module = module;
        this.moduleGroup = moduleGroup;
        this.isAllowMultiPolicy = isAllowMultiPolicy;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModuleGroup() {
        return moduleGroup;
    }

    public void setModuleGroup(String moduleGroup) {
        this.moduleGroup = moduleGroup;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public Integer getIsAllowMultiPolicy() {
        return isAllowMultiPolicy;
    }

    public void setIsAllowMultiPolicy(Integer isAllowMultiPolicy) {
        this.isAllowMultiPolicy = isAllowMultiPolicy;
    }

    @Override

    public NotifyPolicyHandlerVo clone() throws CloneNotSupportedException {
        return (NotifyPolicyHandlerVo) super.clone();
    }
}
