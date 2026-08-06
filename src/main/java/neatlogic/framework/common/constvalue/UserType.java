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

import java.util.HashMap;
import java.util.Map;

public enum UserType implements IUserType {
    ALL("alluser", "common.alluser", true),
    LOGIN_USER("loginuser", "common.loginuser", false),
    LOGIN_TEAM("loginteam", "common.loginteam", false),
    LOGIN_DEPARTMENT("logindepartment", "common.logindepartment", false),
    LOGIN_CENTER("logincenter", "common.logincenter", false),
    VIP_USER("vipuser", "common.vipuser", false);

    private final String status;
    private final String text;
    private final Boolean isDefaultShow;

    UserType(String _status, String _text, Boolean _isDefaultShow) {
        this.status = _status;
        this.text = _text;
        this.isDefaultShow = _isDefaultShow;
    }

    public String getValue() {
        return status;
    }

    public String getText() {
        return $.t(text);
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
