/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.service;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.crossover.IFileCrossoverService;
import neatlogic.framework.exception.file.*;
import neatlogic.framework.exception.user.NoTenantException;
import neatlogic.framework.file.core.FileOperationType;
import neatlogic.framework.file.core.FileTypeHandlerFactory;
import neatlogic.framework.file.core.IFileTypeHandler;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileTypeVo;
import neatlogic.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import neatlogic.module.framework.file.handler.LocalFileSystemHandler;
import neatlogic.module.framework.file.handler.MinioFileSystemHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService, IFileCrossoverService {
    static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    @Resource
    private FileMapper fileMapper;

    @Override
    public void downloadFile(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = paramObj.getLong("id");
        FileVo fileVo = fileMapper.getFileById(id);
        if (fileVo == null) {
            throw new FileNotFoundException(id);
        }
        String tenantUuid = TenantContext.get().getTenantUuid();
        if (StringUtils.isBlank(tenantUuid)) {
            throw new NoTenantException();
        }
        BigDecimal lastModified = null;
        if (paramObj.getDouble("lastModified") != null) {
            lastModified = new BigDecimal(Double.toString(paramObj.getDouble("lastModified")));
        }
        if (lastModified != null) {
            if (lastModified.multiply(new BigDecimal("1000")).longValue() >= fileVo.getUploadTime().getTime()) {
                HttpServletResponse resp = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
                if (resp != null) {
                    resp.setStatus(205);
//                    resp.getWriter().print(StringUtils.EMPTY);
                }
            }
        }
        if (fileVo != null) {
            IFileTypeHandler fileTypeHandler = FileTypeHandlerFactory.getHandler(fileVo.getType());
            if (fileTypeHandler != null) {
                //system 用户下载权限豁免
                if (Objects.equals(UserContext.get().getUserUuid(), SystemUser.SYSTEM.getUserUuid()) || fileTypeHandler.valid(UserContext.get().getUserUuid(), fileVo, paramObj)) {
                    ServletOutputStream os = null;
                    InputStream in = null;
                    in = FileUtil.getData(fileVo.getPath());
                    if (in != null) {
                        String fileNameEncode = "";
                        Boolean flag = request.getHeader("User-Agent").indexOf("Gecko") > 0;
                        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 || flag) {
                            fileNameEncode = URLEncoder.encode(fileVo.getName(), "UTF-8");// IE浏览器
                        } else {
                            fileNameEncode = new String(fileVo.getName().replace(" ", "").getBytes(StandardCharsets.UTF_8), "ISO8859-1");
                        }

                        if (StringUtils.isBlank(fileVo.getContentType())) {
                            response.setContentType("application/x-msdownload");
                        } else {
                            response.setContentType(fileVo.getContentType());
                        }
                        response.setHeader("Content-Disposition", " attachment; filename=\"" + fileNameEncode + "\"");
                        os = response.getOutputStream();
                        IOUtils.copyLarge(in, os);
                        if (os != null) {
                            os.flush();
                            os.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    }
                } else {
                    throw new FileAccessDeniedException(fileVo.getName(), FileOperationType.DOWNLOAD.getText());
                }
            } else {
                throw new FileTypeHandlerNotFoundException(fileVo.getType());
            }
        } else {
            throw new FileNotFoundException(id);
        }
    }

    @Override
    public void deleteFile(Long fileId, JSONObject paramObj) throws Exception {
        FileVo fileVo = fileMapper.getFileById(fileId);
        if (fileVo == null) {
            return;
        }
        IFileTypeHandler fileTypeHandler = FileTypeHandlerFactory.getHandler(fileVo.getType());
        if (fileTypeHandler == null) {
            return;
        }
        if (fileTypeHandler.valid(UserContext.get().getUserUuid(), fileVo, paramObj)) {
            fileTypeHandler.deleteFile(fileVo, paramObj);
        }
    }

    @Override
    public FileVo saveFile(MultipartFile multipartFile, String type, FileTypeVo fileTypeConfigVo, JSONObject paramObj) throws Exception {
        String tenantUuid = TenantContext.get().getTenantUuid();
        if (multipartFile != null) {
            String uniqueKey = paramObj.getString("uniqueKey");
            IFileTypeHandler fileTypeHandler = FileTypeHandlerFactory.getHandler(type);
            if (fileTypeHandler == null) {
                throw new FileTypeHandlerNotFoundException(type);
            }
            if (fileTypeHandler.needSave() && fileTypeHandler.isUnique() && StringUtils.isBlank(uniqueKey)) {
                throw new FileUniqueKeyEmptyException();
            }
            if (fileTypeHandler.beforeUpload(paramObj)) {
                multipartFile.getName();
                String userUuid = UserContext.get().getUserUuid(true);
                String oldFileName = multipartFile.getOriginalFilename();
                long size = multipartFile.getSize();
                // 如果配置为空代表不受任何限制
                if (fileTypeConfigVo != null) {
                    boolean isAllowed = false;
                    long maxSize = 0L;
                    String fileExt = "";
                    if (StringUtils.isNotBlank(oldFileName)) {
                        fileExt = oldFileName.substring(oldFileName.lastIndexOf(".") + 1).toLowerCase();
                    }
                    JSONObject configObj = fileTypeConfigVo.getConfigObj();
                    JSONArray whiteList = new JSONArray();
                    JSONArray blackList = new JSONArray();
                    if (size == 0) {
                        throw new EmptyFileException();
                    }
                    if (configObj != null) {
                        whiteList = configObj.getJSONArray("whiteList");
                        blackList = configObj.getJSONArray("blackList");
                        maxSize = configObj.getLongValue("maxSize");
                    }
                    if (whiteList != null && whiteList.size() > 0) {
                        for (int i = 0; i < whiteList.size(); i++) {
                            if (fileExt.equalsIgnoreCase(whiteList.getString(i))) {
                                isAllowed = true;
                                break;
                            }
                        }
                    } else if (blackList != null && blackList.size() > 0) {
                        isAllowed = true;
                        for (int i = 0; i < blackList.size(); i++) {
                            if (fileExt.equalsIgnoreCase(blackList.getString(i))) {
                                isAllowed = false;
                                break;
                            }
                        }
                    } else {
                        isAllowed = true;
                    }
                    if (!isAllowed) {
                        throw new FileExtNotAllowedException(fileExt);
                    }
                    if (maxSize > 0 && size > maxSize) {
                        throw new FileTooLargeException(size, maxSize);
                    }
                }


                FileVo fileVo = new FileVo();
                fileVo.setName(oldFileName);
                fileVo.setSize(size);
                fileVo.setUserUuid(userUuid);
                fileVo.setType(type);
                fileVo.setContentType(multipartFile.getContentType());
                if (fileTypeHandler.needSave()) {
                    FileVo oldFileVo = null;
                    if (fileTypeHandler.isUnique()) {
                        String uk = fileTypeHandler.getUniqueKey(uniqueKey);
                        fileVo.setUniqueKey(uk);
                        oldFileVo = fileMapper.getFileByNameAndUniqueKey(fileVo.getName(), uk);
                    }
                    String filePath;
                    try {
                        filePath = FileUtil.saveData(MinioFileSystemHandler.NAME, tenantUuid, multipartFile.getInputStream(), fileVo.getId().toString(), fileVo.getContentType(), fileVo.getType());
                    } catch (Exception ex) {
                        //如果没有配置minioUrl，则表示不使用minio，无需抛异常
                        if (StringUtils.isNotBlank(Config.MINIO_URL())) {
                            logger.error(ex.getMessage(), ex);
                        }
                        // 如果minio出现异常，则上传到本地
                        filePath = FileUtil.saveData(LocalFileSystemHandler.NAME, tenantUuid, multipartFile.getInputStream(), fileVo.getId().toString(), fileVo.getContentType(), fileVo.getType());
                    }
                    fileVo.setPath(filePath);
                    if (oldFileVo == null) {
                        fileMapper.insertFile(fileVo);
                    } else {
                        FileUtil.deleteData(oldFileVo.getPath());
                        fileVo.setId(oldFileVo.getId());
                        fileMapper.updateFile(fileVo);
                    }
                    fileTypeHandler.afterUpload(fileVo, paramObj);
                    FileVo file = fileMapper.getFileById(fileVo.getId());
                    file.setUrl("api/binary/file/download?id=" + fileVo.getId());

                    return file;
                } else {
                    fileTypeHandler.analyze(multipartFile, paramObj);
                    return fileVo;
                }
            }
        }
        return null;
    }
}
