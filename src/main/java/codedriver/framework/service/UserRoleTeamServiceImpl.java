package codedriver.framework.service;

import codedriver.framework.restful.groupsearch.core.GroupSearchHandlerFactory;
import codedriver.framework.restful.groupsearch.core.IGroupSearchHandler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserRoleTeamServiceImpl implements UserRoleTeamService {

    @Override
    public JSONArray searchUserRoleTeam(JSONObject jsonObj) {
        List<Object> groupList = jsonObj.getJSONArray("groupList");
        List<Object> excludeList = jsonObj.getJSONArray("excludeList");
        int groupCount = 0;
        JSONArray resultArray = new JSONArray();
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
            List<Object> dataList = null;
            boolean isMore = true;
            if (!jsonObj.containsKey("valueList") || jsonObj.getJSONArray("valueList").isEmpty()) {
                dataList = handler.search(jsonObj);
            } else {
                isMore = false;
                if (jsonObj.containsKey("valueList") && !jsonObj.getJSONArray("valueList").isEmpty()) {
                    dataList = handler.reload(jsonObj);
                } else {
                    return resultArray;
                }
            }
            JSONObject resultObj = handler.repack(dataList);
            //过滤 excludeList
            dataList = resultObj.getJSONArray("dataList");
            if (excludeList != null && !excludeList.isEmpty()) {
                for (Object exclude : excludeList) {
                    dataList = dataList.stream().filter(data -> !((JSONObject) data).getString("value").equalsIgnoreCase(exclude.toString())).collect(Collectors.toList());
                }
            }


            resultObj.put("dataList", dataList);
            if (handler.isLimit()) {
                resultObj.put("index", 0);
            } else {
                resultObj.put("index", dataList.size());
            }
            resultObj.put("isLimit", handler.isLimit());
            resultObj.put("isMore", isMore);
            resultArray.add(resultObj);

        }
        //排序
        resultArray.sort(Comparator.comparing(obj -> ((JSONObject) obj).getInteger("sort")));
        //如果是搜索模式
        if (jsonObj.containsKey("keyword")) {
            //总显示选项个数,默认18个
            Integer total = jsonObj.getInteger("total");
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
                for (Object ob : resultArray) {
                    if (((JSONObject) ob).getBoolean("isLimit")) {
                        JSONArray dataList = ((JSONObject) ob).getJSONArray("dataList");
                        if (i < dataList.size()) {
                            int index = ((JSONObject) ob).getInteger("index");
                            ((JSONObject) ob).put("index", ++index);
                            //判断是否还有多余项
                            if (dataList.size() == index) {
                                ((JSONObject) ob).put("isMore", false);
                                total++;
                            }
                            dataList.get(i);
                            if (totalTmp < (total - 1)) {
                                totalTmp++;
                            } else {
                                break out;
                            }

                        } else {
                            set.add(((JSONObject) ob).getString("value"));
                        }
                    } else {
                        ((JSONObject) ob).put("isMore", false);
                    }
                }
                if (set.size() == groupCount) {
                    break out;
                }
                i++;
            }
            //则根据index删掉多余数据
            for (Object ob : resultArray) {
                JSONArray dataList = ((JSONObject) ob).getJSONArray("dataList");
                if (CollectionUtils.isEmpty(dataList)) {
                    ((JSONObject) ob).put("isMore", false);
                } else {
                    int index = ((JSONObject) ob).getInteger("index");
                    ((JSONObject) ob).put("dataList", dataList.subList(0, index));
                }
            }

        }
        return resultArray;
    }
}
