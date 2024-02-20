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

package neatlogic.framework.dto;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.RC4Util;
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
    private String passwordPlain;
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
