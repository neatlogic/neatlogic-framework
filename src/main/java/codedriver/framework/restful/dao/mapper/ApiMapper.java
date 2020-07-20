package codedriver.framework.restful.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;

public interface ApiMapper {

	public ApiVo getApiByToken(String token);

	public List<String> getApiTokenList(ApiVo apiVo);

	public List<ApiVo> getAllApi();

	public List<ApiVo> getApiListByTokenList(List<String> tokenList);

	public int getApiAuditCount(ApiAuditVo apiAuditVo);

	public List<ApiAuditVo> getApiAuditList(ApiAuditVo apiAuditVo);

	public List<ApiVo> getApiVisitTimesListByTokenList(List<String> tokenList);

	public String getApiAuditDetailByHash(String hash);

	public List<ApiVo> getApiAccessCountByTokenList(List<String> tokenList);

	public String getApiAccessCountLockByToken(String token);

	public List<ApiAuditVo> searchApiAuditList(ApiAuditVo apiAuditVo);

	public List<Map<String, String>> searchApiAuditMapList(ApiAuditVo apiAuditVo);

	public int replaceApi(ApiVo apiVo);

	public int insertApiAudit(ApiAuditVo apiAudit);

	public int updateApiComponentIdById(ApiVo apiVo);

	public int batchUpdate(ApiVo apiVo);

	public int replaceApiAuditDetail(@Param("hash") String hash, @Param("content") String content);
	/**
	 * 
	* @Time:2020年7月15日
	* @Description: 插入接口访问次数
	* @param token
	* @param count
	* @return int
	 */
	public int insertApiAccessCount(@Param("token") String token, @Param("count") Integer count);
	
	/**
	 * 
	* @Time:2020年7月15日
	* @Description: 增加接口访问次数 
	* @param token
	* @param visitTimes
	* @return int
	 */
	public int updateApiAccessCount(@Param("token") String token, @Param("count") Integer count);

	public int deleteApiByToken(String token);

}
