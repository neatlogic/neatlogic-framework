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

package neatlogic.framework.dao.mapper;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.region.RegionTeamVo;
import neatlogic.framework.dto.region.RegionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RegionMapper {

    Integer getMaxRhtCode();

    List<RegionVo> getRegionListForTree(@Param("lft") Integer lft, @Param("rht") Integer rht);

    RegionVo getRegionById(Long id);

    List<RegionVo> getRegionListByIdList(List<Long> idList);

    int searchRegionCount(RegionVo region);

    List<RegionVo> searchRegion(RegionVo region);

    int searchRegionTeamCount(RegionTeamVo region);

    List<TeamVo> searchRegionTeam(RegionTeamVo region);

    int checkRegionNameIsRepeat(RegionVo regionVo);

    Long getParentIdById(Long id);

    void insertRegion(RegionVo region);

    void deleteRegionById(Long id);

    void insertRegionTeam(RegionTeamVo regionTeamVo);

    void deleteRegionExpired(@Param("regionId") Long regionId,@Param("type") String type,@Param("updateTime") Long updateTime);

    void deleteRegionTeamByRegionId(Long id);

    void deleteRegionTeamByRegionIdAndTypeAndTeamUuidList(@Param("regionId") Long regionId,@Param("type") String type,@Param("teamUuidArray") JSONArray teamUuidArray);
}
