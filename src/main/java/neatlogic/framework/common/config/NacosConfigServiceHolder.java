/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.common.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Properties;

public final class NacosConfigServiceHolder {

    private static volatile ConfigService configService;

    private NacosConfigServiceHolder() {}

    public static ConfigService getInstance(Properties properties) throws NacosException {
        if (configService == null) {
            synchronized (NacosConfigServiceHolder.class) {
                if (configService == null) {
                    configService = NacosFactory.createConfigService(properties);
                }
            }
        }
        return configService;
    }

    public static ConfigService getInstance() {
        if (configService == null) {
            throw new IllegalStateException("ConfigService has not been initialized");
        }
        return configService;
    }
}