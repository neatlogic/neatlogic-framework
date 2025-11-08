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

package neatlogic.module.framework.file.handler;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.file.core.IFileStorageHandler;
import neatlogic.framework.file.dto.FileVo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LocalFileSystemHandler implements IFileStorageHandler {

    public static final String NAME = "FILE";


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String saveData(String tenantUuid, InputStream inputStream, FileVo fileParam) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy" + File.separator + "MM" + File.separator + "dd");
        String filePath = tenantUuid + File.separator + fileParam.getType() + File.separator + format.format(new Date()) + File.separator + fileParam.getPathName();
        String finalPath = Config.DATA_HOME() + filePath;
        File file = new File(finalPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        IOUtils.copyLarge(inputStream, fos);
        fos.flush();
        fos.close();
//		fileVo.setPath("file:" + filePath);
        return LocalFileSystemHandler.NAME.toLowerCase() + ":" + finalPath;
    }

    @Override
    public InputStream getData(String path) throws Exception {
        InputStream in = null;
        File file = new File(path.substring(5));
        if (file.exists() && file.isFile()) {
            in = Files.newInputStream(file.toPath());
        }
        return in;
    }

    @Override
    public void deleteData(String filePath) throws Exception {
        if (StringUtils.isNotBlank(filePath) && filePath.startsWith(LocalFileSystemHandler.NAME.toLowerCase() + ":")) {
            File file = new File(filePath.substring(5));
            if (file.exists()) {
                file.delete();
            }
        } else {
            throw new FilePathIllegalException(filePath);
        }
    }

    @Override
    public long getDataLength(String filePath) {
        long length = 0;
        File file = new File(filePath.substring(5));
        if (file.exists() && file.isFile()) {
            length = file.length();
        }
        return length;
    }

    @Override
    public boolean isExit(String filePath) throws Exception {
        File file = new File(filePath.substring(5));
        return file.exists();
    }
}
