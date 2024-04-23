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

package neatlogic.framework.importexport.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.importexport.constvalue.FrameworkImportExportHandlerType;
import neatlogic.framework.importexport.dto.*;
import neatlogic.framework.importexport.exception.DependencyNotFoundException;
import neatlogic.framework.importexport.exception.ImportExportHandlerNotFoundException;
import neatlogic.framework.importexport.exception.ImportExportTypeInconsistencyException;
import neatlogic.framework.importexport.exception.ImportNoAuthException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RootComponent
public class ImportExportHandlerFactory extends ModuleInitializedListenerBase {

    private static final Logger logger = LoggerFactory.getLogger(ImportExportHandlerFactory.class);

    private static Map<String, ImportExportHandler> componentMap = new HashMap<>();

    public static ImportExportHandler getHandler(String handler) {
        return componentMap.get(handler);
    }

    private static FileMapper fileMapper;

    @Resource
    public void setFileMapper(FileMapper _fileMapper) {
        fileMapper = _fileMapper;
    }

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ImportExportHandler> myMap = context.getBeansOfType(ImportExportHandler.class);
        for (Map.Entry<String, ImportExportHandler> entry : myMap.entrySet()) {
            ImportExportHandler component = entry.getValue();
            if (component.getType() == null) {
                logger.error("ImportExportHandler '" + component.getClass().getSimpleName() + "' type is null");
                System.exit(1);
            }
            if (componentMap.containsKey(component.getType().getValue())) {
                logger.error("ImportExportHandler '" + component.getClass().getSimpleName() + "(" + component.getType().getValue() + ")' repeat");
                System.exit(1);
            }
            componentMap.put(component.getType().getValue(), component);
        }
    }

    @Override
    protected void myInit() {

    }

    /**
     * 导入数据
     *
     * @param multipartFile 文件信息
     * @param targetType    导入目标类型
     * @param userSelection 用户选择的依赖导入选项
     * @return
     */
    public static JSONObject importData(MultipartFile multipartFile, String targetType, String userSelection) {
        boolean checkAll = false;
        List<ImportDependencyTypeVo> typeList = new ArrayList<>();
        if (StringUtils.isNotBlank(userSelection)) {
            JSONObject userSelectionObj = JSONObject.parseObject(userSelection);
            if (MapUtils.isNotEmpty(userSelectionObj)) {
                checkAll = userSelectionObj.getBooleanValue("checkedAll");
                JSONArray typeArray = userSelectionObj.getJSONArray("typeList");
                if (CollectionUtils.isNotEmpty(typeArray)) {
                    typeList = typeArray.toJavaList(ImportDependencyTypeVo.class);
                }
            }
        }
        byte[] buf = new byte[1024];
        // 第一次遍历压缩包，检查是否有依赖项需要询问用户是否导入
        if (StringUtils.isBlank(userSelection)) {
            try (ZipInputStream zipIs = new ZipInputStream(multipartFile.getInputStream());
                 ByteArrayOutputStream out = new ByteArrayOutputStream()
            ) {
                ZipEntry zipEntry = null;
                while ((zipEntry = zipIs.getNextEntry()) != null) {
                    if (zipEntry.isDirectory()) {
                        continue;
                    }
                    out.reset();
                    String zipEntryName = zipEntry.getName();
                    if (zipEntryName.startsWith("dependency-folder/")) {
                        continue;
                    }
                    if (zipEntryName.startsWith("attachment-folder/")) {
                        continue;
                    }
                    if (zipEntry.getName().endsWith(".json")) {
                        int len;
                        while ((len = zipIs.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                        ImportExportVo mainImportExportVo = JSONObject.parseObject(new String(out.toByteArray(), StandardCharsets.UTF_8), ImportExportVo.class);
                        ImportExportHandler importExportHandler = getHandler(mainImportExportVo.getType());
                        if (importExportHandler == null) {
                            throw new ImportExportHandlerNotFoundException(mainImportExportVo.getType());
                        }
                        if (!importExportHandler.checkImportAuth(mainImportExportVo)) {
                            throw new ImportNoAuthException();
                        }
                        if (!Objects.equals(mainImportExportVo.getType(), targetType)) {
                            throw new ImportExportTypeInconsistencyException(mainImportExportVo.getType(), targetType);
                        }
                        boolean alreadyExists = false;
                        JSONObject resultObj = new JSONObject();
                        ImportExportBaseInfoVo mainImportExportBaseInfoVo = new ImportExportBaseInfoVo(mainImportExportVo.getType(), mainImportExportVo.getPrimaryKey(), mainImportExportVo.getName());
                        if (importExportHandler.checkIsExists(mainImportExportBaseInfoVo)) {
                            mainImportExportBaseInfoVo.setType(importExportHandler.getType().getText());
                            resultObj.put("alreadyExists", mainImportExportBaseInfoVo);
                            alreadyExists = true;
                        }
                        List<ImportExportBaseInfoVo> dependencyBaseInfoList = mainImportExportVo.getDependencyBaseInfoList();
                        if (CollectionUtils.isNotEmpty(dependencyBaseInfoList) && StringUtils.isBlank(userSelection)) {
                            List<ImportDependencyTypeVo> importDependencyTypeList = importExportHandler.checkDependencyList(dependencyBaseInfoList);
                            if (CollectionUtils.isNotEmpty(importDependencyTypeList)) {
                                resultObj.put("checkedAll", false);
                                resultObj.put("typeList", importDependencyTypeList);
                                return resultObj;
                            } else {
                                checkAll = true;
                                resultObj.put("checkedAll", true);
                                resultObj.put("typeList", new ArrayList<>());
                            }
                        }
                        if (alreadyExists) {
                            return resultObj;
                        }
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        List<ImportExportPrimaryChangeVo> primaryChangeList = new ArrayList<>();
        Map<Long, FileVo> fileMap = new HashMap<>();
        List<String> messageList = new ArrayList<>();
        // 第二次遍历压缩包，导入dependency-folder/{primaryKey}.json和{primaryKey}.json文件
        try (ZipInputStream zipIs = new ZipInputStream(multipartFile.getInputStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            ZipEntry zipEntry = null;
            while ((zipEntry = zipIs.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                }
                out.reset();
                String zipEntryName = zipEntry.getName();
                if (zipEntryName.startsWith("dependency-folder/")) {
                    int len;
                    while ((len = zipIs.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    ImportExportVo dependencyVo = JSONObject.parseObject(new String(out.toByteArray(), StandardCharsets.UTF_8), ImportExportVo.class);
                    ImportExportHandler importExportHandler = getHandler(dependencyVo.getType());
                    if (importExportHandler == null) {
                        throw new ImportExportHandlerNotFoundException(dependencyVo.getType());
                    }
                    Object oldPrimaryKey = dependencyVo.getPrimaryKey();
                    boolean flag = true;
                    if (!checkAll) {
                        flag = check(typeList, dependencyVo.getType(), oldPrimaryKey);
                    }
                    Object newPrimaryKey = null;
                    if (flag) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("import data: " + dependencyVo.getType() + "-" + dependencyVo.getName() + "-" + oldPrimaryKey);
                        }
                        try {
                            newPrimaryKey = importExportHandler.importData(dependencyVo, primaryChangeList);
                            if (Objects.equals(dependencyVo.getType(), FrameworkImportExportHandlerType.FILE.getValue())) {
                                FileVo fileVo = dependencyVo.getData().toJavaObject(FileVo.class);
                                fileMap.put(fileVo.getId(), fileVo);
                            }
                        } catch (DependencyNotFoundException e) {
                            messageList.addAll(e.getMessageList());
                        }
                    } else {
                        newPrimaryKey = importExportHandler.getPrimaryByName(dependencyVo);
                    }
                    if (!Objects.equals(oldPrimaryKey, newPrimaryKey)) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("oldPrimaryKey = " + oldPrimaryKey + ",newPrimaryKey = " + newPrimaryKey);
                        }
                        primaryChangeList.add(new ImportExportPrimaryChangeVo(dependencyVo.getType(), oldPrimaryKey, newPrimaryKey));
                    }
                } else if (zipEntryName.startsWith("attachment-folder/")) {
                    continue;
                } else {
                    int len;
                    while ((len = zipIs.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    ImportExportVo importExportVo = JSONObject.parseObject(new String(out.toByteArray(), StandardCharsets.UTF_8), ImportExportVo.class);
                    ImportExportHandler importExportHandler = getHandler(importExportVo.getType());
                    if (importExportHandler == null) {
                        throw new ImportExportHandlerNotFoundException(importExportVo.getType());
                    }
                    try {
                        importExportHandler.importData(importExportVo, primaryChangeList);
                    } catch (DependencyNotFoundException e) {
                        messageList.addAll(e.getMessageList());
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        if (CollectionUtils.isNotEmpty(messageList)) {
            throw new DependencyNotFoundException(messageList);
        }
        // 第三次遍历压缩包，导入attachment-folder/{primaryKey}/xxx文件
        try (ZipInputStream zipIs = new ZipInputStream(multipartFile.getInputStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            ZipEntry zipEntry = null;
            while ((zipEntry = zipIs.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                }
                out.reset();
                String zipEntryName = zipEntry.getName();
                if (zipEntryName.startsWith("attachment-folder/")) {
                    String tenantUuid = TenantContext.get().getTenantUuid();
                    int beginIndex = zipEntryName.indexOf("/");
                    int endIndex = zipEntryName.indexOf("/", beginIndex + 1);
                    String fileIdStr = zipEntryName.substring(beginIndex + 1, endIndex);
                    Long fileId = Long.valueOf(fileIdStr);
                    boolean flag = true;
                    if (!checkAll) {
                        flag = check(typeList, FrameworkImportExportHandlerType.FILE.getValue(), fileId);
                    }
                    if (flag) {
                        FileVo fileVo = fileMap.get(fileId);
                        Object newPrimary = getNewPrimaryKey(FrameworkImportExportHandlerType.FILE.getValue(), fileId, primaryChangeList);
                        if (newPrimary != null) {
                            fileVo.setId((Long) newPrimary);
                        }
                        int len;
                        while ((len = zipIs.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                        String filePath = FileUtil.saveData(tenantUuid, in, fileVo);
                        in.close();
                        fileVo.setPath(filePath);
                        fileMapper.updateFile(fileVo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取新的primary，如果返回结果为null，说明primary没有变化
     *
     * @param type
     * @param oldPrimary
     * @param primaryChangeList
     * @return
     */
    private static Object getNewPrimaryKey(String type, Object oldPrimary, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        for (ImportExportPrimaryChangeVo primaryChangeVo : primaryChangeList) {
            if (Objects.equals(primaryChangeVo.getType(), type) && Objects.equals(primaryChangeVo.getOldPrimaryKey(), oldPrimary)) {
                return primaryChangeVo.getNewPrimaryKey();
            }
        }
        return null;
    }

    /**
     * 检查是否需要导入数据
     *
     * @param typeList
     * @param type
     * @param primaryKey
     * @return
     */
    private static boolean check(List<ImportDependencyTypeVo> typeList, String type, Object primaryKey) {
        for (ImportDependencyTypeVo typeVo : typeList) {
            if (Objects.equals(typeVo.getValue(), type)) {
                if (!Objects.equals(typeVo.getCheckedAll(), true)) {
                    for (ImportDependencyOptionVo optionVo : typeVo.getOptionList()) {
                        if (Objects.equals(optionVo.getValue(), primaryKey)) {
                            if (!Objects.equals(optionVo.getChecked(), true)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
