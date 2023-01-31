package neatlogic.framework.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CodedriverNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("module", new CodedriverModulePaster());
	}

}
