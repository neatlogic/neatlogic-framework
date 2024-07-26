package neatlogic.framework.service;

import neatlogic.framework.dto.TagVo;

import java.util.List;

public interface RegionService {
    /**
     * 根据组获取地域id列表
     *
     * @param teamUuidList 组
     */
    List<Long> getRegionIdListByTeamUuidList(List<String> teamUuidList);

    /**
     * 根据组获取地域id列表
     *
     * @param userUuid 用户uuid
     */
    List<Long> getRegionIdListByUserUuid(String userUuid);
}
