/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.dao.mapper;

import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiMapper {

    ApiVo getApiByToken(String token);

    List<String> getApiTokenList(ApiVo apiVo);

    List<ApiVo> getAllApi();

    List<ApiVo> getAllApiByModuleId(@Param("moduleIdList") List<String> moduleIdList);

    List<ApiVo> getApiListByTokenList(List<String> tokenList);

    int getApiAuditCount(ApiAuditVo apiAuditVo);

    List<ApiAuditVo> getApiAuditList(ApiAuditVo apiAuditVo);

    List<ApiVo> getApiVisitTimesListByTokenList(List<String> tokenList);

    String getApiAuditDetailByHash(String hash);

    List<ApiVo> getApiAccessCountByTokenList(List<String> tokenList);

    String getApiAccessCountLockByToken(String token);

    List<ApiAuditVo> searchApiAuditList(ApiAuditVo apiAuditVo);

    int searchApiAuditListCount(ApiAuditVo apiAuditVo);

    List<ApiAuditVo> searchApiAuditForExport(ApiAuditVo apiAuditVo);

    List<String> getDistinctTokenInApiAudit();

    String getAuditFileByHash(String hash);

    int replaceApi(ApiVo apiVo);

    int insertApiAudit(ApiAuditVo apiAudit);

    int batchUpdate(ApiVo apiVo);

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
     * @param visitTimes
     * @return int
     * @Time:2020年7月15日
     * @Description: 增加接口访问次数
     */
    int updateApiAccessCount(@Param("token") String token, @Param("count") Integer count);

    int updateApiNeedAuditByToken(String token);

    int deleteApiByToken(String token);
}
