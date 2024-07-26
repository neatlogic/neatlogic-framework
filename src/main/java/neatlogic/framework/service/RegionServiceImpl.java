package neatlogic.framework.service;

import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.RegionType;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.region.RegionMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RegionServiceImpl implements RegionService {
    @Resource
    RegionMapper regionMapper;

    @Resource
    AuthenticationInfoService authenticationInfoService;

    @Resource
    TeamMapper teamMapper;

    /**
     * 根据组获取地域id列表
     *
     * @param teamUuidList 组
     */
    @Override
    public List<Long> getRegionIdListByTeamUuidList(List<String> teamUuidList) {
        Set<Long> regionIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(teamUuidList)) {
            List<Long> regionIdList = regionMapper.getRegionIdListByTeamUuidListAndType(teamUuidList, RegionType.OWNER.getValue());
            regionIdSet.addAll(regionIdList);
            Set<String> upwardUuidSet = authenticationInfoService.getTeamSetWithParents(teamUuidList);
            if (CollectionUtils.isNotEmpty(upwardUuidSet)) {
                regionIdList = regionMapper.getRegionIdListByTeamUuidListAndCheckedChildren(new ArrayList<>(upwardUuidSet), RegionType.OWNER.getValue(), 1);
                regionIdSet.addAll(regionIdList);
            }
        }
        return new ArrayList<>(regionIdSet);
    }

    /**
     * 根据上报人uuid获取地域id列表
     *
     * @param userUuid 用户uuid
     */
    @Override
    public List<Long> getRegionIdListByUserUuid(String userUuid) {
        List<String> teamUuidList = teamMapper.getTeamUuidListByUserUuid(GroupSearch.removePrefix(userUuid));
        if (CollectionUtils.isEmpty(teamUuidList)) {
            return new ArrayList<>();
        }
        return getRegionIdListByTeamUuidList(teamUuidList);
    }
}
