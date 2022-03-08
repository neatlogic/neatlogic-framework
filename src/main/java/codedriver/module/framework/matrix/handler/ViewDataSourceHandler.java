/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.matrix.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.constvalue.ExportFileType;
import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.util.FileUtil;
import codedriver.framework.dao.mapper.SchemaMapper;
import codedriver.framework.exception.database.DataBaseNotFoundException;
import codedriver.framework.exception.file.FileNotFoundException;
import codedriver.framework.exception.type.ParamNotExistsException;
import codedriver.framework.file.dao.mapper.FileMapper;
import codedriver.framework.file.dto.FileVo;
import codedriver.framework.matrix.constvalue.MatrixAttributeType;
import codedriver.framework.matrix.constvalue.MatrixType;
import codedriver.framework.matrix.core.MatrixDataSourceHandlerBase;
import codedriver.framework.matrix.dao.mapper.MatrixViewDataMapper;
import codedriver.framework.matrix.dto.*;
import codedriver.framework.matrix.exception.*;
import codedriver.framework.matrix.view.MatrixViewSqlBuilder;
import codedriver.framework.transaction.core.EscapeTransactionJob;
import codedriver.framework.util.TableResultUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
    public String getExportFileType() {
        return ExportFileType.CSV.getValue();
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
        MatrixViewVo oldMatrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
        if (oldMatrixViewVo != null) {
            if (fileId.equals(oldMatrixViewVo.getFileId())) {
                return false;
            }
        }
        String xml = IOUtils.toString(FileUtil.getData(fileVo.getPath()), StandardCharsets.UTF_8);
//            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ci><attrs><attr name=\"uuid\" label=\"用户uuid\"/><attr name=\"user_id\" label=\"用户id\"/><attr name=\"user_name\" label=\"用户名\"/><attr name=\"teamName\" label=\"分组\"/><attr name=\"vipLevel\" label=\"是否VIP\"/><attr name=\"phone\" label=\"电话\"/><attr name=\"email\" label=\"邮件\"/></attrs><sql>SELECT `u`.`uuid` AS uuid, `u`.`id` AS id, `u`.`user_id` as user_id, `u`.`user_name` as user_name, u.email as email, u.phone as phone, if(u.vip_level=0,'否','是') as vipLevel, group_concat( `t`.`name`) AS teamName FROM `user` `u` LEFT JOIN `user_team` `ut` ON `u`.`uuid` = `ut`.`user_uuid` LEFT JOIN `team` `t` ON `t`.`uuid` = `ut`.`team_uuid` GROUP BY u.uuid </sql></ci>";
        if (StringUtils.isBlank(xml)) {
            throw new MatrixViewSettingFileNotFoundException();
        }
        List<MatrixAttributeVo> matrixAttributeVoList = buildView(matrixVo.getUuid(), matrixVo.getName(), xml);
        MatrixViewVo matrixViewVo = new MatrixViewVo();
        matrixViewVo.setMatrixUuid(matrixVo.getUuid());
        matrixViewVo.setFileId(fileId);
        JSONObject config = new JSONObject();
        config.put("attributeList", matrixAttributeVoList);
        matrixViewVo.setConfig(config.toJSONString());
        matrixMapper.replaceMatrixView(matrixViewVo);
        return true;
    }

    @Override
    protected void myGetMatrix(MatrixVo matrixVo) {
        MatrixViewVo matrixViewVo = matrixMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(matrixVo.getName());
        }
        Long fileId = matrixViewVo.getFileId();
        matrixVo.setFileId(fileId);
        FileVo fileVo = fileMapper.getFileById(fileId);
        if (fileVo == null) {
            throw new FileNotFoundException(fileId);
        }
        matrixVo.setFileVo(fileVo);
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
                List<Map<String, Object>> dataList;
                while (currentPage <= pageCount) {
                    dataVo.setCurrentPage(currentPage);
                    dataList = matrixViewDataMapper.searchDynamicTableData(dataVo);
                    if (!dataList.isEmpty()) {
                        StringBuilder content = new StringBuilder();
                        for (Map<String, Object> map : dataList) {
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
    protected Workbook myExportMatrix2Excel(MatrixVo matrixVo) {
        return null;
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
            List<Map<String, Object>> dataList = matrixViewDataMapper.searchDynamicTableData(dataVo);
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
            List<Map<String, Object>> dataMapList = null;
            JSONArray dafaultValue = dataVo.getDefaultValue();
            if (CollectionUtils.isNotEmpty(dafaultValue)) {
                dataMapList = matrixViewDataMapper.getDynamicTableDataByUuidList(dataVo);
            } else {
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
                        List<Map<String, Object>> dataMapList = matrixViewDataMapper.getDynamicTableDataForSelect(dataVo);
                        resultList.addAll(matrixTableDataValueHandle(dataMapList));
                    }
                }
            } else {
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
                List<Map<String, Object>> dataMapList = matrixViewDataMapper.getDynamicTableDataForSelect(dataVo);
                resultList.addAll(matrixTableDataValueHandle(dataMapList));
            }
        }
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

    private List<MatrixAttributeVo> buildView(String matrixUuid, String matrixName, String xml) {
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
                    throw new MatrixViewCreateSchemaException(matrixName, true);
                }
            }
        }
        return matrixAttributeList;
    }

    public List<Map<String, JSONObject>> matrixTableDataValueHandle(List<Map<String, Object>> valueList) {
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (Map<String, Object> valueMap : valueList) {
                Map<String, JSONObject> resultMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
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
