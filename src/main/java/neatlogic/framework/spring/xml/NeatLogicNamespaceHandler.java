package neatlogic.framework.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NeatLogicNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("module", new NeatLogicModulePaster());
	}

}
