/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.bootstrap;

import neatlogic.framework.asynchronization.thread.ModuleInitApplicationListener;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleVo;
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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.IOException;

public class ModuleInitializer implements WebApplicationInitializer {
    static Logger logger = LoggerFactory.getLogger(ModuleInitializer.class);

    @Override
    public void onStartup(ServletContext context) throws ServletException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String moduleId = null;
        System.out.println(" _____         _     ____                    ____          _      ____       _                \n" +
                "|_   _|__  ___| |__ / ___| _   _ _ __ ___   / ___|___   __| | ___|  _ \\ _ __(_)_   _____ _ __ \n" +
                "  | |/ _ \\/ __| '_ \\\\___ \\| | | | '__/ _ \\ | |   / _ \\ / _` |/ _ \\ | | | '__| \\ \\ / / _ \\ '__|\n" +
                "  | |  __/ (__| | | |___) | |_| | | |  __/ | |__| (_) | (_| |  __/ |_| | |  | |\\ V /  __/ |   \n" +
                "  |_|\\___|\\___|_| |_|____/ \\__,_|_|  \\___|  \\____\\___/ \\__,_|\\___|____/|_|  |_| \\_/ \\___|_|   \n" +
                "===============================================================================================");
        try {
            Resource[] resources = resolver.getResources("classpath*:neatlogic/**/*-servlet-context.xml");
            for (Resource resource : resources) {
                String path = resource.getURL().getPath();
                path = path.substring(path.indexOf("!") + 1);
                SAXReader reader = new SAXReader();
                Document document = reader.read(resource.getURL());
                Element rootE = document.getRootElement();
                Element codedriverE = rootE.element("module");
                // Element nameE = rootE.element("name");
                String moduleName = null, urlMapping = null, moduleDescription = null, version = null, group = null, groupName = null, groupSort = null, groupDescription = null;
                moduleId = codedriverE.attributeValue("id");
                moduleName = codedriverE.attributeValue("name");
                urlMapping = codedriverE.attributeValue("urlMapping");
                moduleDescription = codedriverE.attributeValue("description");
                group = codedriverE.attributeValue("group");
                groupName = codedriverE.attributeValue("groupName");
                groupSort = codedriverE.attributeValue("groupSort");
                groupDescription = codedriverE.attributeValue("groupDescription");

                if (StringUtils.isNotBlank(moduleId)) {
                    System.out.println("start to initialize module " + moduleId + "...");
                    version = Config.getProperty("META-INF/maven/com.techsure/neatlogic-" + moduleId + "/pom.properties", "version");
                    CodedriverWebApplicationContext appContext = new CodedriverWebApplicationContext();
                    appContext.setConfigLocation("classpath*:" + path);
                    appContext.setId(moduleId);
                    appContext.setModuleId(moduleId);
                    appContext.setModuleName(moduleName);
                    appContext.setGroupName(groupName);
                    appContext.setGroup(group);

                    ModuleVo moduleVo = new ModuleVo();
                    moduleVo.setId(moduleId);
                    moduleVo.setName(moduleName);
                    moduleVo.setDescription(moduleDescription);
                    moduleVo.setVersion(version);
                    moduleVo.setGroup(group);
                    moduleVo.setGroupName(groupName);
                    moduleVo.setGroupSort(Integer.parseInt(groupSort));
                    moduleVo.setGroupDescription(groupDescription);
                    ModuleUtil.addModule(moduleVo);

                    ServletRegistration.Dynamic sr = context.addServlet(moduleId + "[" + moduleName + "] " + version, new CodedriverDispatcherServlet(moduleVo, appContext));
                    if (StringUtils.isNotBlank(urlMapping)) {
                        sr.addMapping(urlMapping);
                    }

                    /* 模块加载开始，计数器加一 **/
                    ModuleInitApplicationListener.getModuleinitphaser().register();
                    if (moduleId.equalsIgnoreCase("framework")) {
                        sr.addMapping("/");
                        sr.setLoadOnStartup(1);
                    } else {
                        sr.setLoadOnStartup(2);
                    }


                }
            }
        } catch (IOException | DocumentException ex) {
            ModuleInitApplicationListener.getModuleinitphaser().arrive();
            if (moduleId != null) {
                logger.error("初始化模块：" + moduleId + "失败", ex);
                System.out.println("failed to initialize module " + moduleId + ", please check error log");
            } else {
                logger.error(ex.getMessage(), ex);
            }
        }

    }
}
