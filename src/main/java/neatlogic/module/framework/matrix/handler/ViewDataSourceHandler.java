/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.matrix.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.dao.mapper.SchemaMapper;
import neatlogic.framework.exception.database.DataBaseNotFoundException;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.exception.file.FileTypeHandlerNotFoundException;
import neatlogic.framework.exception.type.ParamNotExistsException;
import neatlogic.framework.file.core.FileTypeHandlerFactory;
import neatlogic.framework.file.core.IFileTypeHandler;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.constvalue.MatrixType;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerBase;
import neatlogic.framework.matrix.dao.mapper.MatrixViewDataMapper;
import neatlogic.framework.matrix.dto.*;
import neatlogic.framework.matrix.exception.*;
import neatlogic.framework.matrix.view.MatrixViewSqlBuilder;
import neatlogic.framework.transaction.core.EscapeTransactionJob;
import neatlogic.framework.util.TableResultUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linbq
 * @since 2021/11/4 19:34
 **/
@Component
public class ViewDataSourceHandler extends MatrixDataSourceHandlerBase {

    @Resource
    private FileMapper fileMapper;

    @Resource
    private MatrixViewDataMapper matrixViewDataMapper;

    @Resource
    private SchemaMapper schemaMapper;

    @Override
    public String getHandler() {
        return MatrixType.VIEW.getValue();
    }

    @Override
    protected boolean mySaveMatrix(MatrixVo matrixVo) throws Exception {
        Long fileId = matrixVo.getFileId();
        if (fileId == null) {
            throw new ParamNotExistsException("fileId");
        }
        FileVo fileVo = fileMapper.getFileById(fileId);
        if (fileVo == null) {
            throw new FileNotFoundException(fileId);
        }
        String xml = IOUtils.toString(FileUtil.getData(fileVo.getPath()), StandardCharsets.UTF_8);
//            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ci><attrs><attr name=\"uuid\" label=\"用户uuid\"/><attr name=\"user_id\" label=\"用户id\"/><attr name=\"user_name\" label=\"用户名\"/><attr name=\"teamName\" label=\"分组\"/><attr name=\"vipLevel\" label=\"是否VIP\"/><attr name=\"phone\" label=\"电话\"/><attr name=\"email\" label=\"邮件\"/></attrs><sql>SELECT `u`.`uuid` AS uuid, `u`.`id` AS id, `u`.`user_id` as user_id, `u`.`user_name` as user_name, u.email as email, u.phone as phone, if(u.vip_level=0,'否','是') as vipLevel, group_concat( `t`.`name`) AS teamName FROM `user` `u` LEFT JOIN `user_team` `ut` ON `u`.`uuid` = `ut`.`user_uuid` LEFT JOIN `team` `t` ON `t`.`uuid` = `ut`.`team_uuid` GROUP BY u.uuid </sql></ci>";
        if (StringUtils.isBlank(xml)) {
            throw new MatrixViewSettingFileNotFoundException();
        }
        MatrixViewVo oldMatrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
        if (oldMatrixViewVo != null) {
            if (xml.equals(oldMatrixViewVo.getXml())) {
                return false;
            }
        }
        List<MatrixAttributeVo> matrixAttributeVoList = buildView(matrixVo.getUuid(), matrixVo.getName(), xml);
        MatrixViewVo matrixViewVo = new MatrixViewVo();
        matrixViewVo.setMatrixUuid(matrixVo.getUuid());
        matrixViewVo.setFileName(fileVo.getName());
        matrixViewVo.setXml(xml);
        JSONObject config = new JSONObject();
        config.put("attributeList", matrixAttributeVoList);
        matrixViewVo.setConfig(config.toJSONString());
        matrixMapper.insertMatrixView(matrixViewVo);
        IFileTypeHandler fileTypeHandler = FileTypeHandlerFactory.getHandler(fileVo.getType());
        if (fileTypeHandler == null) {
            throw new FileTypeHandlerNotFoundException(fileVo.getType());
        }
        fileTypeHandler.deleteFile(fileVo, null);
        return true;
    }

