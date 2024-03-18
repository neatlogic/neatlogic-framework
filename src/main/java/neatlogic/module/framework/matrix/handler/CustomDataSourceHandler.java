/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.matrix.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.common.util.PageUtil;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.constvalue.MatrixType;
import neatlogic.framework.matrix.constvalue.SearchExpression;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerBase;
import neatlogic.framework.matrix.dao.mapper.MatrixAttributeMapper;
import neatlogic.framework.matrix.dao.mapper.MatrixDataMapper;
import neatlogic.framework.matrix.dto.*;
import neatlogic.framework.matrix.exception.*;
import neatlogic.framework.util.ExcelUtil;
import neatlogic.framework.util.TableResultUtil;
import neatlogic.framework.util.TimeUtil;
import neatlogic.framework.util.UuidUtil;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
        attributeMapper.dropMatrixDynamicTable(uuid);
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
            sourceColumnList.add("sort");
            targetColumnList.add("sort");
            attributeMapper.createMatrixDynamicTable(attributeVoList, targetUuid);
            //数据拷贝
            matrixDataMapper.insertDynamicTableDataForCopy(sourceUuid, sourceColumnList, targetUuid, targetColumnList);
        }
    }

    @Override
    protected JSONObject myImportMatrix(MatrixVo matrixVo, MultipartFile multipartFile) throws IOException {
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
                                if (matrixAttributeValueVerify(attributeVo, value)) {
                                    rowData.add(new MatrixColumnVo(attributeUuid, value));
                                } else {
                                    throw new MatrixImportDataIllegalException(i + 1, j + 1, value);
                                }
                            }
                        }
                    }
                }
                if (isNew) {
                    Integer maxSort = matrixDataMapper.getMaxSort(matrixUuid);
                    maxSort = maxSort == null ? 1 : maxSort + 1;
                    rowData.add(new MatrixColumnVo("sort", maxSort.toString()));
                    matrixDataMapper.insertDynamicTableData(rowData, matrixUuid);
                    insert++;
                    update++;
                } else {
                    matrixDataMapper.updateDynamicTableDataByUuid(rowData, uuid, matrixUuid);
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
    protected Workbook myExportMatrix2Excel(MatrixVo matrixVo) {
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
    protected MatrixVo myExportMatrix(MatrixVo matrixVo) {
        List<MatrixAttributeVo> attributeList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixVo.getUuid());
        JSONObject config = new JSONObject();
        config.put("attributeList", attributeList);
        List<String> columnList = attributeList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
        MatrixDataVo matrixDataVo = new MatrixDataVo();
        matrixDataVo.setMatrixUuid(matrixVo.getUuid());
        matrixDataVo.setColumnList(columnList);
        int rowNum = matrixDataMapper.getDynamicTableDataCount(matrixDataVo);
        if (rowNum > 0) {
            List<Map<String, String>> allDataList = new ArrayList<>(rowNum);
            matrixDataVo.setRowNum(rowNum);
            matrixDataVo.setPageSize(100);
            int pageCount = matrixDataVo.getPageCount();
            for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
                matrixDataVo.setCurrentPage(currentPage);
                List<Map<String, String>> dataList = matrixDataMapper.searchDynamicTableData(matrixDataVo);
                allDataList.addAll(dataList);
            }
            config.put("dataList", allDataList);
        }
        matrixVo.setConfig(config);
        return matrixVo;
    }

    @Override
    protected void myImportMatrix(MatrixVo matrixVo) {
        JSONObject config = matrixVo.getConfig();
        if (MapUtils.isEmpty(config)) {
            return;
        }
        JSONArray attributeArray = config.getJSONArray("attributeList");
        if (CollectionUtils.isEmpty(attributeArray)) {
            return;
        }
        String matrixUuid = matrixVo.getUuid();
        List<MatrixAttributeVo> attributeList = attributeArray.toJavaList(MatrixAttributeVo.class);
        List<MatrixAttributeVo> oldMatrixAttributeList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixUuid);
        if (CollectionUtils.isNotEmpty(oldMatrixAttributeList)) {
            // 删除旧字段配置
            attributeMapper.deleteAttributeByMatrixUuid(matrixUuid);
            // 删除动态表及数据
            attributeMapper.dropMatrixDynamicTable(matrixUuid);
        }
        for (MatrixAttributeVo attributeVo : attributeList) {
            attributeVo.setMatrixUuid(matrixUuid);
            attributeMapper.insertMatrixAttribute(attributeVo);
        }
        attributeMapper.createMatrixDynamicTable(attributeList, matrixUuid);

        JSONArray dataArray = config.getJSONArray("dataList");
        if (CollectionUtils.isNotEmpty(dataArray)) {
            for (int i = 0; i < dataArray.size(); i++) {
                Map<String, Object> map = dataArray.getObject(i, Map.class);
                if (MapUtils.isNotEmpty(map)) {
                    List<MatrixColumnVo> columnlist = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String value = entry.getValue() == null ? null : entry.getValue().toString();
                        columnlist.add(new MatrixColumnVo(entry.getKey(), value));
                    }
                    matrixDataMapper.insertDynamicTableData(columnlist, matrixUuid);
                }
            }
        }
    }

    @Override
    protected void mySaveAttributeList(String matrixUuid, List<MatrixAttributeVo> attributeVoList) {
//        List<MatrixAttributeVo> attributeVoList = jsonObj.getJSONArray("matrixAttributeList").toJavaList(MatrixAttributeVo.class);
        List<MatrixAttributeVo> oldMatrixAttributeList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixUuid);
        boolean dataExist = CollectionUtils.isNotEmpty(oldMatrixAttributeList);
        if (dataExist) {
            attributeMapper.deleteAttributeByMatrixUuid(matrixUuid);
        }
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
                    attributeMapper.addMatrixDynamicTableColumn(attributeUuid, matrixUuid);
                }
                //找出需要删除的属性uuid列表
                oldAttributeUuidList.removeAll(existedAttributeUuidList);
                for (String attributeUuid : oldAttributeUuidList) {
                    attributeMapper.dropMatrixDynamicTableColumn(attributeUuid, matrixUuid);
                }
            } else {
                for (MatrixAttributeVo attributeVo : attributeVoList) {
                    attributeVo.setMatrixUuid(matrixUuid);
                    attributeVo.setUuid(UuidUtil.randomUuid());
                    attributeMapper.insertMatrixAttribute(attributeVo);
                }
                attributeMapper.createMatrixDynamicTable(attributeVoList, matrixUuid);
            }
        } else {
            //无数据
            if (dataExist) {
                // 删除动态表
                attributeMapper.dropMatrixDynamicTable(matrixUuid);
            }
        }
    }

    @Override
    protected List<MatrixAttributeVo> myGetAttributeList(MatrixVo matrixVo) {
        List<MatrixAttributeVo> matrixAttributeList = attributeMapper.getMatrixAttributeByMatrixUuid(matrixVo.getUuid());
        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
            List<String> attributeUuidList = matrixAttributeList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
            Map<String, Long> attributeDataCountMap = matrixDataMapper.checkMatrixAttributeHasDataByAttributeUuidList(matrixVo.getUuid(), attributeUuidList);
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
    protected JSONObject myTableDataSearch(MatrixDataVo dataVo) {
        JSONObject returnObj = new JSONObject();
        List<MatrixAttributeVo> matrixAttributeList = attributeMapper.getMatrixAttributeByMatrixUuid(dataVo.getMatrixUuid());
        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
            JSONArray defaultValue = dataVo.getDefaultValue();
            List<Map<String, String>> dataMapList = null;
            if (CollectionUtils.isNotEmpty(defaultValue)) {
                dataMapList = matrixDataMapper.getDynamicTableDataByUuidList(dataVo);
            } else {
                if (!mergeFilterListAndSourceColumnList(dataVo)) {
                    return returnObj;
                }
                int rowNum = matrixDataMapper.getDynamicTableDataCountForTable(dataVo);
                if (rowNum > 0) {
                    dataVo.setRowNum(rowNum);
                    dataMapList = matrixDataMapper.getDynamicTableDataForTable(dataVo);
                }
            }
            List<Map<String, JSONObject>> tbodyList = matrixTableDataValueHandle(matrixAttributeList, dataMapList);
            JSONArray theadList = getTheadList(dataVo.getMatrixUuid(), matrixAttributeList, dataVo.getColumnList());
            returnObj = TableResultUtil.getResult(theadList, tbodyList, dataVo);
        }
        return returnObj;
    }

    @Override
    protected List<Map<String, JSONObject>> mySearchTableDataNew(MatrixDataVo dataVo) {
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        List<MatrixAttributeVo> attributeList = attributeMapper.getMatrixAttributeByMatrixUuid(dataVo.getMatrixUuid());
        if (CollectionUtils.isEmpty(attributeList)) {
            return resultList;
        }
        Map<String, MatrixAttributeVo> matrixAttributeMap = attributeList.stream().collect(Collectors.toMap(e -> e.getUuid(), e -> e));
        List<Map<String, String>> dataMapList = new ArrayList<>();
        JSONArray defaultValue = dataVo.getDefaultValue();
        if (CollectionUtils.isNotEmpty(defaultValue)) {
            dataMapList = matrixDataMapper.getDynamicTableDataByUuidList(dataVo);
        } else if (CollectionUtils.isNotEmpty(dataVo.getDefaultValueFilterList())) {
            for (MatrixDefaultValueFilterVo defaultValueFilterVo : dataVo.getDefaultValueFilterList()) {
                List<MatrixFilterVo> filterList = new ArrayList<>();
                MatrixKeywordFilterVo valueFieldFilter = defaultValueFilterVo.getValueFieldFilter();
                if (valueFieldFilter != null) {
                    filterList.add(new MatrixFilterVo(valueFieldFilter.getUuid(), valueFieldFilter.getExpression(), Arrays.asList(valueFieldFilter.getValue())));
                }
                MatrixKeywordFilterVo textFieldFilter = defaultValueFilterVo.getTextFieldFilter();
                if (textFieldFilter != null) {
                    MatrixAttributeVo attributeVo = matrixAttributeMap.get(textFieldFilter.getUuid());
                    if (MatrixAttributeType.SELECT.getValue().equals(attributeVo.getType())) {
                        List<String> valueList = getSelectTypeValueList(attributeVo, textFieldFilter.getValue(), SearchExpression.EQ);
                        if (CollectionUtils.isNotEmpty(valueList)) {
                            filterList.add(new MatrixFilterVo(textFieldFilter.getUuid(), SearchExpression.EQ.getExpression(), valueList));
                        } else {
                            continue;
                        }
                        dataVo.setKeyword(null);
                    } else {
                        dataVo.setKeyword(textFieldFilter.getValue());
                        dataVo.setKeywordColumn(textFieldFilter.getUuid());
                        dataVo.setAttrType(attributeVo.getType());
                        dataVo.setKeywordExpression(SearchExpression.EQ.getExpression());
                    }
                }
                dataVo.setFilterList(filterList);
                List<Map<String, String>> list = matrixDataMapper.getDynamicTableDataList(dataVo);
                if (CollectionUtils.isNotEmpty(list)) {
                    dataMapList.addAll(list);
                }
            }
        } else {
            String keyword = dataVo.getKeyword();
            String keywordColumn = dataVo.getKeywordColumn();
            MatrixAttributeVo keywordAttributeVo = matrixAttributeMap.get(keywordColumn);
            if (StringUtils.isNotBlank(keywordColumn) && StringUtils.isNotBlank(keyword)) {
                if (MatrixAttributeType.SELECT.getValue().equals(keywordAttributeVo.getType())) {
                    List<String> valueList = getSelectTypeValueList(keywordAttributeVo, keyword, SearchExpression.LI);
                    if (CollectionUtils.isNotEmpty(valueList)) {
                        List<MatrixFilterVo> filterList = dataVo.getFilterList();
                        if (filterList == null) {
                            filterList = new ArrayList<>();
                        }
                        filterList.add(new MatrixFilterVo(keywordColumn, SearchExpression.LI.getExpression(), valueList));
                        dataVo.setFilterList(filterList);
                    } else {
                        return resultList;
                    }
                    dataVo.setKeyword(null);
                } else {
                    dataVo.setAttrType(keywordAttributeVo.getType());
                    dataVo.setKeywordExpression(SearchExpression.LI.getExpression());
                }
            }
            // 遍历过滤条件列表dataVo.getFilterList()，补充type字段值，即条件类型
            List<MatrixFilterVo> filterList = dataVo.getFilterList();
            if (CollectionUtils.isNotEmpty(filterList)) {
                for (MatrixFilterVo filterVo : filterList) {
                    MatrixAttributeVo matrixAttributeVo = matrixAttributeMap.get(filterVo.getUuid());
                    if (matrixAttributeVo != null) {
                        if (Objects.equals(MatrixAttributeType.USER.getValue(), matrixAttributeVo.getType())
                                || Objects.equals(MatrixAttributeType.TEAM.getValue(), matrixAttributeVo.getType())
                                || Objects.equals(MatrixAttributeType.ROLE.getValue(), matrixAttributeVo.getType())) {
                            filterVo.setType(matrixAttributeVo.getType());
                            if (CollectionUtils.isNotEmpty(filterVo.getValueList())) {
                                List<String> valueList = new ArrayList<>();
                                for (String value : filterVo.getValueList()) {
                                    valueList.add(GroupSearch.removePrefix(value));
                                }
                                filterVo.setValueList(valueList);
                            }
                        } else {
//                            if (StringUtils.isBlank(filterVo.getType())) {
                                filterVo.setType(matrixAttributeVo.getType());
//                            }
                        }
                    }
                }
            }
            //下面逻辑适用于下拉框滚动加载，也可以搜索，但是一页返回的数据量可能会小于pageSize，因为做了去重处理
            int rowNum = matrixDataMapper.getDynamicTableDataListCount(dataVo);
            if (rowNum == 0) {
                return resultList;
            }
            dataVo.setRowNum(rowNum);
            if (dataVo.getCurrentPage() > dataVo.getPageCount()) {
                return resultList;
            }
            dataMapList = matrixDataMapper.getDynamicTableDataList(dataVo);
        }
        if (CollectionUtils.isEmpty(dataMapList)) {
            return resultList;
        }
        List<Map<String, String>> distinctList = new ArrayList<>();
        for (Map<String, String> dataMap : dataMapList) {
            if(distinctList.contains(dataMap)){
                continue;
            }
            distinctList.add(dataMap);
        }
        resultList = matrixTableDataValueHandle(attributeList, distinctList);
        return resultList;
    }

    /**
     * 自定义矩阵下拉框类型数据，根据text转换成value
     * @param matrixAttribute
     * @param keyword
     * @param expression
     * @return
     */
    private List<String> getSelectTypeValueList(MatrixAttributeVo matrixAttribute, String keyword, SearchExpression expression) {
        List<String> valueList = new ArrayList<>();
        JSONObject config = matrixAttribute.getConfig();
        if (MapUtils.isEmpty(config)) {
            return valueList;
        }
        JSONArray dataArray = config.getJSONArray("dataList");
        if (CollectionUtils.isEmpty(dataArray)) {
            return valueList;
        }
        List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
        for (ValueTextVo e : dataList) {
            String text = e.getText();
            if (StringUtils.isNotBlank(text)) {
                if (SearchExpression.LI == expression) {
                    if (text.contains(keyword)) {
                        valueList.add((String)e.getValue());
                    }
                } else if (SearchExpression.EQ == expression) {
                    if (Objects.equals(text, keyword)) {
                        valueList.add((String)e.getValue());
                    }
                }
            }
        }
        return valueList;
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
        String uuidValue = rowDataObj.getString("uuid");
        if (uuidValue == null) {
            if (hasData) {
                rowData.add(new MatrixColumnVo("uuid", UuidUtil.randomUuid()));
                Integer maxSort = matrixDataMapper.getMaxSort(matrixUuid);
                maxSort = maxSort == null ? 1 : maxSort + 1;
                rowData.add(new MatrixColumnVo("sort", maxSort.toString()));
                matrixDataMapper.insertDynamicTableData(rowData, matrixUuid);
            }
        } else {
            if (hasData) {
                matrixDataMapper.updateDynamicTableDataByUuid(rowData, uuidValue, matrixUuid);
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
    protected void myMoveTableRowDataSort(String matrixUuid, String uuid, String toUuid) {
        Integer oldSort = matrixDataMapper.getSortByUuid(uuid, matrixUuid);
        Integer newSort = matrixDataMapper.getSortByUuid(toUuid, matrixUuid);
        if (oldSort == null) {
            return;
        }
        if (Objects.equals(newSort, oldSort)) {
            return;
        }
        matrixDataMapper.updateSortByUuid(uuid, matrixUuid, 0);
        if (newSort > oldSort) {
            matrixDataMapper.updateSortDecrement(matrixUuid, oldSort, newSort);
        } else {
            matrixDataMapper.updateSortIncrement(matrixUuid, newSort, oldSort);
        }
        matrixDataMapper.updateSortByUuid(uuid, matrixUuid, newSort);
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
     *
     * @param attributeVo
     * @param selectValueList
     */
    private void decodeDataConfig(MatrixAttributeVo attributeVo, List<String> selectValueList) {
            JSONObject config = attributeVo.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            JSONArray dataList = config.getJSONArray("dataList");
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

    /**
     * @param matrixAttributeVo
     * @param value
     * @return boolean
     * @Time:2020年12月1日
     * @Description: 矩阵属性值合法性校验
     */
    private boolean matrixAttributeValueVerify(MatrixAttributeVo matrixAttributeVo, String value) {
        String type = matrixAttributeVo.getType();
        if (type.equals(MatrixAttributeType.INPUT.getValue())) {
            return true;
        } else if (type.equals(MatrixAttributeType.SELECT.getValue())) {
            JSONObject config = matrixAttributeVo.getConfig();
            if (MapUtils.isNotEmpty(config)) {
                JSONArray dataList = config.getJSONArray("dataList");
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject dataObj = dataList.getJSONObject(i);
                    if (value.equals(dataObj.getString("value"))) {
                        return true;
                    }
                }
            }
        } else if (type.equals(MatrixAttributeType.DATE.getValue())) {
            SimpleDateFormat sdf = new SimpleDateFormat(TimeUtil.YYYY_MM_DD_HH_MM_SS);
            try {
                sdf.parse(value);
            } catch (ParseException e) {
                return false;
            }
            return true;
        } else if (type.equals(MatrixAttributeType.USER.getValue())) {
            return userMapper.checkUserIsExists(value) > 0;
        } else if (type.equals(MatrixAttributeType.TEAM.getValue())) {
            return teamMapper.checkTeamIsExists(value) > 0;
        } else if (type.equals(MatrixAttributeType.ROLE.getValue())) {
            return roleMapper.checkRoleIsExists(value) > 0;
        }
        return false;
    }

    private List<Map<String, JSONObject>> matrixTableDataValueHandle(List<MatrixAttributeVo> matrixAttributeList, List<Map<String, String>> valueList) {
        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
            Map<String, MatrixAttributeVo> matrixAttributeMap = new HashMap<>();
            for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
                matrixAttributeMap.put(matrixAttributeVo.getUuid(), matrixAttributeVo);
            }
            if (CollectionUtils.isNotEmpty(valueList)) {
                List<Map<String, JSONObject>> resultList = new ArrayList<>(valueList.size());
                for (Map<String, String> valueMap : valueList) {
                    Map<String, JSONObject> resultMap = new HashMap<>();
                    for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                        String attributeUuid = entry.getKey();
                        resultMap.put(attributeUuid, matrixAttributeValueHandle(matrixAttributeMap.get(attributeUuid), entry.getValue()));
                    }
                    resultList.add(resultMap);
                }
                return resultList;
            }
        }
        return null;
    }

    private JSONObject matrixAttributeValueHandle(MatrixAttributeVo matrixAttribute, Object valueObj) {
        JSONObject resultObj = new JSONObject();
        String type = MatrixAttributeType.INPUT.getValue();
        if (matrixAttribute != null) {
            type = matrixAttribute.getType();
        }
        resultObj.put("type", type);
        if (valueObj == null) {
            resultObj.put("value", null);
            resultObj.put("text", null);
            return resultObj;
        }
        String value = valueObj.toString();
        resultObj.put("value", value);
        resultObj.put("text", value);
        if (MatrixAttributeType.SELECT.getValue().equals(type)) {
            if (matrixAttribute != null) {
                JSONObject config = matrixAttribute.getConfig();
                if (MapUtils.isNotEmpty(config)) {
                    JSONArray dataList = config.getJSONArray("dataList");
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        for (int i = 0; i < dataList.size(); i++) {
                            JSONObject data = dataList.getJSONObject(i);
                            if (Objects.equals(value, data.getString("value"))) {
                                resultObj.put("text", data.getString("text"));
                            }
                        }
                    }
                }
            }
        } else if (MatrixAttributeType.USER.getValue().equals(type)) {
            UserVo userVo = userMapper.getUserBaseInfoByUuid(value);
            if (userVo != null) {
                resultObj.put("text", userVo.getUserName());
                resultObj.put("avatar", userVo.getAvatar());
                resultObj.put("pinyin", userVo.getPinyin());
                resultObj.put("vipLevel", userVo.getVipLevel());
            }
        } else if (MatrixAttributeType.TEAM.getValue().equals(type)) {
            TeamVo teamVo = teamMapper.getTeamByUuid(value);
            if (teamVo != null) {
                resultObj.put("text", teamVo.getName());
            }
        } else if (MatrixAttributeType.ROLE.getValue().equals(type)) {
            RoleVo roleVo = roleMapper.getRoleByUuid(value);
            if (roleVo != null) {
                resultObj.put("text", roleVo.getName());
            }
        }
        return resultObj;
    }
}
