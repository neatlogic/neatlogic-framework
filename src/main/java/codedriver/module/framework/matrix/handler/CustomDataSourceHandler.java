/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.matrix.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.RoleVo;
import codedriver.framework.dto.TeamVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.matrix.constvalue.MatrixAttributeType;
import codedriver.framework.matrix.constvalue.MatrixType;
import codedriver.framework.matrix.core.MatrixDataSourceHandlerBase;
import codedriver.framework.matrix.dao.mapper.MatrixAttributeMapper;
import codedriver.framework.matrix.dao.mapper.MatrixDataMapper;
import codedriver.framework.matrix.dto.MatrixAttributeVo;
import codedriver.framework.matrix.dto.MatrixColumnVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import codedriver.framework.matrix.dto.MatrixVo;
import codedriver.framework.matrix.exception.*;
import codedriver.framework.util.ExcelUtil;
import codedriver.framework.util.TableResultUtil;
import codedriver.framework.util.UuidUtil;
import codedriver.module.framework.matrix.service.MatrixService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author linbq
 * @since 2021/11/4 19:34
 **/
@Component
public class CustomDataSourceHandler extends MatrixDataSourceHandlerBase {

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private MatrixAttributeMapper attributeMapper;

    @Resource
    private MatrixDataMapper matrixDataMapper;

    @Resource
    private MatrixService matrixService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public String getHandler() {
        return MatrixType.CUSTOM.getValue();
    }

    @Override
    protected boolean mySaveMatrix(MatrixVo matrixVo) throws Exception {
        return true;
    }

    @Override
    protected void myGetMatrix(MatrixVo matrixVo) {

    }

    @Override
    protected void myDeleteMatrix(String uuid) {
        attributeMapper.deleteAttributeByMatrixUuid(uuid);
        attributeMapper.dropMatrixDynamicTable(uuid, TenantContext.get().getDataDbName());
    }

    @Override
    protected void myCopyMatrix(String sourceUuid, MatrixVo matrixVo) {
        List<MatrixAttributeVo> attributeVoList = attributeMapper.getMatrixAttributeByMatrixUuid(sourceUuid);
        if (CollectionUtils.isNotEmpty(attributeVoList)) {
            String targetUuid = matrixVo.getUuid();
            //属性拷贝
            List<String> sourceColumnList = new ArrayList<>();
            List<String> targetColumnList = new ArrayList<>();
            for (MatrixAttributeVo attributeVo : attributeVoList) {
                String sourceAttributeUuid = attributeVo.getUuid();
                String targetAttributeUuid = UuidUtil.randomUuid();
                sourceColumnList.add(sourceAttributeUuid);
                targetColumnList.add(targetAttributeUuid);
                attributeVo.setMatrixUuid(targetUuid);
                attributeVo.setUuid(targetAttributeUuid);
                attributeMapper.insertMatrixAttribute(attributeVo);
            }
            String schemaName = TenantContext.get().getDataDbName();
            attributeMapper.createMatrixDynamicTable(attributeVoList, targetUuid, schemaName);
            //数据拷贝
            matrixDataMapper.insertDynamicTableDataForCopy(sourceUuid, sourceColumnList, targetUuid, targetColumnList, schemaName);
        }
    }

