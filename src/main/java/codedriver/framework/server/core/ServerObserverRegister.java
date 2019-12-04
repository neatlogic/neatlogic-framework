package codedriver.framework.server.core;

import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

@Component
public class ServerObserverRegister implements BeanDefinitionRegistryPostProcessor{
	private Logger logger = LoggerFactory.getLogger(ServerObserverRegister.class);
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}

	@SuppressWarnings({"rawtypes","unchecked"})
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		try {
			Class pluginBaseClass = Class.forName("codedriver.framework.server.core.ServerObserver");
			Reflections reflections = new Reflections("codedriver");
			Set<Class> modules = reflections.getSubTypesOf(pluginBaseClass);
			for(Class clazz : modules) {
				RootBeanDefinition bean = new RootBeanDefinition(clazz);
				registry.registerBeanDefinition(clazz.getName(), bean);
			}
		}catch(ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		
	}

}
