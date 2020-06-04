package codedriver.framework.startup;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dto.ModuleVo;

public class ModuleInitializer implements WebApplicationInitializer {
	static Logger logger = LoggerFactory.getLogger(ModuleInitializer.class);

	@Override
	public void onStartup(ServletContext context) throws ServletException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			Resource[] resources = resolver.getResources("classpath*:codedriver/**/*-servlet-context.xml");
			for (Resource resource : resources) {
				String path = resource.getURL().getPath();
				path = path.substring(path.indexOf("!") + 1);
				SAXReader reader = new SAXReader();
				Document document = reader.read(resource.getURL());
				Element rootE = document.getRootElement();
				Element codedriverE = rootE.element("module");
				// Element nameE = rootE.element("name");
				String moduleId = null, moduleName = null, urlMapping = null, moduleDescription = null, version = null, group = null, groupName = null, groupSort = null, groupDescription = null;
				moduleId = codedriverE.attributeValue("id");
				moduleName = codedriverE.attributeValue("name");
				urlMapping = codedriverE.attributeValue("urlMapping");
				moduleDescription = codedriverE.attributeValue("description");
				group = codedriverE.attributeValue("group");
				groupName = codedriverE.attributeValue("groupName");
				groupSort = codedriverE.attributeValue("groupSort");
				groupDescription = codedriverE.attributeValue("groupDescription");

				if (StringUtils.isNotBlank(moduleId)) {
					version = Config.getProperty("META-INF/maven/com.techsure/codedriver-" + moduleId + "/pom.properties", "version");
					XmlWebApplicationContext appContext = new XmlWebApplicationContext();
					appContext.setConfigLocation("classpath*:" + path);
					appContext.setId(moduleId);
					//InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);

					ServletRegistration.Dynamic sr = context.addServlet(moduleId + "[" + moduleName + "] " + version, new DispatcherServlet(appContext));
					if (StringUtils.isNotBlank(urlMapping)) {
						sr.addMapping(urlMapping);
					}

					if (moduleId.equalsIgnoreCase("framework")) {
						sr.addMapping("/");
						sr.setLoadOnStartup(1);
					} else {
						sr.setLoadOnStartup(2);
					}

					ModuleVo moduleVo = new ModuleVo();
					moduleVo.setId(moduleId);
					moduleVo.setName(moduleName);
					moduleVo.setDescription(moduleDescription);
					moduleVo.setVersion(version);
					moduleVo.setGroup(group);
					moduleVo.setGroupName(groupName);
					moduleVo.setGroupSort(Integer.valueOf(groupSort));
					moduleVo.setGroupDescription(groupDescription);
					ModuleUtil.addModule(moduleVo);
				}
			}
		} catch (IOException | DocumentException ex) {
			logger.error(ex.getMessage(), ex);
		}

	}
}
