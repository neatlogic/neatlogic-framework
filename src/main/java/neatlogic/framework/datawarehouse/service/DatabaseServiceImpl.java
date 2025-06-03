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

package neatlogic.framework.datawarehouse.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.datawarehouse.dao.mapper.DatabaseMapper;
import neatlogic.framework.datawarehouse.dto.DatabaseVo;
import neatlogic.framework.datawarehouse.exceptions.DatabaseConnectionFailedException;
import neatlogic.framework.datawarehouse.exceptions.DatabaseNotFoundException;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    static Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    @Resource
    private DatabaseMapper databaseMapper;

    @Resource
    private FileMapper fileMapper;


    @Override
    public Connection getConnectionByDatabaseId(Long databaseId) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        DatabaseVo dataBaseVo = databaseMapper.getDataBaseById(databaseId);
        if (dataBaseVo == null) {
            throw new DatabaseNotFoundException(databaseId);
        }
        JSONObject config = dataBaseVo.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            String user = config.getString("user");
            String password = config.getString("password");
            String url = config.getString("url");
            String driverClassName = config.getString("driverClassName");
            Properties props = new Properties();
            if (StringUtils.isNoneBlank(user)) {
                props.put("user", user);
            }
            if (StringUtils.isNotBlank(password)) {
                props.put("password", password);
            }
            JSONArray fileIdList = config.getJSONArray("fileIdList");
            if (CollectionUtils.isNotEmpty(fileIdList)) {
                URL[] urls = new URL[fileIdList.size()];
                for (int i = 0; i < fileIdList.size(); i++) {
                    Long fileId = fileIdList.getLong(i);
                    FileVo fileVo = fileMapper.getFileById(fileId);
                    if (fileVo == null) {
                        throw new FileNotFoundException(fileId);
                    }
                    try (InputStream is = FileUtil.getData(fileVo.getPath())) {
                        String fileName = fileVo.getName();
                        String suffix = ".jar";
                        String prefix = fileName.substring(0, fileName.length() - suffix.length());
                        File file = new File(prefix + "-" + fileVo.getId() + suffix);
                        Path path = Paths.get(file.toURI());
                        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
                        urls[i] = file.toURI().toURL();
                    }catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
                URLClassLoader loader = new URLClassLoader(urls, null);
                Class<?> clazz = loader.loadClass(driverClassName);
                Driver driver = ((Driver) clazz.newInstance());
                return driver.connect(url, props);
            } else {
                throw new DatabaseConnectionFailedException(DatabaseConnectionFailedException.Type.FILE_ID_LIST_IS_EMPTY, dataBaseVo.getName());
            }
        } else {
            throw new DatabaseConnectionFailedException(DatabaseConnectionFailedException.Type.CONFIG_IS_EMPTY, dataBaseVo.getName());
        }
    }
}
