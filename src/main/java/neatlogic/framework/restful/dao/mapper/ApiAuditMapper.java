/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.restful.dao.mapper;

import neatlogic.framework.restful.dto.ApiAuditPathVo;
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

    List<ApiVo> getApiVisitTimesListByTokenList(List<String> tokenList);

    List<ApiVo> getApiAccessCountByTokenList(List<String> tokenList);

    String getApiAccessCountLockByToken(String token);

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
    int insertApiAccessCount(@Param("token") String token, @Param("count") Integer count);

    int insertAuditFile(@Param("hash") String hash, @Param("filePath") String filePath);

    /**
     * @param token
     * @param count
     * @return int
     * @Time:2020年7月15日
     * @Description: 增加接口访问次数
     */
    int updateApiAccessCount(@Param("token") String token, @Param("count") Integer count);

    int updateApiNeedAuditByToken(String token);

    void deleteAuditByDayBefore(int dayBefore);
}
