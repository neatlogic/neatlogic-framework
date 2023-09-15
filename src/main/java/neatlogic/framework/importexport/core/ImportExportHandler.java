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

import neatlogic.framework.importexport.dto.ImportDependencyTypeVo;
import neatlogic.framework.importexport.dto.ImportExportBaseInfoVo;
import neatlogic.framework.importexport.dto.ImportExportPrimaryChangeVo;
import neatlogic.framework.importexport.dto.ImportExportVo;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface ImportExportHandler {

    /**
     * 处理器唯一标识
     * @return
     */
    ImportExportHandlerType getType();

    /**
     * 检验导入授权
     * @param importExportVo 导入对象数据
     * @return
     */
    boolean checkImportAuth(ImportExportVo importExportVo);

    /**
     * 校验导出授权
     * @param primaryKey 主键
     * @return
     */
    boolean checkExportAuth(Object primaryKey);

    /**
     * 检查导入对象是否存在
     * @param importExportBaseInfoVo
     * @return
     */
    boolean checkIsExists(ImportExportBaseInfoVo importExportBaseInfoVo);

    /**
     * 检查导入依赖列表中的对象是否已经存在，存在则需要让用户决定是否覆盖
     * @param dependencyBaseInfoList
     * @return
     */
    List<ImportDependencyTypeVo> checkDependencyList(List<ImportExportBaseInfoVo> dependencyBaseInfoList);

    /**
     * 导入数据
     * @param importExportVo 导入对象信息
     * @param primaryChangeList 导入依赖对象中主键发生改变的主键映射对象列表
     * @return
     */
    Object importData(ImportExportVo importExportVo, List<ImportExportPrimaryChangeVo> primaryChangeList);

    /**
     * 导出数据
     * @param primaryKey 导出对象主键
     * @param dependencyList 导出对象依赖列表
     * @param zipOutputStream 压缩输出流
     * @return
     */
    ImportExportVo exportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream);
}
