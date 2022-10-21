/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.dao.mapper;

import codedriver.framework.restful.dto.ApiAuditPathVo;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;
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