    @Override
    protected JSONObject myImportMatrix(MatrixVo matrixVo,MultipartFile multipartFile) throws IOException {
        JSONObject returnObj = new JSONObject();
        int update = 0, insert = 0, unExist = 0;
//        MultipartFile multipartFile;
        InputStream is;
//        MultipartFile multipartFile = entry.getValue();
        is = multipartFile.getInputStream();
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isNotBlank(originalFilename) && originalFilename.contains(".")) {
            originalFilename = originalFilename.substring(0, originalFilename.indexOf("."));
        }
        if (StringUtils.isNotBlank(originalFilename) && !originalFilename.equals(matrixVo.getName())) {
            throw new MatrixNameDifferentImportFileNameException();
        }
        String matrixUuid = matrixVo.getUuid();
        List<MatrixAttributeVo> attributeVoList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixUuid);
        if (CollectionUtils.isNotEmpty(attributeVoList)) {
            Map<String, MatrixAttributeVo> headerMap = new HashMap<>();
            for (MatrixAttributeVo attributeVo : attributeVoList) {
                headerMap.put(attributeVo.getName(), attributeVo);
            }
            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);
            int rowNum = sheet.getLastRowNum();
            //获取头栏位
            Row headerRow = sheet.getRow(0);
            int colNum = headerRow.getLastCellNum();
            //attributeList 缺少uuid
            if (colNum != attributeVoList.size() + 1) {
                throw new MatrixHeaderMisMatchException(originalFilename);
            }
            //解析数据
            for (int i = 1; i <= rowNum; i++) {
                Row row = sheet.getRow(i);
                boolean isNew = false;
                String uuid = null;
                List<MatrixColumnVo> rowData = new ArrayList<>();
                for (int j = 0; j < colNum; j++) {
                    Cell tbodycell = row.getCell(j);
                    String value = getCellValue(tbodycell);
                    Cell theadCell = headerRow.getCell(j);
                    String columnName = theadCell.getStringCellValue();
                    if (("uuid").equals(columnName)) {
                        MatrixDataVo dataVo = new MatrixDataVo();
                        dataVo.setMatrixUuid(matrixUuid);
                        dataVo.setUuid(value);
                        if (StringUtils.isBlank(value) || matrixDataMapper.getDynamicTableDataCountByUuid(dataVo) == 0) {
                            uuid = UuidUtil.randomUuid();
                            isNew = true;
                            rowData.add(new MatrixColumnVo("uuid", uuid));
                        } else {
                            uuid = value;
                        }
                    } else {
                        MatrixAttributeVo attributeVo = headerMap.get(columnName);
                        if (attributeVo != null) {
                            String attributeUuid = attributeVo.getUuid();
                            if (StringUtils.isNotBlank(attributeUuid)) {
                                if (matrixService.matrixAttributeValueVerify(attributeVo, value)) {
                                    rowData.add(new MatrixColumnVo(attributeUuid, value));
                                } else {
                                    throw new MatrixImportDataIllegalException(i + 1, j + 1, value);
                                }
                            }
                        }
                    }
                }
                if (isNew) {
                    matrixDataMapper.insertDynamicTableData(rowData, matrixUuid, TenantContext.get().getDataDbName());
                    insert++;
                    update++;
                } else {
                    matrixDataMapper.updateDynamicTableDataByUuid(rowData, uuid, matrixUuid, TenantContext.get().getDataDbName());
                }
            }
        } else {
            throw new MatrixDataNotFoundException(originalFilename);
        }
        returnObj.put("insert", insert);
        returnObj.put("update", update);
        returnObj.put("unExist", unExist);
        return returnObj;
    }

    @Override
    protected HSSFWorkbook myExportMatrix(MatrixVo matrixVo) {
        HSSFWorkbook workbook = null;
        List<MatrixAttributeVo> attributeVoList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixVo.getUuid());
        if (CollectionUtils.isNotEmpty(attributeVoList)) {
            List<String> headerList = new ArrayList<>();
            List<String> columnList = new ArrayList<>();
            List<List<String>> columnSelectValueList = new ArrayList<>();
            headerList.add("uuid");
            columnList.add("uuid");
            columnSelectValueList.add(new ArrayList<>());
            for (MatrixAttributeVo attributeVo : attributeVoList) {
                headerList.add(attributeVo.getName());
                columnList.add(attributeVo.getUuid());
                List<String> selectValueList = new ArrayList<>();
                decodeDataConfig(attributeVo, selectValueList);
                columnSelectValueList.add(selectValueList);
            }
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixVo.getUuid());
            dataVo.setColumnList(columnList);

            int currentPage = 1;
            dataVo.setPageSize(1000);
            int rowNum = matrixDataMapper.getDynamicTableDataCount(dataVo);
            int pageCount = PageUtil.getPageCount(rowNum, dataVo.getPageSize());
            while (currentPage <= pageCount) {
                dataVo.setCurrentPage(currentPage);
                dataVo.setStartNum(null);
                List<Map<String, String>> dataMapList = matrixDataMapper.searchDynamicTableData(dataVo);
                /* 转换用户、分组、角色字段值为用户名、分组名、角色名 **/
                if (CollectionUtils.isNotEmpty(dataMapList)) {
                    for (Map<String, String> map : dataMapList) {
                        for (MatrixAttributeVo attributeVo : attributeVoList) {
                            String value = map.get(attributeVo.getUuid());
                            if (StringUtils.isNotBlank(value)) {
                                if (GroupSearch.USER.getValue().equals(attributeVo.getType())) {
                                    UserVo user = userMapper.getUserBaseInfoByUuid(value);
                                    if (user != null) {
                                        map.put(attributeVo.getUuid(), user.getUserName());
                                    }
                                } else if (GroupSearch.TEAM.getValue().equals(attributeVo.getType())) {
                                    TeamVo team = teamMapper.getTeamByUuid(value);
                                    if (team != null) {
                                        map.put(attributeVo.getUuid(), team.getName());
                                    }
                                } else if (GroupSearch.ROLE.getValue().equals(attributeVo.getType())) {
                                    RoleVo role = roleMapper.getRoleByUuid(value);
                                    if (role != null) {
                                        map.put(attributeVo.getUuid(), role.getName());
                                    }
                                }
                            }
                        }
                    }
                }
                workbook = ExcelUtil.createExcel(workbook, headerList, columnList, columnSelectValueList, dataMapList);
                currentPage++;
            }
        }
        return workbook;
    }

    @Override
    protected void mySaveAttributeList(String matrixUuid, List<MatrixAttributeVo> attributeVoList) {
//        List<MatrixAttributeVo> attributeVoList = jsonObj.getJSONArray("matrixAttributeList").toJavaList(MatrixAttributeVo.class);
        List<MatrixAttributeVo> oldMatrixAttributeList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixUuid);
        boolean dataExist = CollectionUtils.isNotEmpty(oldMatrixAttributeList);
        if (dataExist) {
            attributeMapper.deleteAttributeByMatrixUuid(matrixUuid);
        }
        String schemaName = TenantContext.get().getDataDbName();
        if (CollectionUtils.isNotEmpty(attributeVoList)) {
            //有数据
            if (dataExist) {
                //数据对比
                //删除数据
                //调整表
                List<String> oldAttributeUuidList = oldMatrixAttributeList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
                List<String> addAttributeUuidList = new ArrayList<>();
                List<String> existedAttributeUuidList = new ArrayList<>();
                for (MatrixAttributeVo attributeVo : attributeVoList) {
                    attributeVo.setMatrixUuid(matrixUuid);
                    if (oldAttributeUuidList.contains(attributeVo.getUuid())) {
                        attributeMapper.insertMatrixAttribute(attributeVo);
                        existedAttributeUuidList.add(attributeVo.getUuid());
                    } else {
                        //过滤新增属性uuid
                        attributeMapper.insertMatrixAttribute(attributeVo);
                        addAttributeUuidList.add(attributeVo.getUuid());
                    }
                }

                //添加新增字段
                for (String attributeUuid : addAttributeUuidList) {
                    attributeMapper.addMatrixDynamicTableColumn(attributeUuid, matrixUuid, schemaName);
                }
                //找出需要删除的属性uuid列表
                oldAttributeUuidList.removeAll(existedAttributeUuidList);
                for (String attributeUuid : oldAttributeUuidList) {
                    attributeMapper.dropMatrixDynamicTableColumn(attributeUuid, matrixUuid, schemaName);
                }
            } else {
                for (MatrixAttributeVo attributeVo : attributeVoList) {
                    attributeVo.setMatrixUuid(matrixUuid);
                    attributeVo.setUuid(UuidUtil.randomUuid());
                    attributeMapper.insertMatrixAttribute(attributeVo);
                }
                attributeMapper.createMatrixDynamicTable(attributeVoList, matrixUuid, schemaName);
            }
        } else {
            //无数据
            if (dataExist) {
                // 删除动态表
                attributeMapper.dropMatrixDynamicTable(matrixUuid, schemaName);
            }
        }
    }

    @Override
    protected List<MatrixAttributeVo> myGetAttributeList(MatrixVo matrixVo) {
        List<MatrixAttributeVo> matrixAttributeList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixVo.getUuid());
        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
            List<String> attributeUuidList = matrixAttributeList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
            Map<String, Long> attributeDataCountMap = matrixDataMapper.checkMatrixAttributeHasDataByAttributeUuidList(matrixVo.getUuid(), attributeUuidList, TenantContext.get().getDataDbName());
            for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
                long count = attributeDataCountMap.get(matrixAttributeVo.getUuid());
                matrixAttributeVo.setIsDeletable(count == 0 ? 1 : 0);
            }
        }
        return matrixAttributeList;
    }

    @Override
    protected JSONObject myExportAttribute(MatrixVo matrixVo) {
        JSONObject resultObj = new JSONObject();
        List<MatrixAttributeVo> attributeVoList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixVo.getUuid());
        if (CollectionUtils.isNotEmpty(attributeVoList)) {
            List<String> headerList = new ArrayList<>();
            List<List<String>> columnSelectValueList = new ArrayList<>();
            headerList.add("uuid");
            columnSelectValueList.add(new ArrayList<>());
            for (MatrixAttributeVo attributeVo : attributeVoList) {
                headerList.add(attributeVo.getName());
                List<String> selectValueList = new ArrayList<>();
                decodeDataConfig(attributeVo, selectValueList);
                columnSelectValueList.add(selectValueList);
            }
            resultObj.put("headerList", headerList);
            resultObj.put("columnSelectValueList", columnSelectValueList);
        }
        return resultObj;
    }

    @Override
    protected JSONObject myGetTableData(MatrixDataVo dataVo) {
        List<MatrixAttributeVo> attributeVoList = attributeMapper.getMatrixAttributeByMatrixUuid(dataVo.getMatrixUuid());
        if (CollectionUtils.isNotEmpty(attributeVoList)) {
            List<String> columnList = attributeVoList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
            dataVo.setColumnList(columnList);
            if (dataVo.getNeedPage()) {
                int rowNum = matrixDataMapper.getDynamicTableDataCount(dataVo);
                dataVo.setRowNum(rowNum);
            }
            List<Map<String, String>> dataList = matrixDataMapper.searchDynamicTableData(dataVo);
            List<Map<String, Object>> tbodyList = matrixService.matrixTableDataValueHandle(attributeVoList, dataList);
            JSONArray theadList = getTheadList(attributeVoList);
            return TableResultUtil.getResult(theadList, tbodyList, dataVo);
        }
        return new JSONObject();
    }

    @Override
    protected JSONObject myTableDataSearch(MatrixDataVo dataVo) {
        JSONObject returnObj = new JSONObject();
        List<MatrixAttributeVo> matrixAttributeList = attributeMapper.getMatrixAttributeByMatrixUuid(dataVo.getMatrixUuid());
        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
            JSONArray dafaultValue = dataVo.getDefaultValue();
            List<Map<String, String>> dataMapList = null;
            if (CollectionUtils.isNotEmpty(dafaultValue)) {
                dataMapList = matrixDataMapper.getDynamicTableDataByUuidList(dataVo);
            } else {
                if (dataVo.getNeedPage()) {
                    int rowNum = matrixDataMapper.getDynamicTableDataByColumnCount(dataVo);
                    dataVo.setRowNum(rowNum);
                }
                dataMapList = matrixDataMapper.getDynamicTableDataByColumnList(dataVo);
            }
            List<Map<String, Object>> tbodyList = matrixService.matrixTableDataValueHandle(matrixAttributeList, dataMapList);
            JSONArray theadList = getTheadList(dataVo.getMatrixUuid(), matrixAttributeList, dataVo.getColumnList());
            returnObj = TableResultUtil.getResult(theadList, tbodyList, dataVo);
//            returnObj.put("searchColumnDetailList", getSearchColumnDetailList(dataVo.getMatrixUuid(), matrixAttributeList, searchColumnArray));
        }
        return returnObj;
    }

    @Override
    protected List<Map<String, JSONObject>> myTableColumnDataSearch(MatrixDataVo dataVo) {
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        List<MatrixAttributeVo> attributeList = attributeMapper.getMatrixAttributeByMatrixUuid(dataVo.getMatrixUuid());
        if (CollectionUtils.isNotEmpty(attributeList)) {
            Map<String, MatrixAttributeVo> matrixAttributeMap = new HashMap<>();
            for (MatrixAttributeVo matrixAttributeVo : attributeList) {
                matrixAttributeMap.put(matrixAttributeVo.getUuid(), matrixAttributeVo);
            }
            List<String> columnList = dataVo.getColumnList();
            for (String column : columnList) {
                if (!matrixAttributeMap.containsKey(column)) {
                    throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), column);
                }
            }
            String keywordColumn = dataVo.getKeywordColumn();
            JSONArray defaultValue = dataVo.getDefaultValue();
            if (CollectionUtils.isNotEmpty(defaultValue)) {
                for (String value : defaultValue.toJavaList(String.class)) {
                    if (value.contains(SELECT_COMPOSE_JOINER)) {
                        List<MatrixColumnVo> sourceColumnList = new ArrayList<>();
                        String[] split = value.split(SELECT_COMPOSE_JOINER);
                        if (StringUtils.isNotBlank(columnList.get(0))) {
                            MatrixColumnVo matrixColumnVo = new MatrixColumnVo(columnList.get(0), split[0]);
                            matrixColumnVo.setExpression(Expression.EQUAL.getExpression());
                            sourceColumnList.add(matrixColumnVo);
                        }
                        dataVo.setSourceColumnList(sourceColumnList);
                        if (columnList.size() >= 2) {
                            keywordColumn = columnList.get(1);
                        } else {
                            keywordColumn = columnList.get(0);
                        }
                        MatrixAttributeVo matrixAttribute = matrixAttributeMap.get(keywordColumn);
                        if (matrixAttribute == null) {
                            throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), keywordColumn);
                        }
                        dataVo.setKeyword(split[1]);
                        List<Map<String, String>> dataMapList = matrixService.matrixAttributeValueKeyWordSearch(matrixAttribute, dataVo);
                        if (CollectionUtils.isNotEmpty(dataMapList)) {
                            for (Map<String, String> dataMap : dataMapList) {
                                Map<String, JSONObject> resultMap = new HashMap<>(dataMap.size());
                                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                                    String attributeUuid = entry.getKey();
                                    resultMap.put(attributeUuid, matrixService.matrixAttributeValueHandle(matrixAttributeMap.get(attributeUuid), entry.getValue()));
                                }
                                JSONObject textObj = resultMap.get(keywordColumn);
                                if (MapUtils.isNotEmpty(textObj) && Objects.equal(textObj.get("text"), split[1])) {
                                    resultList.add(resultMap);
                                }
                            }
                        }
                    }
                }
            } else {
                MatrixAttributeVo matrixAttribute = null;
                if (StringUtils.isNotBlank(keywordColumn) && StringUtils.isNotBlank(dataVo.getKeyword())) {
                    matrixAttribute = matrixAttributeMap.get(keywordColumn);
                    if (matrixAttribute == null) {
                        throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), keywordColumn);
                    }
                }
                List<Map<String, String>> dataMapList = matrixService.matrixAttributeValueKeyWordSearch(matrixAttribute, dataVo);
                for (Map<String, String> dataMap : dataMapList) {
                    Map<String, JSONObject> resultMap = new HashMap<>(dataMap.size());
                    for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                        String attributeUuid = entry.getKey();
                        resultMap.put(attributeUuid, matrixService.matrixAttributeValueHandle(matrixAttributeMap.get(attributeUuid), entry.getValue()));
                    }
                    resultList.add(resultMap);
                }
            }
        }
        return resultList;
    }

    @Override
    protected JSONObject mySaveTableRowData(String matrixUuid, JSONObject rowDataObj) {
        List<MatrixAttributeVo> attributeList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixUuid);
        List<String> attributeUuidList = attributeList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
