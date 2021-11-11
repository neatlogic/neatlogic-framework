/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.matrix.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.util.FileUtil;
import codedriver.framework.dao.mapper.SchemaMapper;
import codedriver.framework.exception.file.FileNotFoundException;
import codedriver.framework.exception.type.ParamNotExistsException;
import codedriver.framework.file.dao.mapper.FileMapper;
import codedriver.framework.file.dto.FileVo;
import codedriver.framework.matrix.constvalue.MatrixType;
import codedriver.framework.matrix.core.MatrixDataSourceHandlerBase;
import codedriver.framework.matrix.dao.mapper.MatrixDataMapper;
import codedriver.framework.matrix.dao.mapper.MatrixViewMapper;
import codedriver.framework.matrix.dto.*;
import codedriver.framework.matrix.exception.MatrixAttributeNotFoundException;
import codedriver.framework.matrix.exception.MatrixViewNotFoundException;
import codedriver.framework.matrix.exception.MatrixViewSettingFileNotFoundException;
import codedriver.framework.util.TableResultUtil;
import codedriver.module.framework.matrix.service.MatrixService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
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
    private MatrixViewMapper viewMapper;

    @Resource
    private MatrixService matrixService;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private MatrixViewMapper matrixViewMapper;

    @Resource
    private MatrixDataMapper matrixDataMapper;

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
        MatrixViewVo oldMatrixViewVo = viewMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
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
        List<MatrixAttributeVo> matrixAttributeVoList = matrixService.buildView(matrixVo.getUuid(), matrixVo.getName(), xml);
        MatrixViewVo matrixViewVo = new MatrixViewVo();
        matrixViewVo.setMatrixUuid(matrixVo.getUuid());
        matrixViewVo.setFileId(fileId);
        JSONObject config = new JSONObject();
        config.put("attributeList", matrixAttributeVoList);
        matrixViewVo.setConfig(config.toJSONString());
        viewMapper.replaceMatrixView(matrixViewVo);
        return true;
    }

    @Override
    protected void myGetMatrix(MatrixVo matrixVo) {
        MatrixViewVo matrixViewVo = viewMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
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
        matrixViewMapper.deleteMatrixViewByMatrixUuid(uuid);
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
    protected HSSFWorkbook myExportMatrix(MatrixVo matrixVo) {
        return null;
    }

    @Override
    protected void mySaveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList) {

    }

    @Override
    protected List<MatrixAttributeVo> myGetAttributeList(MatrixVo matrixVo) {
        MatrixViewVo matrixViewVo = matrixViewMapper.getMatrixViewByMatrixUuid(matrixVo.getUuid());
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
        MatrixViewVo matrixViewVo = viewMapper.getMatrixViewByMatrixUuid(dataVo.getMatrixUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(dataVo.getMatrixUuid());
        }
        JSONArray attributeList = (JSONArray) JSONPath.read(matrixViewVo.getConfig(), "attributeList");
        if (CollectionUtils.isNotEmpty(attributeList)) {
            List<MatrixAttributeVo> attributeVoList = attributeList.toJavaList(MatrixAttributeVo.class);
            List<String> columnList = attributeVoList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
            dataVo.setColumnList(columnList);
            if (dataVo.getNeedPage()) {
                int rowNum = matrixDataMapper.getDynamicTableDataCount(dataVo);
                dataVo.setRowNum(rowNum);
            }
            List<Map<String, String>> dataList = matrixDataMapper.searchDynamicTableData(dataVo);
            List<Map<String, Object>>  tbodyList = matrixService.matrixTableDataValueHandle(attributeVoList, dataList);
            JSONArray theadList = getTheadList(attributeVoList);
            return TableResultUtil.getResult(theadList, tbodyList, dataVo);
        }
        return new JSONObject();
    }

    @Override
    protected JSONObject myTableDataSearch(MatrixDataVo dataVo) {
        JSONObject returnObj = new JSONObject();
        MatrixViewVo matrixViewVo = viewMapper.getMatrixViewByMatrixUuid(dataVo.getMatrixUuid());
        if (matrixViewVo == null) {
            throw new MatrixViewNotFoundException(dataVo.getUuid());
        }
        JSONArray attributeList = (JSONArray) JSONPath.read(matrixViewVo.getConfig(), "attributeList");
        if (CollectionUtils.isNotEmpty(attributeList)) {
            List<MatrixAttributeVo> matrixAttributeList = attributeList.toJavaList(MatrixAttributeVo.class);
            if (dataVo.getNeedPage()) {
                int rowNum = matrixDataMapper.getDynamicTableDataByColumnCount(dataVo);
                dataVo.setRowNum(rowNum);
            }
            List<String> uuidList = null;
            JSONArray dafaultValue = dataVo.getDefaultValue();
            if (CollectionUtils.isNotEmpty(dafaultValue)) {
                uuidList = dafaultValue.toJavaList(String.class);
            } else {
                uuidList = dataVo.getUuidList();
            }
            List<Map<String, String>> dataMapList = null;
            if (CollectionUtils.isNotEmpty(uuidList)) {
                dataMapList = matrixDataMapper.getDynamicTableDataByUuidList(dataVo);
            } else {
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
        MatrixViewVo matrixViewVo = viewMapper.getMatrixViewByMatrixUuid(dataVo.getMatrixUuid());
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
            }String keywordColumn = dataVo.getKeywordColumn();
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
}
