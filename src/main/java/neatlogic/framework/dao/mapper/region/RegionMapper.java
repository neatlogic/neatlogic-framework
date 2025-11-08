/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.dao.mapper.region;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.region.RegionTeamVo;
import neatlogic.framework.dto.region.RegionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RegionMapper {

    Integer getMaxRhtCode();

    List<RegionVo> getRegionListForTree(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("isActive") Integer isActive);

    RegionVo getRegionById(Long id);

    RegionVo getRegionById(String id);

    RegionVo getRegionByUpwardNamePath(String upwardNamePath);

    List<RegionVo> getRegionListByIdList(List<Long> idList);

    List<RegionVo> getAncestorsAndSelfByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

    int searchRegionCount(RegionVo region);

    List<RegionVo> searchRegion(RegionVo region);

    List<RegionVo> getRegionByNameList(List<String> values);

    List<RegionVo> getRegionByUpwardNamePathList(List<String> values);


    int searchRegionTeamCount(RegionTeamVo region);

    List<TeamVo> searchRegionTeam(RegionTeamVo region);

    int checkRegionNameIsRepeat(RegionVo regionVo);

    List<Long> getRegionIdListByTeamUuidListAndType(@Param("teamUuidList") List<String> teamUuidList, @Param("type") String type);

    List<Long> getRegionIdListByTeamUuidListAndCheckedChildren(@Param("teamUuidList") List<String> teamUuidList, @Param("type") String type, @Param("checkedChildren") int checkedChildren);

    int updateUpwardIdPathByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

    int updateUpwardNamePathByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

    Long getParentIdById(Long id);

    void insertRegion(RegionVo region);

    void deleteRegionById(Long id);

    void insertRegionTeam(RegionTeamVo regionTeamVo);

    void deleteRegionExpired(@Param("regionId") Long regionId, @Param("updateTime") Long updateTime);

    void deleteRegionTeamByRegionId(Long id);

    void deleteRegionTeamByRegionIdAndTypeAndTeamUuidList(@Param("regionId") Long regionId, @Param("teamUuidArray") JSONArray teamUuidArray);

}
