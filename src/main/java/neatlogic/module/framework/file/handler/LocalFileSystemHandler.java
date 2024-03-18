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
