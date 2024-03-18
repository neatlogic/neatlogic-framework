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
