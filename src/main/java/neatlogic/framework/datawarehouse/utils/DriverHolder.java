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

package neatlogic.framework.datawarehouse.utils;

import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.datawarehouse.dto.DatabaseVo;
import neatlogic.framework.datawarehouse.exceptions.DatabaseConnectionFailedException;
import neatlogic.framework.file.dto.FileVo;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverHolder {
    private static final Logger logger = LoggerFactory.getLogger(DriverHolder.class);
    private static final Map<Long, URLClassLoader> LOADER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Driver> DRIVER_MAP = new ConcurrentHashMap<>();

    public static synchronized Driver borrowDriver(DatabaseVo databaseVo) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Long id = databaseVo.getId();
        Driver driver = DRIVER_MAP.get(id);
        if (driver != null) return driver;

        // 只在第一次创建
        List<FileVo> fileList = databaseVo.getFileList();
        if (CollectionUtils.isNotEmpty(fileList)) {
            URL[] urls = new URL[fileList.size()];
            for (int i = 0; i < fileList.size(); i++) {
                FileVo fileVo = fileList.get(i);
                try (InputStream is = FileUtil.getData(fileVo.getPath())) {
                    String fileName = fileVo.getName();
                    String suffix = ".jar";
                    String prefix = fileName.substring(0, fileName.length() - suffix.length());
                    File file = new File(prefix + "-" + fileVo.getId() + suffix);
                    if (!file.exists()) {
                        Path path = Paths.get(file.toURI());
                        Files.copy(is, path);
                    }
                    urls[i] = file.toURI().toURL();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
            URLClassLoader loader = new URLClassLoader(urls, DriverHolder.class.getClassLoader());
            Class<?> clazz = loader.loadClass(databaseVo.getConfig().getString("driverClassName"));
            driver = (Driver) clazz.newInstance();
            LOADER_MAP.put(id, loader);
            DRIVER_MAP.put(id, driver);
            return driver;
        } else {
            throw new DatabaseConnectionFailedException(DatabaseConnectionFailedException.Type.FILE_ID_LIST_IS_EMPTY, databaseVo.getName());
        }
    }

    public static synchronized void destroyDriver(Long id) throws SQLException, IOException {
        Driver driver = DRIVER_MAP.remove(id);
        if (driver != null) {
            DriverManager.deregisterDriver(driver);
        }
        URLClassLoader classLoader = LOADER_MAP.remove(id);
        if (classLoader != null) {
            classLoader.close();
        }
    }
}