    @Override
    protected void myGetMatrix(MatrixVo matrixVo) {
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(matrixVo.getName());
        }
        matrixVo.setFileName(matrixViewVo.getFileName());
    }

    @Override
    protected void myDeleteMatrix(String uuid) {
        matrixMapper.deleteMatrixViewByMatrixUuid(uuid);
        schemaMapper.deleteView(TenantContext.get().getDataDbName() + ".matrix_" + uuid);
    }

    @Override
    protected void myCopyMatrix(String sourceUuid, MatrixVo matrixVo) {

    }

    @Override
    protected JSONObject myImportMatrix(MatrixVo matrixVo, MultipartFile multipartFile) throws IOException {
        return null;
    }

    @Override
    protected void myExportMatrix2CSV(MatrixVo matrixVo, OutputStream os) throws IOException {
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(matrixVo.getUuid());
        }
        JSONArray attributeList = (JSONArray) JSONPath.read(matrixViewVo.getConfig(), "attributeList");
        if (CollectionUtils.isNotEmpty(attributeList)) {
            List<MatrixAttributeVo> attributeVoList = attributeList.toJavaList(MatrixAttributeVo.class);
            StringBuilder header = new StringBuilder();
            List<String> headList = new ArrayList<>();
            JSONArray theadList = getTheadList(attributeVoList);
            for (int i = 0; i < theadList.size(); i++) {
                JSONObject obj = theadList.getJSONObject(i);
                String title = obj.getString("title");
                String key = obj.getString("key");
                if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(key)) {
                    header.append(title).append(",");
                    headList.add(key);
                }
            }
            header.append("\n");
            os.write(header.toString().getBytes("GBK"));
            os.flush();
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixVo.getUuid());
            dataVo.setColumnList(attributeVoList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList()));
            int rowNum = matrixViewDataMapper.getDynamicTableDataCount(dataVo);
            if (rowNum > 0) {
                dataVo.setRowNum(rowNum);
                int currentPage = 1;
                dataVo.setPageSize(1000);
                Integer pageCount = dataVo.getPageCount();
                List<Map<String, String>> dataList;
                while (currentPage <= pageCount) {
                    dataVo.setCurrentPage(currentPage);
                    dataList = matrixViewDataMapper.searchDynamicTableData(dataVo);
                    if (!dataList.isEmpty()) {
                        StringBuilder content = new StringBuilder();
                        for (Map<String, String> map : dataList) {
                            for (String head : headList) {
                                Object value = map.get(head);
                                content.append(value != null ? value.toString().replaceAll("\n", "").replaceAll(",", "，") : StringUtils.EMPTY).append(",");
                            }
                            content.append("\n");
                        }
                        os.write(content.toString().getBytes("GBK"));
                        os.flush();
                    }
                    dataList.clear();
                    currentPage++;
                }
            }
        }
    }

    @Override
    protected MatrixVo myExportMatrix(MatrixVo matrixVo) {
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(matrixVo.getName());
        }
        JSONObject config = new JSONObject();
        config.put("config", matrixViewVo.getConfig());
        config.put("xml", matrixViewVo.getXml());
        matrixVo.setConfig(config);
        matrixVo.setFileName(matrixViewVo.getFileName());
        return matrixVo;
    }

    @Override
    protected void myImportMatrix(MatrixVo matrixVo) {
        myDeleteMatrix(matrixVo.getUuid());
        JSONObject config = matrixVo.getConfig();
        String xml = config.getString("xml");
        buildView(matrixVo.getUuid(), matrixVo.getName(), xml);
        MatrixViewVo matrixViewVo = new MatrixViewVo();
        matrixViewVo.setMatrixUuid(matrixVo.getUuid());
        matrixViewVo.setFileName(matrixVo.getFileName());
        matrixViewVo.setXml(xml);
        matrixViewVo.setConfig(config.getString("config"));
        matrixMapper.insertMatrixView(matrixViewVo);
    }

    @Override
    protected void mySaveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList) {

    }

    @Override
    protected List<MatrixAttributeVo> myGetAttributeList(MatrixVo matrixVo) {
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(matrixVo.getName());
        }
        JSONArray attributeList = (JSONArray) JSONPath.read(matrixViewVo.getConfig(), "attributeList");
        return attributeList.toJavaList(MatrixAttributeVo.class);
    }

    @Override
    protected JSONObject myExportAttribute(MatrixVo matrixVo) {
        return null;
    }

    @Override
    protected JSONObject myGetTableData(MatrixDataVo dataVo) {
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(dataVo.getMatrixUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(dataVo.getMatrixUuid());
        }
        JSONArray attributeList = (JSONArray) JSONPath.read(matrixViewVo.getConfig(), "attributeList");
        if (CollectionUtils.isNotEmpty(attributeList)) {
            List<MatrixAttributeVo> attributeVoList = attributeList.toJavaList(MatrixAttributeVo.class);
            List<String> columnList = attributeVoList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
            dataVo.setColumnList(columnList);
            if (dataVo.getNeedPage()) {
                int rowNum = matrixViewDataMapper.getDynamicTableDataCount(dataVo);
                dataVo.setRowNum(rowNum);
            }
            List<Map<String, String>> dataList = matrixViewDataMapper.searchDynamicTableData(dataVo);
            List<Map<String, JSONObject>> tbodyList = matrixTableDataValueHandle(dataList);
            JSONArray theadList = getTheadList(attributeVoList);
            return TableResultUtil.getResult(theadList, tbodyList, dataVo);
        }
        return new JSONObject();
    }

    @Override
    protected JSONObject myTableDataSearch(MatrixDataVo dataVo) {
        JSONObject returnObj = new JSONObject();
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(dataVo.getMatrixUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(dataVo.getUuid());
        }
        JSONArray attributeList = (JSONArray) JSONPath.read(matrixViewVo.getConfig(), "attributeList");
        if (CollectionUtils.isNotEmpty(attributeList)) {
            List<Map<String, String>> dataMapList = null;
            JSONArray defaultValue = dataVo.getDefaultValue();
            if (CollectionUtils.isNotEmpty(defaultValue)) {
                dataMapList = matrixViewDataMapper.getDynamicTableDataByUuidList(dataVo);
            } else {
                if (!mergeFilterListAndSourceColumnList(dataVo)) {
                    return returnObj;
                }
                int rowNum = matrixViewDataMapper.getDynamicTableDataCountForTable(dataVo);
                if (rowNum > 0) {
                    dataVo.setRowNum(rowNum);
                    dataMapList = matrixViewDataMapper.getDynamicTableDataForTable(dataVo);
                }
            }
            List<MatrixAttributeVo> matrixAttributeList = attributeList.toJavaList(MatrixAttributeVo.class);
            List<Map<String, JSONObject>> tbodyList = matrixTableDataValueHandle(dataMapList);
            JSONArray theadList = getTheadList(dataVo.getMatrixUuid(), matrixAttributeList, dataVo.getColumnList());
            returnObj = TableResultUtil.getResult(theadList, tbodyList, dataVo);
//            returnObj.put("searchColumnDetailList", getSearchColumnDetailList(dataVo.getMatrixUuid(), matrixAttributeList, searchColumnArray));
        }
        return returnObj;
    }

    @Override
    protected List<Map<String, JSONObject>> myTableColumnDataSearch(MatrixDataVo dataVo) {
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(dataVo.getMatrixUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(dataVo.getMatrixUuid());
        }
        JSONArray attributeArray = (JSONArray) JSONPath.read(matrixViewVo.getConfig(), "attributeList");
        if (CollectionUtils.isNotEmpty(attributeArray)) {
            List<MatrixAttributeVo> attributeList = attributeArray.toJavaList(MatrixAttributeVo.class);
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
                        if (columnList.size() >= 2) {
                            String keywordColumn = columnList.get(1);
                            MatrixAttributeVo matrixAttribute = matrixAttributeMap.get(keywordColumn);
                            if (matrixAttribute == null) {
                                throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), keywordColumn);
                            }
                            MatrixColumnVo matrixColumnVo = new MatrixColumnVo(keywordColumn, split[1]);
                            matrixColumnVo.setExpression(Expression.EQUAL.getExpression());
                            sourceColumnList.add(matrixColumnVo);
                        }
                        dataVo.setSourceColumnList(sourceColumnList);
                        List<Map<String, String>> dataMapList = matrixViewDataMapper.getDynamicTableDataForSelect(dataVo);
                        resultList.addAll(matrixTableDataValueHandle(dataMapList));
                    }
                }
            } else {
                if (!mergeFilterListAndSourceColumnList(dataVo)) {
                    return resultList;
                }
                String keywordColumn = dataVo.getKeywordColumn();
                String keyword = dataVo.getKeyword();
                if (StringUtils.isNotBlank(keywordColumn) && StringUtils.isNotBlank(keyword)) {
                    MatrixAttributeVo matrixAttribute = matrixAttributeMap.get(keywordColumn);
                    if (matrixAttribute == null) {
                        throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), keywordColumn);
                    }
                    List<MatrixColumnVo> sourceColumnList = new ArrayList<>();
                    MatrixColumnVo matrixColumnVo = new MatrixColumnVo(keywordColumn, keyword);
                    matrixColumnVo.setExpression(Expression.LIKE.getExpression());
                    sourceColumnList.add(matrixColumnVo);
                    dataVo.setSourceColumnList(sourceColumnList);
                }
                //下面逻辑适用于下拉框滚动加载，也可以搜索，但是一页返回的数据量可能会小于pageSize，因为做了去重处理
                int rowNum = matrixViewDataMapper.getDynamicTableDataCountForSelect(dataVo);
                if (rowNum == 0) {
                    return resultList;
                }
                dataVo.setRowNum(rowNum);
                if (dataVo.getCurrentPage() > dataVo.getPageCount()) {
                    return resultList;
                }
                List<Map<String, String>> dataMapList = matrixViewDataMapper.getDynamicTableDataForSelect(dataVo);
                if (CollectionUtils.isEmpty(dataMapList)) {
                    return resultList;
                }
                List<Map<String, String>> distinctDataMapList = new ArrayList<>();
                List<Map<String, String>> distinctList = new ArrayList<>(100);
                for (Map<String, String> dataMap : dataMapList) {
                    if(distinctList.contains(dataMap)){
                        continue;
                    }
                    distinctDataMapList.add(dataMap);
                    distinctList.add(dataMap);
                    if (distinctList.size() >= dataVo.getPageSize()) {
                        break;
                    }
                }
                resultList.addAll(matrixTableDataValueHandle(distinctDataMapList));
            }
        }
        return resultList;
    }

    @Override
    protected List<Map<String, JSONObject>> mySearchTableDataNew(MatrixDataVo dataVo) {
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(dataVo.getMatrixUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(dataVo.getUuid());
        }
        JSONArray attributeArray = (JSONArray) JSONPath.read(matrixViewVo.getConfig(), "attributeList");
        if (CollectionUtils.isEmpty(attributeArray)) {
            return resultList;
        }
        List<MatrixAttributeVo> attributeList = attributeArray.toJavaList(MatrixAttributeVo.class);

        Map<String, MatrixAttributeVo> matrixAttributeMap = attributeList.stream().collect(Collectors.toMap(e -> e.getUuid(), e -> e));
        List<Map<String, String>> dataMapList = new ArrayList<>();
        JSONArray defaultValue = dataVo.getDefaultValue();
        if (CollectionUtils.isNotEmpty(defaultValue)) {
            dataMapList = matrixViewDataMapper.getDynamicTableDataByUuidList(dataVo);
        } else if (CollectionUtils.isNotEmpty(dataVo.getDefaultValueFilterList())) {
            for (MatrixDefaultValueFilterVo defaultValueFilterVo : dataVo.getDefaultValueFilterList()) {
                List<MatrixFilterVo> filterList = new ArrayList<>();
                MatrixKeywordFilterVo valueFieldFilter = defaultValueFilterVo.getValueFieldFilter();
                filterList.add(new MatrixFilterVo(valueFieldFilter.getUuid(), valueFieldFilter.getExpression(), Arrays.asList(valueFieldFilter.getValue())));
                MatrixKeywordFilterVo textFieldFilter = defaultValueFilterVo.getTextFieldFilter();
                filterList.add(new MatrixFilterVo(textFieldFilter.getUuid(), textFieldFilter.getExpression(), Arrays.asList(textFieldFilter.getValue())));
                dataVo.setFilterList(filterList);
                List<Map<String, String>> list = matrixViewDataMapper.getDynamicTableDataList(dataVo);
                if (CollectionUtils.isNotEmpty(list)) {
                    dataMapList.addAll(list);
                }
            }
        } else {
            String keyword = dataVo.getKeyword();
            String keywordColumn = dataVo.getKeywordColumn();
            if (StringUtils.isNotBlank(keywordColumn) && StringUtils.isNotBlank(keyword)) {
                MatrixAttributeVo matrixAttribute = matrixAttributeMap.get(keywordColumn);
                if (matrixAttribute == null) {
                    throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), keywordColumn);
                }
                List<MatrixFilterVo> filterList = new ArrayList<>();
                filterList.add(new MatrixFilterVo(keywordColumn, Expression.LIKE.getExpression(), Arrays.asList(keyword)));
                dataVo.setFilterList(filterList);
            }
            //下面逻辑适用于下拉框滚动加载，也可以搜索，但是一页返回的数据量可能会小于pageSize，因为做了去重处理
            int rowNum = matrixViewDataMapper.getDynamicTableDataListCount(dataVo);
            if (rowNum == 0) {
                return resultList;
            }
            dataVo.setRowNum(rowNum);
            if (dataVo.getCurrentPage() > dataVo.getPageCount()) {
                return resultList;
            }
            dataMapList = matrixViewDataMapper.getDynamicTableDataList(dataVo);
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
        resultList = matrixTableDataValueHandle(distinctList);
        return resultList;
    }
    @Override
    protected JSONObject mySaveTableRowData(String matrixUuid, JSONObject rowData) {
        return null;
    }

    @Override
    protected Map<String, String> myGetTableRowData(MatrixDataVo matrixDataVo) {
        return null;
    }

    @Override
    protected void myDeleteTableRowData(String matrixUuid, List<String> uuidList) {

    }

    public List<MatrixAttributeVo> buildView(String matrixUuid, String matrixName, String xml) {
        List<MatrixAttributeVo> matrixAttributeList = new ArrayList<>();
        MatrixViewSqlBuilder viewBuilder = new MatrixViewSqlBuilder(xml);
//        viewBuilder.setCiId(ciVo.getId());
        viewBuilder.setViewName("matrix_" + matrixUuid);
        if (viewBuilder.valid()) {
            //测试一下语句是否能正常执行
            try {
                schemaMapper.testCiViewSql(viewBuilder.getTestSql());
            } catch (Exception ex) {
                throw new MatrixViewSqlIrregularException(ex);
            }
            List<MatrixViewAttributeVo> attrList = viewBuilder.getAttrList();
            if (CollectionUtils.isNotEmpty(attrList)) {
                int sort = 0;
                for (MatrixViewAttributeVo attrVo : attrList) {
                    MatrixAttributeVo matrixAttributeVo = new MatrixAttributeVo();
                    matrixAttributeVo.setMatrixUuid(matrixUuid);
                    String name = attrVo.getName();
                    matrixAttributeVo.setUuid(name);
                    if ("uuid".equals(name)) {
                        matrixAttributeVo.setPrimaryKey(1);
                    }
                    matrixAttributeVo.setName(attrVo.getLabel());
                    matrixAttributeVo.setType(MatrixAttributeType.INPUT.getValue());
                    matrixAttributeVo.setIsDeletable(0);
                    matrixAttributeVo.setSort(sort++);
                    matrixAttributeVo.setIsRequired(0);
                    matrixAttributeList.add(matrixAttributeVo);
                }
                EscapeTransactionJob.State s = new EscapeTransactionJob(() -> {
                    if (schemaMapper.checkSchemaIsExists(TenantContext.get().getDataDbName()) > 0) {
                        //创建配置项表
//                        String viewSql = viewBuilder.getCreateViewSql();
//                        System.out.println(viewSql);
                        schemaMapper.insertView(viewBuilder.getCreateViewSql());
                    } else {
                        throw new DataBaseNotFoundException();
                    }
                }).execute();
                if (!s.isSucceed()) {
                    throw new MatrixViewCreateSchemaException(matrixName);
                }
            }
        }
        return matrixAttributeList;
    }

    public List<Map<String, JSONObject>> matrixTableDataValueHandle(List<Map<String, String>> valueList) {
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(valueList)) {
            //对valueList去重
            List<Map<String, String>> distinctList = new ArrayList<>();
            for (Map<String, String> valueMap : valueList) {
                if(distinctList.contains(valueMap)){
                    continue;
                }
                distinctList.add(valueMap);
                Map<String, JSONObject> resultMap = new HashMap<>();
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    JSONObject resultObj = new JSONObject();
                    resultObj.put("type", MatrixAttributeType.INPUT.getValue());
                    resultObj.put("value", entry.getValue());
                    resultObj.put("text", entry.getValue());
                    resultMap.put(entry.getKey(), resultObj);
                }
                resultList.add(resultMap);
            }
        }
        return resultList;
    }
}
