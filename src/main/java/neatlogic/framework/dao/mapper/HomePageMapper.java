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

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.AuthorityVo;
import neatlogic.framework.dto.HomePageVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HomePageMapper {

    int checkHomePageNameIsRepeat(HomePageVo homePage);

    HomePageVo getHomePageById(Long id);

    int getHomePageCount(BasePageVo basePageVo);

    List<HomePageVo> getHomePageList(BasePageVo basePageVo);

    List<AuthorityVo> getHomePageAuthorityListByHomePageId(Long homePageId);

    Integer getMaxSort();

    List<Long> getHomePageIdListByAuthority(AuthenticationInfoVo authenticationInfoVo);

    HomePageVo getMinSortHomePageByIdList(List<Long> idList);

    int insertHomePage(HomePageVo homePage);

    int insertHomePageAuthority(@Param("homePageId") Long homePageId, @Param("authorityVo") AuthorityVo authorityVo);

    int updateHomePageSortById(HomePageVo homePageVo);

    void updateHomePageIsActiveById(Long id);

    int updateSortDecrement(@Param("fromSort")Integer fromSort, @Param("toSort")Integer toSort);

    int updateSortIncrement(@Param("fromSort")Integer fromSort, @Param("toSort")Integer toSort);

    int deleteHomePageById(Long id);

    int deleteHomePageAuthorityByHomePageId(Long homePageId);
}
