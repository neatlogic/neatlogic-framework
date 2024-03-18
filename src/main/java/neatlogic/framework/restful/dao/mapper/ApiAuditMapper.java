/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
