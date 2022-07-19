/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.dao.mapper;

import codedriver.framework.restful.dto.ApiVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiMapper {

    ApiVo getApiByToken(String token);

    List<String> getApiTokenList(ApiVo apiVo);

    List<ApiVo> getAllApi();

    List<ApiVo> getAllApiByModuleId(@Param("moduleIdList") List<String> moduleIdList);

    List<ApiVo> getApiListByTokenList(List<String> tokenList);

    int batchUpdate(ApiVo apiVo);

    int deleteApiByToken(String token);

    int replaceApi(ApiVo apiVo);

    int updatePasswordByToken(@Param("token")String token,@Param("password")String password);

}
