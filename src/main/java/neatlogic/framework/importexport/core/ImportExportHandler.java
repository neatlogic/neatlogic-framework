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
     * 通过名称判断依赖对象存在，如果用户选择不导入新的依赖对象时，需要通过名称查出primary值
     * @param importExportVo
     * @return
     */
    Object getPrimaryByName(ImportExportVo importExportVo);

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
