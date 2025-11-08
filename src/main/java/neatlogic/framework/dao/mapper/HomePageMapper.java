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
