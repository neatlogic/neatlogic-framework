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

package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

/** 查看功能自检场景、执行历史和结果详情的权限。 */
public class SELF_CHECK_VIEW extends AuthBase {
    @Override
    public String getAuthDisplayName() {
        return "auth.self_check_view.name";
    }

    @Override
    public String getAuthIntroduction() {
        return "auth.self_check_view.description";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return 29;
    }
}
