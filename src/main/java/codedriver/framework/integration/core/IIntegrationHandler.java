package codedriver.framework.integration.core;

import java.util.List;

import org.springframework.util.ClassUtils;

import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.integration.dto.PartternVo;

public interface IIntegrationHandler<T> {
	public String getName();

	public default String getHandler() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	public T getData(IntegrationVo integrationVo);

	// 输入参数模板
	public List<PartternVo> getInputPartternList();

	// 输出参数模板
	public List<PartternVo> getOutputPartternList();
}
