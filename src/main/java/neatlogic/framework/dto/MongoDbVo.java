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

package neatlogic.framework.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.ConnectionPasswordUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class MongoDbVo {
    private String tenantUuid;
    private String host;
    private String database;
    private String username;
    @JSONField(serialize = false)
    private transient String passwordPlain;
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

    /** 按存储密文前缀解密，继续兼容历史明文配置。 */
    public String getPasswordPlain() {
        if (passwordPlain == null && StringUtils.isNotBlank(passwordCipher)) {
            this.passwordPlain = ConnectionPasswordUtil.decrypt(passwordCipher);
        }
        return passwordPlain;
    }

    /** 更新明文时清除旧密文，避免保存上一次的密码。 */
    public void setPasswordPlain(String passwordPlain) {
        this.passwordPlain = passwordPlain;
        this.passwordCipher = null;
    }

    /** 新密码使用AES前缀的混合密文，已有密文保留原值，历史明文在保存时转为密文。 */
    public String getPasswordCipher() {
        if (StringUtils.isNotBlank(passwordCipher)) {
            this.passwordCipher = ConnectionPasswordUtil.toCipher(passwordCipher);
        } else if (StringUtils.isNotBlank(passwordPlain)) {
            this.passwordCipher = ConnectionPasswordUtil.encryptPlain(passwordPlain);
        }
        return passwordCipher;
    }

    /** 更新密文时清除旧明文，后续认证按新配置解密。 */
    public void setPasswordCipher(String passwordCipher) {
        this.passwordCipher = passwordCipher;
        this.passwordPlain = null;
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
