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

package neatlogic.framework.mq.dto;

public class SubscribeHandlerVo {
    private String name;
    private String className;
    private String label;
    private Boolean isEmbed;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SubscribeHandlerVo(String _name, String _label, String _className) {
        name = _name;
        label = _label;
        className = _className;
    }

    public SubscribeHandlerVo(String _name, String _label, String _className, Boolean _isEmbed) {
        name = _name;
        label = _label;
        className = _className;
        isEmbed = _isEmbed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getIsEmbed() {
        return isEmbed;
    }

    public void setIsEmbed(Boolean isEmbed) {
        this.isEmbed = isEmbed;
    }
}
