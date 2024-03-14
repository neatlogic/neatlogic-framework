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
            jsonObj.put("label", "teamNameList");
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
        List<UserVo> userList = new ArrayList<>();
        JSONArray defaultValue = dataVo.getDefaultValue();
        if (CollectionUtils.isNotEmpty(defaultValue)) {
            List<String> uuidList = defaultValue.toJavaList(String.class);
            userList = userMapper.getUserByUserUuidList(uuidList);
        } else {
            MatrixDataVo searchVo = matrixDataVoConvertSearchCondition(dataVo);
            int rowNum = userMapper.searchUserCountForMatrix(searchVo);
            if (rowNum > 0) {
                searchVo.setRowNum(rowNum);
                userList = userMapper.searchUserListForMatrix(searchVo);
            }
        }
        if (CollectionUtils.isNotEmpty(userList)) {
            for (UserVo userVo : userList) {
                List<TeamVo> teamList = teamMapper.getTeamListByUserUuid(userVo.getUuid());
                List<String> teamNameList = teamList.stream().map(TeamVo::getName).collect(Collectors.toList());
                userVo.setTeamNameList(teamNameList);
            }
        }
        return userListConvertDataList(userList);
    }

    private MatrixDataVo matrixDataVoConvertSearchCondition(MatrixDataVo dataVo) {
        List<MatrixFilterVo> filterList = dataVo.getFilterList();
        if (CollectionUtils.isEmpty(filterList)) {
            return dataVo;
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
                newFilterList.add(new MatrixFilterVo("a.`user_id`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("userName").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`user_name`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("vipLevel").equals(uuid)) {
                List<String> valueList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(filter.getValueList())) {
                    String value = filter.getValueList().get(0);
                    if (Objects.equals(value, "是")) {
                        valueList.add("1");
                    } else if (Objects.equals(value, "否")) {
                        valueList.add("0");
                    }
                }
                newFilterList.add(new MatrixFilterVo("a.`vip_level`", filter.getExpression(), valueList));
            } else if (columnsMap.get("email").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`email`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("phone").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`phone`", filter.getExpression(), filter.getValueList()));
            } else if (columnsMap.get("teamNameList").equals(uuid)) {
                newFilterList.add(new MatrixFilterVo("a.`team_name`", filter.getExpression(), filter.getValueList()));
            }
        }
        dataVo.setFilterList(newFilterList);
        return dataVo;
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
            data.put(columnsMap.get("teamNameList"), String.join(",", userVo.getTeamNameList()));
            data.put(columnsMap.get("vipLevel"), Objects.equals(userVo.getVipLevel(), 1) ? "是" : "否");
            data.put(columnsMap.get("email"), userVo.getEmail() == null ? "" : userVo.getEmail());
            data.put(columnsMap.get("phone"), userVo.getPhone() == null ? "" : userVo.getPhone());
            dataList.add(data);
        }
        return dataList;
    }
}
