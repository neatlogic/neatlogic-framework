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

package neatlogic.framework.datawarehouse.dto;

import neatlogic.framework.common.util.RC4Util;
import org.apache.commons.lang3.StringUtils;

public class ConnectionVo {
    private Long id;
    private String name;
    private String url;
    private String username;
    private String databaseVersion;
    private String passwordPlain;
    private String passwordCipher;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
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
}
