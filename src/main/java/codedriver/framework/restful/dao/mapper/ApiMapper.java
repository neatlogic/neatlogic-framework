package codedriver.framework.restful.dao.mapper;

import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiMapper {

	ApiVo getApiByToken(String token);

	List<String> getApiTokenList(ApiVo apiVo);

	List<ApiVo> getAllApi();

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
	 * 
	* @Time:2020年7月15日
	* @Description: 插入接口访问次数
	* @param token
	* @param count
	* @return int
	 */
	int insertApiAccessCount(@Param("token") String token, @Param("count") Integer count);

	int insertAuditFile(@Param("hash") String hash, @Param("filePath") String filePath);

	/**
	 * 
	* @Time:2020年7月15日
	* @Description: 增加接口访问次数 
	* @param token
	* @param visitTimes
	* @return int
	 */
	int updateApiAccessCount(@Param("token") String token, @Param("count") Integer count);

	int deleteApiByToken(String token);

}
