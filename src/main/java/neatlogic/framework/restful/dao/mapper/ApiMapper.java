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

import neatlogic.framework.restful.dto.ApiVo;
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

    int insertApi(ApiVo apiVo);

    int insertOrUpdateNeedAuditApi(ApiVo apiVo);

    int updatePasswordByToken(@Param("token")String token,@Param("password")String password);
}
