package neatlogic.framework.service;

import neatlogic.framework.restful.groupsearch.core.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRoleTeamServiceImpl implements UserRoleTeamService {

    @Override
    public List<GroupSearchGroupVo> searchUserRoleTeam(GroupSearchVo groupSearchVo) {
        List<String> groupList = groupSearchVo.getGroupList();
        List<String> excludeList = groupSearchVo.getExcludeList();
        int groupCount = 0;
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
            if (CollectionUtils.isEmpty(groupSearchVo.getValueList())) {
                dataList = handler.search(groupSearchVo);
            } else {
                isMore = false;
                if (CollectionUtils.isNotEmpty(groupSearchVo.getValueList())) {
                    dataList = handler.reload(groupSearchVo);
                } else {
                    return resultArray;
                }
            }
            GroupSearchGroupVo groupSearchGroupVo = new GroupSearchGroupVo();
            groupSearchGroupVo.setValue(handler.getName());
            groupSearchGroupVo.setText(handler.getLabel());
            groupSearchGroupVo.setSort(handler.getSort());
            //过滤 excludeList
            if (CollectionUtils.isNotEmpty(excludeList)) {
                for (String exclude : excludeList) {
                    dataList = dataList.stream().filter(data -> !(data.getValue().equalsIgnoreCase(exclude))).collect(Collectors.toList());
                }
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
