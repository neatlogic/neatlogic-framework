/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
import com.alibaba.fastjson.JSONPath;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.dao.mapper.DataBaseViewInfoMapper;
import neatlogic.framework.dao.mapper.SchemaMapper;
import neatlogic.framework.dto.DataBaseViewInfoVo;
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
import neatlogic.framework.util.Md5Util;
import neatlogic.framework.util.TableResultUtil;
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

    @Resource
    private DataBaseViewInfoMapper dataBaseViewInfoMapper;

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
        List<MatrixAttributeVo> matrixAttributeList = attributeList.toJavaList(MatrixAttributeVo.class);
        for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
            if (matrixAttributeVo.getUniqueIdentifier() == null) {
                matrixAttributeVo.setUniqueIdentifier(matrixAttributeVo.getUuid());
            }
        }
        return matrixAttributeList;
    }

    @Override
    protected JSONObject myExportAttribute(MatrixVo matrixVo) {
        return null;
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
                if (valueFieldFilter != null) {
                    filterList.add(new MatrixFilterVo(valueFieldFilter.getUuid(), valueFieldFilter.getExpression(), Arrays.asList(valueFieldFilter.getValue())));
                }
                MatrixKeywordFilterVo textFieldFilter = defaultValueFilterVo.getTextFieldFilter();
                if (textFieldFilter != null) {
                    filterList.add(new MatrixFilterVo(textFieldFilter.getUuid(), textFieldFilter.getExpression(), Arrays.asList(textFieldFilter.getValue())));
                }
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
        String viewName = "matrix_" + matrixUuid;
        viewBuilder.setViewName(viewName);
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
                    matrixAttributeVo.setUniqueIdentifier(name);
                    matrixAttributeVo.setName(attrVo.getLabel());
                    matrixAttributeVo.setType(MatrixAttributeType.INPUT.getValue());
                    matrixAttributeVo.setIsDeletable(0);
                    matrixAttributeVo.setSort(sort++);
                    matrixAttributeVo.setIsRequired(0);
                    matrixAttributeList.add(matrixAttributeVo);
                }
                String selectSql = viewBuilder.getSql();
                String md5 = Md5Util.encryptMD5(selectSql);
                String tableType = schemaMapper.checkTableOrViewIsExists(TenantContext.get().getDataDbName(), viewName);
                if (tableType != null) {
                    if (Objects.equals(tableType, "SYSTEM VIEW")) {
                        return matrixAttributeList;
                    } else if (Objects.equals(tableType, "VIEW")) {
                        DataBaseViewInfoVo dataBaseViewInfoVo = dataBaseViewInfoMapper.getDataBaseViewInfoByViewName(viewName);
                        if (dataBaseViewInfoVo != null) {
                            if (Objects.equals(md5, dataBaseViewInfoVo.getMd5())) {
                                // md5相同就不用更新视图了
                                return matrixAttributeList;
                            }
                        }
                    }
                }
                EscapeTransactionJob.State s = new EscapeTransactionJob(() -> {
                    if (schemaMapper.checkSchemaIsExists(TenantContext.get().getDataDbName()) > 0) {
                        if (Objects.equals(tableType, "BASE TABLE")) {
                            schemaMapper.deleteTable(TenantContext.get().getDataDbName() + "." + viewName);
                        }
                        String sql = "CREATE OR REPLACE VIEW " + TenantContext.get().getDataDbName() + "." + viewName + " AS " + selectSql;
                        schemaMapper.insertView(sql);
                    } else {
                        throw new DataBaseNotFoundException();
                    }
                }).execute();
                if (!s.isSucceed()) {
                    throw new MatrixViewCreateSchemaException(matrixName);
                }
                DataBaseViewInfoVo dataBaseViewInfoVo = new DataBaseViewInfoVo();
                dataBaseViewInfoVo.setViewName(viewName);
                dataBaseViewInfoVo.setMd5(md5);
                dataBaseViewInfoVo.setLcu(UserContext.get().getUserUuid());
                dataBaseViewInfoMapper.insertDataBaseViewInfo(dataBaseViewInfoVo);
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
