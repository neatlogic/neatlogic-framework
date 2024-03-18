package neatlogic.module.framework.matrix.privatedatasource.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.matrix.constvalue.SearchExpression;
import neatlogic.framework.matrix.core.IMatrixPrivateDataSourceHandler;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import neatlogic.framework.matrix.dto.MatrixFilterVo;
import neatlogic.framework.util.UuidUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class RoleMatrixPrivateDataSourceHandler implements IMatrixPrivateDataSourceHandler {

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
            jsonObj.put("name", "描述");
            jsonObj.put("label", "description");
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
    private RoleMapper roleMapper;

    @Override
    public String getUuid() {
        return UuidUtil.getCustomUUID(getLabel());
    }

    @Override
    public String getName() {
        return "角色";
    }

    @Override
    public String getLabel() {
        return "role";
    }

    @Override
    public List<MatrixAttributeVo> getAttributeList() {
        return matrixAttributeList;
    }

    @Override
    public List<Map<String, String>> searchTableData(MatrixDataVo dataVo) {
        List<RoleVo> roleList = new ArrayList<>();
        JSONArray defaultValue = dataVo.getDefaultValue();
        if (CollectionUtils.isNotEmpty(defaultValue)) {
            List<String> uuidList = defaultValue.toJavaList(String.class);
            roleList = roleMapper.getRoleListContainsDeletedByUuidList(uuidList);
        } else {
            MatrixDataVo searchVo = matrixDataVoConvertSearchCondition(dataVo);
            int rowNum = roleMapper.searchRoleCountForMatrix(searchVo);
            if (rowNum > 0) {
                dataVo.setRowNum(rowNum);
                searchVo.setRowNum(rowNum);
                roleList = roleMapper.searchRoleListForMatrix(searchVo);
            }
        }
        return roleListConvertDataList(roleList);
    }

    private MatrixDataVo matrixDataVoConvertSearchCondition(MatrixDataVo dataVo) {
        MatrixDataVo newDataVo = new MatrixDataVo();
        newDataVo.setCurrentPage(dataVo.getCurrentPage());
        newDataVo.setPageSize(dataVo.getPageSize());
        newDataVo.setNeedPage(dataVo.getNeedPage());
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
                newFilterList.add(new MatrixFilterVo("uuid", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("id").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("id", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("name").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("name", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("description").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("description", filter.getExpression(), filter.getValueList()));
            }
        }
        newDataVo.setFilterList(newFilterList);
        return newDataVo;
    }

    private List<Map<String, String>> roleListConvertDataList(List<RoleVo> roleList) {
        List<Map<String, String>> dataList = new ArrayList<>();
        for (RoleVo roleVo : roleList) {
            Map<String, String> data = new HashMap<>();
            data.put("uuid", roleVo.getUuid());
            data.put(columnsMap.get("uuid"), roleVo.getUuid());
            data.put(columnsMap.get("name"), roleVo.getName());
            data.put(columnsMap.get("id"), roleVo.getId().toString());
            data.put(columnsMap.get("description"), roleVo.getDescription());
            dataList.add(data);
        }
        return dataList;
    }
}
