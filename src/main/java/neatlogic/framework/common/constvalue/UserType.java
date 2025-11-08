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

package neatlogic.framework.common.constvalue;

import neatlogic.framework.dto.UserTypeVo;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.HashMap;
import java.util.Map;

public enum UserType implements IUserType {
    ALL("alluser", new I18n("所有人"), true),
    LOGIN_USER("loginuser", new I18n("当前登录人"), false),
    LOGIN_TEAM("loginteam", new I18n("当前登录人所在组"), false),
    LOGIN_DEPARTMENT("logindepartment", new I18n("当前登录人所在部"), false),
    LOGIN_CENTER("logincenter", new I18n("当前登录人所在中心"), false),
    VIP_USER("vipuser", new I18n("vip用户"), false);

    private final String status;
    private final I18n text;
    private final Boolean isDefaultShow;

    UserType(String _status, I18n _text, Boolean _isDefaultShow) {
        this.status = _status;
        this.text = _text;
        this.isDefaultShow = _isDefaultShow;
    }

    public String getValue() {
        return status;
    }

    public String getText() {
        return $.t(text.toString());
    }

    public Boolean getIsDefaultShow() {
        return isDefaultShow;
    }

    public static String getValue(String _status) {
        for (UserType s : UserType.values()) {
            if (s.getValue().equals(_status)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getText(String _status) {
        for (UserType s : UserType.values()) {
            if (s.getValue().equals(_status)) {
                return s.getText();
            }
        }
        return "";
    }


    @Override
    public UserTypeVo getUserType() {
        UserTypeVo vo = new UserTypeVo();
        vo.setModuleId(getModuleId());
        Map<String, String> map = new HashMap<>();
        for (UserType type : UserType.values()) {
            map.put(type.getValue(), type.getText());
        }
        vo.setValues(map);
        return vo;
    }

    @Override
    public String getModuleId() {
        return "framework";
    }
}
