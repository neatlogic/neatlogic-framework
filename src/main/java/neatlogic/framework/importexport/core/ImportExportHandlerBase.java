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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.importexport.dto.*;
import neatlogic.framework.importexport.exception.ImportExportHandlerNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class ImportExportHandlerBase implements ImportExportHandler {

    private static Logger logger = LoggerFactory.getLogger(ImportExportHandlerBase.class);

    protected final String IMPORT = "import";
    protected final String EXPORT = "export";
    /**
     * 检查导入依赖列表中的对象是否已经存在，存在则需要让用户决定是否覆盖
     * @param dependencyBaseInfoList
     * @return
     */
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
            if (StringUtils.isBlank(dependencyBaseInfoVo.getName())) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The name of the dependency object " + JSONObject.toJSONString(dependencyBaseInfoVo) + " is null");
                }
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
    /**
     * 导出数据
     * @param primaryKey 导出对象主键
     * @param dependencyList 导出对象依赖列表
     * @param zipOutputStream 压缩输出流
     * @return
     */
    @Override
    public ImportExportVo exportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        ImportExportVo importExportVo = myExportData(primaryKey, dependencyList, zipOutputStream);
        if (importExportVo != null) {
            importExportVo.setType(this.getType().getValue());
            return importExportVo;
        }
        return null;
    }

    /**
     * 导出数据
     * @param primaryKey 导出对象主键
     * @param dependencyList 导出对象依赖列表
     * @param zipOutputStream 压缩输出流
     * @return
     */
    protected abstract ImportExportVo myExportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream);

    /**
     * 导出依赖对象数据
     * @param type
     * @param primaryKey
     * @param dependencyList
     * @param zipOutputStream
     */
    protected void doExportData(ImportExportHandlerType type, Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        ImportExportHandler importExportHandler = ImportExportHandlerFactory.getHandler(type.getValue());
        if (importExportHandler == null) {
            throw new ImportExportHandlerNotFoundException(type.getText());
        }
        for (ImportExportBaseInfoVo importExportVo : dependencyList) {
            if (Objects.equals(importExportVo.getPrimaryKey(), primaryKey) && Objects.equals(importExportVo.getType(), type.getValue())) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The data of type " + importExportVo.getType() + " whose primary key is " + importExportVo.getPrimaryKey() + " has been exported");
                }
                return;
            }
        }
        ImportExportBaseInfoVo dependencyVo = new ImportExportBaseInfoVo(type.getValue(), primaryKey);
        dependencyList.add(dependencyVo);
        ImportExportVo importExportVo = importExportHandler.exportData(primaryKey, dependencyList, zipOutputStream);
        if (importExportVo != null) {
            dependencyVo.setName(importExportVo.getName());
            if (zipOutputStream != null) {
                if (logger.isWarnEnabled()) {
                    logger.warn("export data: " + importExportVo.getType() + "-" + importExportVo.getName() + "-" + importExportVo.getPrimaryKey());
                }
                try {
                    zipOutputStream.putNextEntry(new ZipEntry("dependency-folder/" + importExportVo.getPrimaryKey() + ".json"));
                    zipOutputStream.write(JSONObject.toJSONBytes(importExportVo));
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 获取新的primary，如果返回结果为null，说明primary没有变化
     * @param type
     * @param oldPrimary
     * @param primaryChangeList
     * @return
     */
    protected Object getNewPrimaryKey(ImportExportHandlerType type, Object oldPrimary, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        for (ImportExportPrimaryChangeVo primaryChangeVo : primaryChangeList) {
            if (Objects.equals(primaryChangeVo.getType(), type.getValue()) && Objects.equals(primaryChangeVo.getOldPrimaryKey(), oldPrimary)) {
                return primaryChangeVo.getNewPrimaryKey();
            }
        }
        return null;
    }
}
