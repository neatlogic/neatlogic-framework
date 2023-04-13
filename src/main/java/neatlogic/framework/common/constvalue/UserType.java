/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.common.constvalue;

import neatlogic.framework.dto.UserTypeVo;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.HashMap;
import java.util.Map;

public enum UserType implements IUserType {
    ALL("alluser", new I18n("enum.framework.usertype.all"), true),
    LOGIN_USER("loginuser", new I18n("enum.framework.usertype.login_user"), false),
    LOGIN_TEAM("loginteam", new I18n("enum.framework.usertype.login_team"), false),
    LOGIN_DEPARTMENT("logindepartment", new I18n("enum.framework.usertype.login_department"), false),
    LOGIN_CENTER("logincenter", new I18n("enum.framework.usertype.login_center"), false),
    VIP_USER("vipuser", new I18n("enum.framework.usertype.vip_user"), false);

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
        return I18nUtils.getMessage(text.toString());
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
