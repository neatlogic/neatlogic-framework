package codedriver.framework.integration.core;

import java.util.List;

import org.springframework.util.ClassUtils;

import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.integration.dto.PatternVo;

public interface IIntegrationHandler {

	public enum Type{
		MATRIX("matrix","矩阵外部数据源查询"),
		CUSTOM("custom","自定义");

		private String value;
		private String text;

		private Type(String value,String text){
			this.value = value;
			this.text = text;
		}
		public String getValue(){
			return value;
		}
		public String getText(){
			return text;
		}
	}

	public String getName();

	public default String getHandler() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	public IntegrationResultVo sendRequest(IntegrationVo integrationVo, IRequestFrom iRequestFrom);

	public Integer hasPattern();

	public List<PatternVo> getInputPattern();

	public List<PatternVo> getOutputPattern();

	public String getType();

}
