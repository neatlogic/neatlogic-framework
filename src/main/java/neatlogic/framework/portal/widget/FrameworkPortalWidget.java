/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.portal.widget;

import neatlogic.framework.portal.widget.core.IPortalWidget;

public enum FrameworkPortalWidget implements IPortalWidget {
    welcomeOverview("welcomeOverview", "欢迎概况", 1),
//    todayFocus("todayFocus", "今日关注", 2),
    quickEntry("quickEntry", "快捷入口", 2),
//    notice("notice", "系统通知", 4),
//    recentAccess("recentAccess", "最近访问", 5),
//    calendar("calendar", "日历提醒", 6),
    ;
    private final String value;
    private final String text;
    private final Integer sort;

    FrameworkPortalWidget(String value, String text, Integer sort) {
        this.value = value;
        this.text = text;
        this.sort = sort;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public Integer getSort() {
        return this.sort;
    }
}
