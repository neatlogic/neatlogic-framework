package neatlogic.module.framework.matrix.privatedatasource.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.UserVo;
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
public class UserMatrixPrivateDataSourceHandler implements IMatrixPrivateDataSourceHandler {

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
            jsonObj.put("name", "用户ID");
            jsonObj.put("label", "userId");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "名称");
            jsonObj.put("label", "userName");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "是否vip");
            jsonObj.put("label", "vipLevel");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "邮箱");
            jsonObj.put("label", "email");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "电话");
            jsonObj.put("label", "phone");
            jsonObj.put("isPrimaryKey", 0);
            jsonObj.put("isSearchable", 1);
            attributeDefinedList.add(jsonObj);
        }
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", "所属组");
            jsonObj.put("label", "teamName");
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
    private UserMapper userMapper;

    @Resource
    private TeamMapper teamMapper;

    @Override
    public String getUuid() {
        return UuidUtil.getCustomUUID(getLabel());
    }

    @Override
    public String getName() {
        return "用户";
    }

    @Override
    public String getLabel() {
        return "user";
    }

    @Override
    public List<MatrixAttributeVo> getAttributeList() {
        return matrixAttributeList;
    }

    @Override
    public List<Map<String, String>> searchTableData(MatrixDataVo dataVo) {
        JSONArray defaultValue = dataVo.getDefaultValue();
        if (CollectionUtils.isNotEmpty(defaultValue)) {
            List<String> uuidList = defaultValue.toJavaList(String.class);
            List<UserVo> userList = new ArrayList<>();
            userList = userMapper.getUserByUserUuidList(uuidList);
            if (CollectionUtils.isNotEmpty(userList)) {
                for (UserVo userVo : userList) {
                    List<TeamVo> teamList = teamMapper.getTeamListByUserUuid(userVo.getUuid());
                    List<String> teamNameList = teamList.stream().map(TeamVo::getName).collect(Collectors.toList());
                    userVo.setTeamNameList(teamNameList);
                }
            }
            return userListConvertDataList(userList);
        } else {
            List<Map<String, String>> resultList = new ArrayList<>();
            MatrixDataVo searchVo = matrixDataVoConvertSearchCondition(dataVo);
            int rowNum = userMapper.searchUserCountForMatrix(searchVo);
            if (rowNum > 0) {
                dataVo.setRowNum(rowNum);
                searchVo.setRowNum(rowNum);
                List<Map<String, Object>> list = userMapper.searchUserListForMatrix(searchVo);
                for (Map<String, Object> map : list) {
                    if (MapUtils.isEmpty(map)) {
                        continue;
                    }
                    Map<String, String> newMap = new HashMap<>();
                    for (String column : searchVo.getColumnList()) {
                        Object value = map.get(column);
                        if (value != null) {
                            String valueStr = value.toString();
                            if (Objects.equals(column, "vipLevel")) {
                                if (Objects.equals(valueStr, "1")) {
                                    valueStr = "是";
                                } else {
                                    valueStr = "否";
                                }
                            }
                            newMap.put(columnsMap.get(column), valueStr);
                        } else {
                            newMap.put(columnsMap.get(column), "");
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
                }
            }
        }
        newDataVo.setColumnList(columnList);
        if (CollectionUtils.isNotEmpty(dataVo.getNotNullColumnList())) {
            List<String> notNullColumnList = new ArrayList<>();
            for (String notNullColumn : dataVo.getNotNullColumnList()) {
                for(MatrixAttributeVo matrixAttributeVo : matrixAttributeList){
                    if (Objects.equals(matrixAttributeVo.getUuid(), notNullColumn)) {
                        notNullColumnList.add(matrixAttributeVo.getLabel());
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
            } else if (columnsMap.get("userId").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`userId`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("userName").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`userName`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("vipLevel").equals(uuid)) {
                List<String> valueList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(filter.getValueList())) {
                    String value = filter.getValueList().get(0);
                    if (Objects.equals(value, "是")) {
                        valueList.add("1");
                    } else if (Objects.equals(value, "否")) {
                        valueList.add("0");
                    } else {
                        valueList.add("2");
                    }
                }
                newFilterList.add(new MatrixFilterVo("a.`vipLevel`", filter.getExpression(), valueList));
            } else if (columnsMap.get("email").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`email`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("phone").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`phone`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("teamName").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`teamName`", filter.getExpression(), filter.getValueList()));
            }
        }
        newDataVo.setFilterList(newFilterList);
        return newDataVo;
    }

    private List<Map<String, String>> userListConvertDataList(List<UserVo> userList) {
        List<Map<String, String>> dataList = new ArrayList<>();
        for (UserVo userVo : userList) {
            Map<String, String> data = new HashMap<>();
            data.put("uuid", userVo.getUuid());
            data.put(columnsMap.get("uuid"), userVo.getUuid());
            data.put(columnsMap.get("id"), userVo.getId().toString());
            data.put(columnsMap.get("userId"), userVo.getUserId());
            data.put(columnsMap.get("userName"), userVo.getUserName());
            data.put(columnsMap.get("teamName"), String.join(",", userVo.getTeamNameList()));
            data.put(columnsMap.get("vipLevel"), Objects.equals(userVo.getVipLevel(), 1) ? "是" : "否");
            data.put(columnsMap.get("email"), userVo.getEmail() == null ? "" : userVo.getEmail());
            data.put(columnsMap.get("phone"), userVo.getPhone() == null ? "" : userVo.getPhone());
            dataList.add(data);
        }
        return dataList;
    }
}
