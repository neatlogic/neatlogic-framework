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

package neatlogic.framework.importexport.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.importexport.dto.*;
import neatlogic.framework.importexport.exception.ImportExportHandlerNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class ImportExportHandlerBase implements ImportExportHandler {

    private static Logger logger = LoggerFactory.getLogger(ImportExportHandlerBase.class);

    @Override
    public List<ImportDependencyTypeVo> checkDependencyList(List<ImportExportBaseInfoVo> dependencyBaseInfoList) {
        if (CollectionUtils.isEmpty(dependencyBaseInfoList)) {
            return null;
        }
        Map<String, ImportDependencyTypeVo> importDependencyTypeMap = new HashMap<>();
        for (ImportExportBaseInfoVo dependencyBaseInfoVo : dependencyBaseInfoList) {
            ImportExportHandler importExportHandler = ImportExportHandlerFactory.getHandler(dependencyBaseInfoVo.getType());
            if (importExportHandler == null) {
                throw new ImportExportHandlerNotFoundException(dependencyBaseInfoVo.getType());
            }
            if (importExportHandler.checkIsExists(dependencyBaseInfoVo)) {
                ImportDependencyTypeVo importDependencyTypeVo = importDependencyTypeMap.get(dependencyBaseInfoVo.getType());
                if (importDependencyTypeVo == null) {
                    importDependencyTypeVo = new ImportDependencyTypeVo();
                    ImportExportHandlerType type = importExportHandler.getType();
                    importDependencyTypeVo.setValue(type.getValue());
                    importDependencyTypeVo.setText(type.getText());
                    importDependencyTypeMap.put(dependencyBaseInfoVo.getType(), importDependencyTypeVo);
                }
                List<ImportDependencyOptionVo> optionList = importDependencyTypeVo.getOptionList();
                if (optionList == null) {
                    optionList = new ArrayList<>();
                    importDependencyTypeVo.setOptionList(optionList);
                }
                ImportDependencyOptionVo importDependencyOptionVo = new ImportDependencyOptionVo();
                importDependencyOptionVo.setValue(dependencyBaseInfoVo.getPrimaryKey());
                importDependencyOptionVo.setText(dependencyBaseInfoVo.getName());
                importDependencyOptionVo.setChecked(false);
                optionList.add(importDependencyOptionVo);
            }
        }
        if (MapUtils.isNotEmpty(importDependencyTypeMap)) {
            return new ArrayList<>(importDependencyTypeMap.values());
        }
        return null;
    }

    @Override
    public ImportExportVo exportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        for (ImportExportBaseInfoVo importExportVo : dependencyList) {
            if (Objects.equals(importExportVo.getPrimaryKey(), primaryKey) && Objects.equals(importExportVo.getType(), this.getType().getValue())) {
                return null;
            }
        }
        ImportExportVo importExportVo = myExportData(primaryKey, dependencyList, zipOutputStream);
        importExportVo.setType(this.getType().getValue());
        return importExportVo;
    }

    protected abstract ImportExportVo myExportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream);

    protected void doExportData(ImportExportHandlerType type, Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        ImportExportHandler importExportHandler = ImportExportHandlerFactory.getHandler(type.getValue());
        if (importExportHandler == null) {
            throw new ImportExportHandlerNotFoundException(type.getText());
        }
        ImportExportVo importExportVo = importExportHandler.exportData(primaryKey, dependencyList, zipOutputStream);
        if (importExportVo != null) {
            dependencyList.add(new ImportExportBaseInfoVo(importExportVo.getType(), importExportVo.getPrimaryKey(), importExportVo.getName()));
            try {
                zipOutputStream.putNextEntry(new ZipEntry("dependency-folder/" + importExportVo.getPrimaryKey() + ".json"));
                zipOutputStream.write(JSONObject.toJSONBytes(importExportVo));
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    protected Object getNewPrimaryKey(ImportExportHandlerType type, Object oldPrimary, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        for (ImportExportPrimaryChangeVo primaryChangeVo : primaryChangeList) {
            if (Objects.equals(primaryChangeVo.getType(), type.getValue()) && Objects.equals(primaryChangeVo.getOldPrimaryKey(), oldPrimary)) {
                return primaryChangeVo.getNewPrimaryKey();
            }
        }
        return null;
    }
}
