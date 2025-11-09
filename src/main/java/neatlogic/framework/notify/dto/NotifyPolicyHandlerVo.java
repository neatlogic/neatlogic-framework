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