//        JSONObject rowDataObj = jsonObj.getJSONObject("rowData");
        for (String columnUuid : rowDataObj.keySet()) {
            if (!"uuid".equals(columnUuid) && !"id".equals(columnUuid) && !attributeUuidList.contains(columnUuid)) {
                throw new MatrixAttributeNotFoundException(matrixUuid, columnUuid);
            }
        }

        boolean hasData = false;
        List<MatrixColumnVo> rowData = new ArrayList<>();
        for (MatrixAttributeVo matrixAttributeVo : attributeList) {
            String value = rowDataObj.getString(matrixAttributeVo.getUuid());
            if (StringUtils.isNotBlank(value)) {
                hasData = true;
                if (MatrixAttributeType.USER.getValue().equals(matrixAttributeVo.getType())) {
                    value = value.split("#")[1];
                } else if (MatrixAttributeType.TEAM.getValue().equals(matrixAttributeVo.getType())) {
                    value = value.split("#")[1];
                } else if (MatrixAttributeType.ROLE.getValue().equals(matrixAttributeVo.getType())) {
                    value = value.split("#")[1];
                }
            }
            rowData.add(new MatrixColumnVo(matrixAttributeVo.getUuid(), value));
        }
        String schemaName = TenantContext.get().getDataDbName();
        String uuidValue = rowDataObj.getString("uuid");
        if (uuidValue == null) {
            if (hasData) {
                rowData.add(new MatrixColumnVo("uuid", UuidUtil.randomUuid()));
                matrixDataMapper.insertDynamicTableData(rowData, matrixUuid, schemaName);
            }
        } else {
            if (hasData) {
                matrixDataMapper.updateDynamicTableDataByUuid(rowData, uuidValue, matrixUuid, schemaName);
            } else {
                MatrixDataVo dataVo = new MatrixDataVo();
                dataVo.setMatrixUuid(matrixUuid);
                dataVo.setUuid(uuidValue);
                matrixDataMapper.deleteDynamicTableDataByUuid(dataVo);
            }
        }
        return null;
    }

    @Override
    protected Map<String, String> myGetTableRowData(MatrixDataVo dataVo) {
        List<MatrixAttributeVo> attributeVoList = attributeMapper.getMatrixAttributeByMatrixUuid(dataVo.getMatrixUuid());
        if (CollectionUtils.isNotEmpty(attributeVoList)) {
            List<String> columnList = attributeVoList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
            dataVo.setColumnList(columnList);
            Map<String, String> rowData = matrixDataMapper.getDynamicRowDataByUuid(dataVo);
            for (MatrixAttributeVo attributeVo : attributeVoList) {
                if (MatrixAttributeType.USER.getValue().equals(attributeVo.getType())) {
                    String value = rowData.get(attributeVo.getUuid());
                    if (value != null) {
                        value = GroupSearch.USER.getValuePlugin() + value;
                        rowData.put(attributeVo.getUuid(), value);
                    }
                } else if (MatrixAttributeType.TEAM.getValue().equals(attributeVo.getType())) {
                    String value = rowData.get(attributeVo.getUuid());
                    if (value != null) {
                        value = GroupSearch.TEAM.getValuePlugin() + value;
                        rowData.put(attributeVo.getUuid(), value);
                    }
                } else if (MatrixAttributeType.ROLE.getValue().equals(attributeVo.getType())) {
                    String value = rowData.get(attributeVo.getUuid());
                    if (value != null) {
                        value = GroupSearch.ROLE.getValuePlugin() + value;
                        rowData.put(attributeVo.getUuid(), value);
                    }
                }
            }
            return rowData;
        }
        return null;
    }

    @Override
    protected void myDeleteTableRowData(String matrixUuid, List<String> uuidList) {
//        List<String> uuidList = jsonObj.getJSONArray("uuidList").toJavaList(String.class);
        MatrixDataVo dataVo = new MatrixDataVo();
        dataVo.setMatrixUuid(matrixUuid);
        for (String uuid : uuidList) {
            dataVo.setUuid(uuid);
            matrixDataMapper.deleteDynamicTableDataByUuid(dataVo);
        }
    }

    private String getCellValue(Cell cell) {
        String value = "";
        if (cell != null) {
            if (cell.getCellType() != CellType.BLANK) {
                switch (cell.getCellType()) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            value = formatter.format(cell.getDateCellValue());
                        } else {
                            value = String.valueOf(cell.getNumericCellValue());
                        }
                        break;
                    case BOOLEAN:
                        value = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        value = cell.getCellFormula();
                        break;
                    default:
                        value = cell.getStringCellValue();
                        break;
                }
            }
        }
        return value;
    }

    /**
     * 解析config，抽取属性下拉框值
     * @param attributeVo
     * @param selectValueList
     */
    private void decodeDataConfig(MatrixAttributeVo attributeVo, List<String> selectValueList) {
        if (StringUtils.isNotBlank(attributeVo.getConfig())) {
            String config = attributeVo.getConfig();
            JSONObject configObj = JSONObject.parseObject(config);
            JSONArray dataList = configObj.getJSONArray("dataList");
            if (CollectionUtils.isNotEmpty(dataList)) {
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject dataObj = dataList.getJSONObject(i);
                    if (MapUtils.isNotEmpty(dataObj)) {
                        String value = dataObj.getString("value");
                        if (StringUtils.isNotBlank(value)) {
                            selectValueList.add(value);
                        }
                    }
                }
            }
        }
    }
}
