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

package neatlogic.framework.restful.dao.mapper;

import neatlogic.framework.restful.dto.ApiAuditVo;
import neatlogic.framework.restful.dto.ApiVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author longrf
 * @date 2022/4/11 12:03 下午
 */
public interface ApiAuditMapper {

    int getApiAuditCount(ApiAuditVo apiAuditVo);

    List<ApiAuditVo> getApiAuditList(ApiAuditVo apiAuditVo);

//    List<ApiVo> getApiVisitTimesListByTokenList(List<String> tokenList);

    List<ApiVo> getApiAccessCountByTokenList(List<String> tokenList);

//    String getApiAccessCountLockByToken(String token);

    List<ApiAuditVo> searchApiAuditList(ApiAuditVo apiAuditVo);

    int searchApiAuditListCount(ApiAuditVo apiAuditVo);

    List<ApiAuditVo> searchApiAuditForExport(ApiAuditVo apiAuditVo);

    List<String> getDistinctTokenInApiAudit();

    String getAuditFileByHash(String hash);

    int insertApiAudit(ApiAuditVo apiAudit);

    /**
     * @param token
     * @param count
     * @return int
     * @Time:2020年7月15日
     * @Description: 插入接口访问次数
     */
    int insertApiAccessCount(@Param("token") String token, @Param("count") long count);

    int insertAuditFile(@Param("hash") String hash, @Param("filePath") String filePath);

    /**
     * @param token
     * @param count
     * @return int
     * @Time:2020年7月15日
     * @Description: 增加接口访问次数
     */
//    int updateApiAccessCount(@Param("token") String token, @Param("count") Long count);

    void deleteAuditByDayBefore(int dayBefore);
}
