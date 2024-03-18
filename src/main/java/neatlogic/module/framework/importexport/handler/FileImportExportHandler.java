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

package neatlogic.module.framework.importexport.handler;

import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.importexport.constvalue.FrameworkImportExportHandlerType;
import neatlogic.framework.importexport.core.ImportExportHandlerBase;
import neatlogic.framework.importexport.core.ImportExportHandlerType;
import neatlogic.framework.importexport.dto.ImportExportBaseInfoVo;
import neatlogic.framework.importexport.dto.ImportExportPrimaryChangeVo;
import neatlogic.framework.importexport.dto.ImportExportVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class FileImportExportHandler extends ImportExportHandlerBase {

    private Logger logger = LoggerFactory.getLogger(FileImportExportHandler.class);
    @Resource
    private FileMapper fileMapper;

    @Override
    public ImportExportHandlerType getType() {
        return FrameworkImportExportHandlerType.FILE;
    }

    @Override
    public boolean checkImportAuth(ImportExportVo importExportVo) {
        return true;
    }

    @Override
    public boolean checkExportAuth(Object primaryKey) {
        return true;
    }

    @Override
    public boolean checkIsExists(ImportExportBaseInfoVo importExportBaseInfoVo) {
        return fileMapper.getFileById((Long) importExportBaseInfoVo.getPrimaryKey()) != null;
    }

    @Override
    public Object getPrimaryByName(ImportExportVo importExportVo) {
        return importExportVo.getPrimaryKey();
    }

    @Override
    public Long importData(ImportExportVo importExportVo, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        FileVo fileVo = importExportVo.getData().toJavaObject(FileVo.class);
        if (fileMapper.getFileById(fileVo.getId()) != null) {
            fileVo.setId(null);
        }
        fileMapper.insertFile(fileVo);
        return fileVo.getId();
    }

    @Override
    protected ImportExportVo myExportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        Long id = (Long) primaryKey;
        FileVo fileVo = fileMapper.getFileById(id);
        if (fileVo == null) {
            throw new FileNotFoundException(id);
        }
        InputStream in = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            in = FileUtil.getData(fileVo.getPath());
            if (in != null) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                if (zipOutputStream != null) {
                    zipOutputStream.putNextEntry(new ZipEntry("attachment-folder/" + fileVo.getId() + "/" + fileVo.getName()));
                    zipOutputStream.write(out.toByteArray());
                    zipOutputStream.closeEntry();
                    in.close();
                    out.reset();
                    if (logger.isWarnEnabled()) {
                        logger.warn("export file: " + fileVo.getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ImportExportVo importExportVo = new ImportExportVo(this.getType().getValue(), primaryKey, fileVo.getName());
        importExportVo.setDataWithObject(fileVo);
        return importExportVo;
    }
}
