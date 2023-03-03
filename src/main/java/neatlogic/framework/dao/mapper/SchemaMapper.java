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

package neatlogic.framework.dao.mapper;

import neatlogic.framework.matrix.dto.MatrixViewAttributeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SchemaMapper {

    List<Map<String, String>> testCiViewSql(String sql);

//    int checkTableHasData(String tableName);

//    int checkTableIsExists(@Param("schemaName") String schemaName, @Param("tableName") String tableName);

    int checkSchemaIsExists(String databaseName);

    String checkTableOrViewIsExists(@Param("tableSchema") String tableSchema, @Param("tableName") String tableName);

    List<String> getTableOrViewAllColumnNameList(@Param("tableSchema") String tableSchema, @Param("tableName") String tableName);

//    void insertAttrToCiTable(@Param("tableName") String tableName, @Param("attrVo") MatrixViewAttributeVo attrVo);

//    void insertCiTable(@Param("tableName") String tableName);

    void insertView(String sql);

//    void deleteAttrFromCiTable(@Param("tableName") String tableName, @Param("attrVo") MatrixViewAttributeVo attrVo);

    void deleteTable(String tableName);

    void deleteView(String tableName);

}
