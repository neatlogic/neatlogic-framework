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

package neatlogic.module.framework.file.handler;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.file.core.IFileStorageHandler;
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
    public String saveData(String tenantUuid, InputStream inputStream, String fileId, String contentType, String fileType) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy" + File.separator + "MM" + File.separator + "dd");
        String filePath = tenantUuid + File.separator + fileType + File.separator + format.format(new Date()) + File.separator + fileId;
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
