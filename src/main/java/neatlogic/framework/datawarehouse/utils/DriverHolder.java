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
                String fileName = fileVo.getName();
                String suffix = ".jar";
                String prefix = fileName.substring(0, fileName.length() - suffix.length());
                File file = new File(prefix + "-" + fileVo.getId() + suffix);
                if (!file.exists()) {
                    try (InputStream is = FileUtil.getData(fileVo.getPath())) {
                        Path path = Paths.get(file.toURI());
                        Files.copy(is, path);
                        urls[i] = file.toURI().toURL();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
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
            try {
                DriverManager.deregisterDriver(driver);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        URLClassLoader classLoader = LOADER_MAP.remove(id);
        if (classLoader != null) {
            try {
                classLoader.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
