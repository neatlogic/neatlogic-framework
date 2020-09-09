package codedriver.framework.matrix.dao.mapper;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.matrix.dto.MatrixColumnVo;
import codedriver.framework.matrix.dto.MatrixDataVo;

import java.util.List;
import java.util.Map;

public interface MatrixDataMapper {
    public int insertDynamicTableData(@Param("rowData") List<MatrixColumnVo> rowData, @Param("matrixUuid") String matrixUuid);

	public int updateDynamicTableDataByUuid(@Param("rowData") List<MatrixColumnVo> rowData, @Param("uuidColumn") MatrixColumnVo uuidColumn, @Param("matrixUuid") String matrixUuid);

    public int deleteDynamicTableDataByUuid(@Param("matrixUuid") String matrixUuid, @Param("uuid") String uuid);

    public int getDynamicTableDataCount(MatrixDataVo dataVo);

    public int getDynamicTableDataCountByUuid(@Param("uuid") String uuid,@Param("matrixUuid") String matrixUuid);

    public List<Map<String, String>> searchDynamicTableData(MatrixDataVo dataVo);

    public List<Map<String, String>> getDynamicTableDataByColumnList(MatrixDataVo dataVo);
    
    public List<Map<String, String>> getDynamicTableDataByUuidList(MatrixDataVo processMatrixDataVo);

	public int getDynamicTableDataByColumnCount(MatrixDataVo dataVo);
	
	public int getDynamicTableDataByUuidCount(MatrixDataVo dataVo);
	
	public List<String> getDynamicTableCellData(@Param("matrixUuid") String matrixUuid, @Param("sourceColumnVo") MatrixColumnVo sourceColumnVo, @Param("targetColumn") String targetColumn);

	public List<Map<String, String>> getDynamicTableDataByColumnList2(MatrixDataVo dataVo);

	public List<String> getUuidListByAttributeValueListForSelectType(
			@Param("matrixUuid") String matrixUuid, 
			@Param("attributeUuid") String attributeUuid, 
			@Param("attributeValueList") List<String> attributeValueList, 
			@Param("pageSize") int pageSize
			);

	public List<String> getUuidListByKeywordForUserType(
			@Param("matrixUuid") String matrixUuid, 
			@Param("attributeUuid") String attributeUuid, 
			@Param("keyword") String keyword, 
			@Param("pageSize") int pageSize
			);

	public List<String> getUuidListByKeywordForTeamType(
			@Param("matrixUuid") String matrixUuid, 
			@Param("attributeUuid") String attributeUuid, 
			@Param("keyword") String keyword, 
			@Param("pageSize") int pageSize
			);

	public List<String> getUuidListByKeywordForRoleType(
			@Param("matrixUuid") String matrixUuid, 
			@Param("attributeUuid") String attributeUuid, 
			@Param("keyword") String keyword, 
			@Param("pageSize") int pageSize
			);

	public List<String> getUuidListByKeywordForDateType(
			@Param("matrixUuid") String matrixUuid, 
			@Param("attributeUuid") String attributeUuid, 
			@Param("keyword") String keyword, 
			@Param("pageSize") int pageSize
			);

	public List<String> getUuidListByKeywordForInputType(
			@Param("matrixUuid") String matrixUuid, 
			@Param("attributeUuid") String attributeUuid, 
			@Param("keyword") String keyword, 
			@Param("pageSize") int pageSize
			);

	public Map<String, String> getDynamicRowDataByUuid(MatrixDataVo dataVo);

	public Map<String, Long> checkMatrixAttributeHasDataByAttributeUuidList(@Param("matrixUuid") String matrixUuid, @Param("attributeUuidList") List<String> attributeUuidList);

	public int insertDynamicTableDataForCopy(
			@Param("sourceMatrixUuid") String sourceMatrixUuid, 
			@Param("sourceColumnList") List<String> sourceColumnList, 
			@Param("targetMatrixUuid") String targetMatrixUuid, 
			@Param("targetColumnList") List<String> targetColumnList
			);
}
