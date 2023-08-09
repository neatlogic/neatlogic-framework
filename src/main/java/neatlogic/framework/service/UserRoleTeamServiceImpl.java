package neatlogic.framework.service;

import neatlogic.framework.restful.groupsearch.core.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRoleTeamServiceImpl implements UserRoleTeamService {

    @Override
    public List<GroupSearchGroupVo> searchUserRoleTeam(GroupSearchVo groupSearchVo) {
//        List<Object> groupList = jsonObj.getJSONArray("groupList");
//        List<Object> excludeList = jsonObj.getJSONArray("excludeList");
        List<String> groupList = groupSearchVo.getGroupList();
        List<String> excludeList = groupSearchVo.getGroupList();
        int groupCount = 0;
//        JSONArray resultArray = new JSONArray();
        List<GroupSearchGroupVo> resultArray = new ArrayList<>();
        Map<String, IGroupSearchHandler> handlerMap = GroupSearchHandlerFactory.getComponentMap();
        for (Map.Entry<String, IGroupSearchHandler> handlerEntry : handlerMap.entrySet()) {
            IGroupSearchHandler handler = handlerEntry.getValue();
            if (groupList != null && !groupList.contains(handler.getName())) {
                continue;
            }
            //如果group存在不需要限制总数的类型
            if (handler.isLimit()) {
                groupCount++;
            }
            List<GroupSearchOptionVo> dataList = null;
            boolean isMore = true;
//            if (!jsonObj.containsKey("valueList") || jsonObj.getJSONArray("valueList").isEmpty()) {
            if (CollectionUtils.isEmpty(groupSearchVo.getValueList())) {
                dataList = handler.search(groupSearchVo);
            } else {
                isMore = false;
//                if (jsonObj.containsKey("valueList") && !jsonObj.getJSONArray("valueList").isEmpty()) {
                if (CollectionUtils.isNotEmpty(groupSearchVo.getValueList())) {
                    dataList = handler.reload(groupSearchVo);
                } else {
                    return resultArray;
                }
            }
//            JSONObject resultObj = handler.repack(dataList);
//            //过滤 excludeList
//            dataList = resultObj.getJSONArray("dataList");
//            if (excludeList != null && !excludeList.isEmpty()) {
//                for (Object exclude : excludeList) {
//                    dataList = dataList.stream().filter(data -> !(JSONObject.parseObject(JSONObject.toJSONString(data)).getString("value").equalsIgnoreCase(exclude.toString()))).collect(Collectors.toList());
//                }
//            }
//
//
//            resultObj.put("dataList", dataList);
//            if (handler.isLimit()) {
//                resultObj.put("index", 0);
//            } else {
//                resultObj.put("index", dataList.size());
//            }
//            resultObj.put("isLimit", handler.isLimit());
//            resultObj.put("isMore", isMore);
//            resultArray.add(resultObj);
//            GroupSearchGroupVo groupSearchGroupVo = handler.repack(dataList);
            GroupSearchGroupVo groupSearchGroupVo = new GroupSearchGroupVo();
            groupSearchGroupVo.setValue(handler.getName());
            groupSearchGroupVo.setText(handler.getLabel());
            groupSearchGroupVo.setSort(handler.getSort());
            //过滤 excludeList
            if (CollectionUtils.isNotEmpty(excludeList)) {
//                List<GroupSearchOptionVo> groupSearchOptionList = groupSearchGroupVo.getDataList();
                for (String exclude : excludeList) {
//                    groupSearchOptionList = groupSearchOptionList.stream().filter(data -> !(data.getValue().equalsIgnoreCase(exclude))).collect(Collectors.toList());
                    dataList = dataList.stream().filter(data -> !(data.getValue().equalsIgnoreCase(exclude))).collect(Collectors.toList());
                }
//                groupSearchGroupVo.setDataList(groupSearchOptionList);
            }
            groupSearchGroupVo.setDataList(dataList);
            if (handler.isLimit()) {
                groupSearchGroupVo.setIndex(0);
            } else {
                groupSearchGroupVo.setIndex(groupSearchGroupVo.getDataList().size());
            }
            groupSearchGroupVo.setIsLimit(handler.isLimit());
            groupSearchGroupVo.setIsMore(isMore);
            resultArray.add(groupSearchGroupVo);
        }
        //排序
//        resultArray.sort(Comparator.comparing(obj -> ((JSONObject) obj).getInteger("sort")));
        resultArray.sort(Comparator.comparing(GroupSearchGroupVo::getSort));
        //如果是搜索模式
        if (CollectionUtils.isEmpty(groupSearchVo.getValueList())) {
            //总显示选项个数,默认18个
            Integer total = groupSearchVo.getTotal();
            if (total == null) {
                total = 18;
            }
            //预留“更多”选项位置
            total = total - groupCount;
            //计算index位置
            int i = 0;
            int totalTmp = 0;
            HashSet<String> set = new HashSet<>();
            out:
            while (totalTmp < total) {
//                for (Object ob : resultArray) {
//                    if (((JSONObject) ob).getBoolean("isLimit")) {
//                        JSONArray dataList = ((JSONObject) ob).getJSONArray("dataList");
//                        if (i < dataList.size()) {
//                            int index = ((JSONObject) ob).getInteger("index");
//                            ((JSONObject) ob).put("index", ++index);
//                            //判断是否还有多余项
//                            if (dataList.size() == index) {
//                                ((JSONObject) ob).put("isMore", false);
//                                total++;
//                            }
//                            dataList.get(i);
//                            if (totalTmp < (total - 1)) {
//                                totalTmp++;
//                            } else {
//                                break out;
//                            }
//
//                        } else {
//                            set.add(((JSONObject) ob).getString("value"));
//                        }
//                    } else {
//                        ((JSONObject) ob).put("isMore", false);
//                    }
//                }
                for (GroupSearchGroupVo groupSearchGroupVo : resultArray) {
                    if (groupSearchGroupVo.getIsLimit()) {
                        List<GroupSearchOptionVo> dataList = groupSearchGroupVo.getDataList();
                        if (i < dataList.size()) {
                            int index = groupSearchGroupVo.getIndex();
                            groupSearchGroupVo.setIndex(++index);
                            //判断是否还有多余项
                            if (dataList.size() == index) {
                                groupSearchGroupVo.setIsMore(false);
                                total++;
                            }
                            dataList.get(i);
                            if (totalTmp < (total - 1)) {
                                totalTmp++;
                            } else {
                                break out;
                            }

                        } else {
                            set.add(groupSearchGroupVo.getValue());
                        }
                    } else {
                        groupSearchGroupVo.setIsMore(false);
                    }
                }
                if (set.size() == groupCount) {
                    break out;
                }
                i++;
            }
            //则根据index删掉多余数据
//            for (Object ob : resultArray) {
//                JSONArray dataList = ((JSONObject) ob).getJSONArray("dataList");
//                if (CollectionUtils.isEmpty(dataList)) {
//                    ((JSONObject) ob).put("isMore", false);
//                } else {
//                    int index = ((JSONObject) ob).getInteger("index");
//                    ((JSONObject) ob).put("dataList", dataList.subList(0, index));
//                }
//            }
            for (GroupSearchGroupVo groupSearchGroupVo : resultArray) {
                List<GroupSearchOptionVo> dataList = groupSearchGroupVo.getDataList();
                if (CollectionUtils.isEmpty(dataList)) {
                    groupSearchGroupVo.setIsMore(false);
                } else {
                    int index = groupSearchGroupVo.getIndex();
                    groupSearchGroupVo.setDataList(dataList.subList(0, index));
                }
            }

        }
        return resultArray;
    }
}
