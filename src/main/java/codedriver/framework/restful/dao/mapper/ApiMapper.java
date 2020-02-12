package codedriver.framework.restful.dao.mapper;

import java.util.List;

import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;

public interface ApiMapper {
	public List<ApiVo> getApiByComponentId(String apiComponentId);

	public ApiVo getApiByToken(String token);

	public List<String> getApiTokenList(ApiVo apiVo);

	public List<ApiVo> getApiListByTokenList(List<String> tokenList);

	public int getApiAuditCount(ApiAuditVo apiAuditVo);

	public List<ApiAuditVo> getApiAuditList(ApiAuditVo apiAuditVo);

	public List<ApiVo> getApiVisitTimesListByTokenList(List<String> tokenList);

	public int replaceApi(ApiVo apiVo);

	public int insertApiAudit(ApiAuditVo apiAudit);
	
	public int updateApiComponentIdById(ApiVo apiVo);

	public int deleteApiByToken(String token);
	
}
