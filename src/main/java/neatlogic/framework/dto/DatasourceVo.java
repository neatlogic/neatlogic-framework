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
import neatlogic.framework.common.config.Config;
import neatlogic.framework.util.ConnectionPasswordUtil;
import neatlogic.framework.util.PassWordUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class DatasourceVo implements Serializable {
    private static final long serialVersionUID = 2803421287862902646L;
    private String tenantUuid;
    private String host;
    private Integer port;
    private String url;
    private String username;
    @JSONField(serialize = false)
    private transient String passwordPlain;
    private String passwordCipher;
    private String driver = "com.mysql.cj.jdbc.Driver";

    public DatasourceVo() {

    }

    public DatasourceVo(boolean generatePwd) {
        if (generatePwd) {
            this.passwordPlain  = PassWordUtil.createRandomPassWord();
        }
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }

    public String getUrl() {
        if (StringUtils.isBlank(url)) {
            url = "jdbc:mysql://{host}:{port}/{dbname}?characterEncoding=UTF-8&jdbcCompliantTruncation=false&allowMultiQueries=true&useSSL=false&&serverTimeZone=Asia/Shanghai";
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getHost() {
        if (StringUtils.isBlank(host)) {
            host = Config.DB_HOST();
        }
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        if (port == null) {
            port = Config.DB_PORT();
        }
        return port;
    }

    public boolean getIsLocalDb() {
        return Objects.equals(getHost(), Config.DB_HOST());
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
