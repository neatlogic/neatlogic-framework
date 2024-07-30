package neatlogic.module.framework.matrix.privatedatasource.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dto.TeamVo;
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
import java.util.stream.Collectors;

@Component
public class TeamMatrixPrivateDataSourceHandler implements IMatrixPrivateDataSourceHandler {

    private final List<MatrixAttributeVo> matrixAttributeList = new ArrayList<>();

    private final Map<String , String> columnsMap = new HashMap<>();

    {
        JSONArray attributeDefinedList = new JSONArray();
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "UUID");
            jsonObj.put("label", "uuid");
            jsonObj.put("isPrimaryKey", 1);
            jsonObj.put("isSearchable", 0);
            attributeDefinedList.add(jsonObj);
        }
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "ID");
            jsonObj.put("label", "id");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 0);
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
            jsonObj.put("name", "父级名称");
            jsonObj.put("label", "parentName");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }
        this.setAttribute(matrixAttributeList , attributeDefinedList);
        for(MatrixAttributeVo matrixAttributeVo : matrixAttributeList){
            columnsMap.put(matrixAttributeVo.getLabel() , matrixAttributeVo.getUuid());
        }
    }

    @Resource
    private TeamMapper teamMapper;

    @Override
    public String getUuid() {
        return UuidUtil.getCustomUUID(getLabel());
    }

    @Override
    public String getName() {
        return "分组";
    }

    @Override
    public String getLabel() {
        return "team";
    }

    @Override
    public List<MatrixAttributeVo> getAttributeList() {
        return matrixAttributeList;
    }

    @Override
    public List<Map<String, String>> searchTableData(MatrixDataVo dataVo) {
        List<TeamVo> teamList = new ArrayList<>();
        JSONArray defaultValue = dataVo.getDefaultValue();
        if (CollectionUtils.isNotEmpty(defaultValue)) {
            List<String> uuidList = defaultValue.toJavaList(String.class);
            teamList = teamMapper.getTeamListContainsDeletedByUuidList(uuidList);
            if (CollectionUtils.isNotEmpty(teamList)) {
                List<String> parentUuidList = teamList.stream().map(TeamVo::getParentUuid).collect(Collectors.toList());
                List<TeamVo> parentList = teamMapper.getTeamListContainsDeletedByUuidList(parentUuidList);
                Map<String, String> map = parentList.stream().collect(Collectors.toMap(TeamVo::getUuid, TeamVo::getName));
                for (TeamVo teamVo : teamList) {
                    teamVo.setParentName(map.get(teamVo.getParentUuid()));
                }
            }
            return teamListConvertDataList(teamList);
        } else {
            List<Map<String, String>> resultList = new ArrayList<>();
            MatrixDataVo searchVo = matrixDataVoConvertSearchCondition(dataVo);
            int rowNum = teamMapper.searchTeamCountForMatrix(searchVo);
            if (rowNum > 0) {
                dataVo.setRowNum(rowNum);
                searchVo.setRowNum(rowNum);
                List<Map<String, Object>> list = teamMapper.searchTeamListForMatrix(searchVo);
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
                        newMap.put(columnsMap.get(column), value.toString());
                        if (Objects.equals(column, "uuid")) {
                            newMap.put("uuid", value.toString());
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
            for(MatrixAttributeVo matrixAttributeVo : matrixAttributeList){
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
            if (columnsMap.get("uuid").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`uuid`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("id").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`id`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("name").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`name`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("parentName").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`parentName`", filter.getExpression(), filter.getValueList()));
            }
        }
        newDataVo.setFilterList(newFilterList);
        return newDataVo;
    }

    private List<Map<String, String>> teamListConvertDataList(List<TeamVo> teamList) {
        List<Map<String, String>> dataList = new ArrayList<>();
        for (TeamVo teamVo : teamList) {
            Map<String, String> data = new HashMap<>();
            data.put("uuid", teamVo.getUuid());
            data.put(columnsMap.get("uuid"), teamVo.getUuid());
            data.put(columnsMap.get("id"), teamVo.getId().toString());
            data.put(columnsMap.get("name"), teamVo.getName());
            data.put(columnsMap.get("parentName"), teamVo.getParentName());
            dataList.add(data);
        }
        return dataList;
    }
}
