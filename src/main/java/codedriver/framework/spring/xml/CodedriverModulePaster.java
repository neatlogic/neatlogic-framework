package codedriver.framework.spring.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import codedriver.framework.dto.ModuleVo;

public class CodedriverModulePaster extends AbstractSingleBeanDefinitionParser {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Class getBeanClass(Element element) {
		return ModuleVo.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		String id = element.getAttribute("id");
		String name = element.getAttribute("name");
		String description = element.getAttribute("description");
		bean.addPropertyValue("id", id);
		bean.addPropertyValue("name", name);
		bean.addPropertyValue("description", description);
		bean.setLazyInit(true);
	}
}
