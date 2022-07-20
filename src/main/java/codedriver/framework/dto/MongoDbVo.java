/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto;

import codedriver.framework.common.util.RC4Util;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class MongoDbVo {
    private Long tenantId;
    private String tenantUuid;
    private String host;
    private String database;
    private String username;
    private String passwordPlain;
    private String passwordCipher;
    private String option;

    public MongoDbVo() {

    }

    public MongoDbVo(Long _tenantId, String _tenantUuid, boolean generatePwd) {
        this.tenantId = _tenantId;
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


    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


}
