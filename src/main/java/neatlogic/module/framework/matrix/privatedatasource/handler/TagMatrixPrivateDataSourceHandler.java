package neatlogic.module.framework.matrix.privatedatasource.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dao.mapper.TagMapper;
import neatlogic.framework.dto.TagVo;
import neatlogic.framework.matrix.constvalue.SearchExpression;
import neatlogic.framework.matrix.core.IMatrixPrivateDataSourceHandler;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import neatlogic.framework.matrix.dto.MatrixFilterVo;
import neatlogic.framework.util.UuidUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class TagMatrixPrivateDataSourceHandler implements IMatrixPrivateDataSourceHandler {

    private final List<MatrixAttributeVo> matrixAttributeList = new ArrayList<>();

    private final Map<String, String> columnsMap = new HashMap<>();

    {
        JSONArray attributeDefinedList = new JSONArray();
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "ID");
            jsonObj.put("label", "id");
            jsonObj.put("isPrimaryKey", 1);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "名称");
            jsonObj.put("label", "name");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "类型");
            jsonObj.put("label", "type");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }

        this.setAttribute(matrixAttributeList, attributeDefinedList);
        for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
            columnsMap.put(matrixAttributeVo.getLabel(), matrixAttributeVo.getUuid());
        }
    }

    @Resource
    private TagMapper tagMapper;

    @Override
    public String getUuid() {
        return UuidUtil.getCustomUUID(getLabel());
    }

    @Override
    public String getName() {
        return "标签";
    }

    @Override
    public String getLabel() {
        return "tag";
    }

    @Override
    public List<MatrixAttributeVo> getAttributeList() {
        return matrixAttributeList;
    }

    @Override
    public List<Map<String, String>> searchTableData(MatrixDataVo dataVo) {
        JSONArray defaultValue = dataVo.getDefaultValue();
        if (CollectionUtils.isNotEmpty(defaultValue)) {
            List<Long> idList = defaultValue.toJavaList(Long.class);
            List<TagVo> tagList = tagMapper.getTagListByIdList(idList);
            return tagListConvertDataList(tagList);
        } else {
            List<Map<String, String>> resultList = new ArrayList<>();
            MatrixDataVo searchVo = matrixDataVoConvertSearchCondition(dataVo);
            int rowNum = tagMapper.searchTagCountForMatrix(searchVo);
            if (rowNum > 0) {
                dataVo.setRowNum(rowNum);
                searchVo.setRowNum(rowNum);
                List<Map<String, Object>> list = tagMapper.searchTagForMatrix(searchVo);
                for (Map<String, Object> map : list) {
                    if (MapUtils.isEmpty(map)) {
                        continue;
                    }
                    Map<String, String> newMap = new HashMap<>();
                    for (String column : searchVo.getColumnList()) {
                        Object value = map.get(column);
                        if (value == null) {
                            value = "";
                        }
                        String valueStr = value.toString();
                        newMap.put(columnsMap.get(column), valueStr);
                        if (Objects.equals(column, "id")) {
                            newMap.put("uuid", valueStr);
                        }
                    }
                    resultList.add(newMap);
                }
            }
            return resultList;
        }
    }

    private MatrixDataVo matrixDataVoConvertSearchCondition(MatrixDataVo dataVo) {
        MatrixDataVo newDataVo = new MatrixDataVo();
        newDataVo.setCurrentPage(dataVo.getCurrentPage());
        newDataVo.setPageSize(dataVo.getPageSize());
        newDataVo.setNeedPage(dataVo.getNeedPage());
        List<String> columnList = new ArrayList<>();
        for (String column : dataVo.getColumnList()) {
            for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
                if (Objects.equals(matrixAttributeVo.getUuid(), column)) {
                    columnList.add(matrixAttributeVo.getLabel());
                    break;
                }
            }
        }
        newDataVo.setColumnList(columnList);
        if (CollectionUtils.isNotEmpty(dataVo.getNotNullColumnList())) {
            List<String> notNullColumnList = new ArrayList<>();
            for (String notNullColumn : dataVo.getNotNullColumnList()) {
                for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
                    if (Objects.equals(matrixAttributeVo.getUuid(), notNullColumn)) {
                        notNullColumnList.add(matrixAttributeVo.getLabel());
                        break;
                    }
                }
            }
            newDataVo.setNotNullColumnList(notNullColumnList);
        }
        List<MatrixFilterVo> filterList = dataVo.getFilterList();
        if (CollectionUtils.isEmpty(filterList)) {
            return newDataVo;
        }
        List<MatrixFilterVo> newFilterList = new ArrayList<>();
        for (MatrixFilterVo filter : filterList) {
            String uuid = filter.getUuid();
            if (StringUtils.isBlank(uuid)) {
                continue;
            }
            if (!Objects.equals(filter.getExpression(), SearchExpression.NULL.getExpression()) && !Objects.equals(filter.getExpression(), SearchExpression.NOTNULL.getExpression())) {
                List<String> valueList = filter.getValueList();
                if (CollectionUtils.isEmpty(valueList)) {
                    continue;
                }
                String value = valueList.get(0);
                if (StringUtils.isBlank(value)) {
                    continue;
                }
            }
            if (columnsMap.get("id").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`id`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("name").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`name`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("type").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`type`", filter.getExpression(), filter.getValueList()));
            }
        }
        newDataVo.setFilterList(newFilterList);
        return newDataVo;
    }

    private List<Map<String, String>> tagListConvertDataList(List<TagVo> tagVoList) {
        List<Map<String, String>> dataList = new ArrayList<>();
        for (TagVo tag : tagVoList) {
            Map<String, String> data = new HashMap<>();
            data.put("uuid", tag.getId().toString());
            data.put(columnsMap.get("id"), tag.getId().toString());
            data.put(columnsMap.get("name"), tag.getName());
            data.put(columnsMap.get("type"), tag.getType());
            dataList.add(data);
        }
        return dataList;
    }
}
