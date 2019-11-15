package codedriver.framework.scheduler.core;

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
public class ScheduleComponentRegister implements BeanDefinitionRegistryPostProcessor{

	Logger logger = LoggerFactory.getLogger(ScheduleComponentRegister.class);
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		try {
			Class pluginBaseClass = Class.forName("codedriver.framework.scheduler.core.JobBase");
			Reflections reflections = new Reflections("codedriver.framework");
			Set<Class> modules = reflections.getSubTypesOf(pluginBaseClass);
			for(Class clazz : modules) {
				RootBeanDefinition bean = new RootBeanDefinition(clazz);
				registry.registerBeanDefinition(clazz.getName(),bean);
			}
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
	}

}
