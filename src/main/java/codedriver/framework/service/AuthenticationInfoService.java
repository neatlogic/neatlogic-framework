/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.service;

import codedriver.framework.dto.AuthenticationInfoVo;

import java.util.List;

/**
 * @author linbq
 * @since 2021/8/2 20:43
 **/
public interface AuthenticationInfoService {

    AuthenticationInfoVo getAuthenticationInfo(String userUuid);

    AuthenticationInfoVo getAuthenticationInfo(List<String> userUuidList);
}
