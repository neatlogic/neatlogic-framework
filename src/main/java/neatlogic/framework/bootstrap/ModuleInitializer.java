/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        System.out.println("    _   __              __   __                  _          _____    ____ \n" +
                "   / | / /___   ____ _ / /_ / /   ____   ____ _ (_)_____   |__  /   / __ \\\n" +
                "  /  |/ // _ \\ / __ `// __// /   / __ \\ / __ `// // ___/    /_ <   / / / /\n" +
                " / /|  //  __// /_/ // /_ / /___/ /_/ // /_/ // // /__    ___/ /_ / /_/ / \n" +
                "/_/ |_/ \\___/ \\__,_/ \\__//_____/\\____/ \\__, //_/ \\___/   /____/(_)\\____/  \n" +
                "                                      /____/                             \n" +
                "===============================================================================================");
        try {
            Resource[] resources = resolver.getResources("classpath*:neatlogic/**/*-servlet-context.xml");
            for (Resource resource : resources) {
                String path = resource.getURL().getPath();
                path = path.substring(path.indexOf("!") + 1);
                SAXReader reader = new SAXReader();
                Document document = reader.read(resource.getURL());
                Element rootE = document.getRootElement();
                Element neatlogicE = rootE.element("module");
                // Element nameE = rootE.element("name");
                String moduleName = null, urlMapping = null, moduleDescription = null, version = null, group = null, groupName = null, groupSort = null, groupDescription = null;
                moduleId = neatlogicE.attributeValue("id");
                moduleName = neatlogicE.attributeValue("name");
                urlMapping = neatlogicE.attributeValue("urlMapping");
                moduleDescription = neatlogicE.attributeValue("description");
                group = neatlogicE.attributeValue("group");
                groupName = neatlogicE.attributeValue("groupName");
                groupSort = neatlogicE.attributeValue("groupSort");
                groupDescription = neatlogicE.attributeValue("groupDescription");

                if (StringUtils.isNotBlank(moduleId)) {
                    System.out.println("start to initialize module " + moduleId + "...");
                    version = Config.getProperty("META-INF/maven/com.neatlogic/neatlogic-" + moduleId + "/pom.properties", "version");
                    NeatLogicWebApplicationContext appContext = new NeatLogicWebApplicationContext();
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

                    ServletRegistration.Dynamic sr = context.addServlet(moduleId + "[" + moduleName + "] " + version, new NeatLogicDispatcherServlet(moduleVo, appContext));
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
