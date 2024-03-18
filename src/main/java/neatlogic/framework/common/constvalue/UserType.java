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
