package codedriver.framework.restful.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.restful.dto.ApiComponentVo;
import codedriver.framework.restful.dto.ApiVo;

public interface ApiMapper {
	public List<ApiVo> getApiByComponentId(String apiComponentId);

	public ApiVo getApiByToken(String token);

	public int replaceApiComponent(ApiComponentVo apiComponentVo);

	public int insertApi(ApiVo apiVo);

	public int updateApiComponentIdById(ApiVo apiVo);

	public int deleteAllApiComponent();
}
