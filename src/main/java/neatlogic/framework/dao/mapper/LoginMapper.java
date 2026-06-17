/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.loginaudit.LoginAuditVo;
import neatlogic.framework.dto.captcha.LoginCaptchaVo;
import neatlogic.framework.dto.captcha.LoginFailedCountVo;
import neatlogic.framework.dto.loginaudit.LoginAuditSearchVo;

import java.util.List;

public interface LoginMapper {

    LoginCaptchaVo getLoginCaptchaBySessionId(String key);

    Integer getLoginFailedCountByUserId(String userUuid);

    LoginFailedCountVo getLoginFailedCountVoByUserId(String userUuid);

    LoginFailedCountVo getLoginFailedCountLockByUserId(String userUuid);

    int getLoginAuditCount(LoginAuditSearchVo searchVo);

    List<LoginAuditVo> getLoginAuditList(LoginAuditSearchVo searchVo);

    LoginAuditVo getLastLoginAuditByUserUuid(String userUuid);

    Integer updateLoginCaptcha(LoginCaptchaVo loginCaptchaVo);

    Integer updateLoginFailedCount(LoginFailedCountVo loginFailedCountVo);

    int insertLoginAudit(LoginAuditVo loginAuditVo);

    Integer deleteLoginCaptchaBySessionId(String sessionId);

    Integer deleteLoginFailedCountByUserId(String userId);

    void deleteLoginInvalidCaptcha();

    int deleteLoginAuditByDayBefore(int dayBefore);
}
