/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.integration.handler;

import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.integration.core.IntegrationHandlerBase;
import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.integration.dto.PatternVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomIntegrationHandler extends IntegrationHandlerBase {
	public String getName() {
		return "自定义";
	}

	public Object myGetData() {
		return null;
	}

	@Override
	public List<PatternVo> getInputPattern() {
		return null;
	}

	@Override
	public List<PatternVo> getOutputPattern() {
		return null;
	}

	@Override
	protected void beforeSend(IntegrationVo integrationVo) {

	}

	@Override
	protected void afterReturn(IntegrationVo integrationVo) {

	}

	@Override
	public Integer hasPattern() {
		return 0;
	}

	@Override
	public void validate(IntegrationResultVo resultVo) throws ApiRuntimeException {

	}
}
