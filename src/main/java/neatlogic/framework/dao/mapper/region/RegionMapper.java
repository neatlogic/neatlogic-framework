/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    List<RegionVo> getRegionByUpwardNamePath(List<String> values);


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
