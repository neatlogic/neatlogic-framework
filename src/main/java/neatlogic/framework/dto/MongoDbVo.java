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

package neatlogic.framework.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class MongoDbVo {
    private String tenantUuid;
    private String host;
    private String database;
    private String username;
    private String passwordPlain;
    private String passwordCipher;
    private String option;

    @JSONField(serialize = false)
    @EntityField(name = "nfd.tenantvo.authmongodb", type = ApiParamType.STRING)
    private String authConfig;

    public MongoDbVo() {

    }

    public MongoDbVo( String _tenantUuid, boolean generatePwd) {
        this.tenantUuid = _tenantUuid;
        // 生成随机密码
        if (generatePwd) {
            // this.passwordPlain = "123456";
            Random rand = new Random();
            StringBuilder password = new StringBuilder();
            String[] chars = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

            for (int i = 0; i < 6; i++) {
                int randNumber = rand.nextInt(chars.length);
                password.append(chars[randNumber]);
            }
            this.passwordPlain = password.toString();
        }
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }


    public String getUsername() {
        if (StringUtils.isBlank(username)) {
            username = this.tenantUuid;
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordPlain() {
        if (StringUtils.isBlank(passwordPlain)) {
            if (StringUtils.isNotBlank(passwordCipher)) {
                this.passwordPlain = RC4Util.decrypt(this.passwordCipher);
            }
        }
        return passwordPlain;
    }

    public void setPasswordPlain(String passwordPlain) {
        this.passwordPlain = passwordPlain;
    }

    public String getPasswordCipher() {
        if (StringUtils.isBlank(passwordCipher)) {
            if (StringUtils.isNotBlank(passwordPlain)) {
                this.passwordCipher = RC4Util.encrypt(passwordPlain);
            }
        }
        return passwordCipher;
    }

    public void setPasswordCipher(String passwordCipher) {
        this.passwordCipher = passwordCipher;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(String authConfig) {
        this.authConfig = authConfig;
    }
}
