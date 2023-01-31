/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
