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
import neatlogic.framework.dto.DatasourceVo;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.sqlfile.ScriptRunnerManager;
import neatlogic.framework.util.I18nUtils;
import neatlogic.framework.util.JdbcUtil;
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
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ModuleInitializer implements WebApplicationInitializer {
    static Logger logger = LoggerFactory.getLogger(ModuleInitializer.class);

    @Override
    public void onStartup(ServletContext context) throws ServletException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String moduleId = null;
        String moduleName = null;
        System.out.println("    _   __              __   __                  _          _____    ____ \n" +
                "   / | / /___   ____ _ / /_ / /   ____   ____ _ (_)_____   |__  /   / __ \\\n" +
                "  /  |/ // _ \\ / __ `// __// /   / __ \\ / __ `// // ___/    /_ <   / / / /\n" +
                " / /|  //  __// /_/ // /_ / /___/ /_/ // /_/ // // /__    ___/ /_ / /_/ / \n" +
                "/_/ |_/ \\___/ \\__,_/ \\__//_____/\\____/ \\__, //_/ \\___/   /____/(_)\\____/  \n" +
                "                                      /____/                             \n" +
                "===========================================================================");

        initDmlSql(resolver);
        try {
            Resource[] resources = resolver.getResources("classpath*:neatlogic/**/*-servlet-context.xml");
            System.out.println("⚡" + I18nUtils.getStaticMessage("common.startloadmodule"));
            for (Resource resource : resources) {
                String path = resource.getURL().getPath();
                path = path.substring(path.indexOf("!") + 1);
                SAXReader reader = new SAXReader();
                Document document = reader.read(resource.getURL());
                Element rootE = document.getRootElement();
                Element neatlogicE = rootE.element("module");
                // Element nameE = rootE.element("name");
                String urlMapping, moduleDescription, version, group, groupName, groupSort, groupDescription;
                moduleId = neatlogicE.attributeValue("id");
                moduleName = neatlogicE.attributeValue("name");
                urlMapping = neatlogicE.attributeValue("urlMapping");
                moduleDescription = neatlogicE.attributeValue("description");
                group = neatlogicE.attributeValue("group");
                groupName = neatlogicE.attributeValue("groupName");
                groupSort = neatlogicE.attributeValue("groupSort");
                groupDescription = neatlogicE.attributeValue("groupDescription");

                if (StringUtils.isNotBlank(moduleId)) {
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

                    ServletRegistration.Dynamic sr = context.addServlet(moduleId + "[" + I18nUtils.getStaticMessage(moduleName) + "] " + version, new NeatLogicDispatcherServlet(moduleVo, appContext));
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
                    System.out.println("  ✓" + moduleId + "·" + I18nUtils.getStaticMessage(moduleName));
                }
            }
        } catch (IOException | DocumentException ex) {
            ModuleInitApplicationListener.getModuleinitphaser().arrive();
            if (moduleId != null) {
                System.out.println("  ✖" + moduleId + "·" + I18nUtils.getStaticMessage(moduleName));
            }
            logger.error(ex.getMessage(), ex);
        }

    }

    /**
     * 执行模块的dml
     */
    private void initDmlSql(ResourcePatternResolver resolver) {
        String currentTenant = "";
        try {
            Resource[] resources = resolver.getResources("classpath*:neatlogic/**/*-servlet-context.xml");
            List<TenantVo> activeTenantList = getActiveTenantList();
            System.out.printf("⚡" + (I18nUtils.getStaticMessage("nfb.moduleinitializer.initdmlsql.tenant")) + "%n");
            for (TenantVo tenantVo : activeTenantList) {
                currentTenant = tenantVo.getName();
                for (Resource resource : resources) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(resource.getURL());
                    Element rootE = document.getRootElement();
                    Element neatlogicE = rootE.element("module");
                    String moduleId = neatlogicE.attributeValue("id");
                    String moduleName = neatlogicE.attributeValue("name");

                    Resource dmlResource = null;

                    Resource[] dmlResources = resolver.getResources("classpath*:neatlogic/resources/" + moduleId + "/sqlscript/dml.sql");
                    if (dmlResources.length == 1) {
                        dmlResource = dmlResources[0];
                        Reader scriptReader = new InputStreamReader(dmlResource.getInputStream());
                        ScriptRunnerManager.runScriptOnceWithJdbc(tenantVo, moduleId, scriptReader, false);

                    }
                }
                System.out.println("  ✓" + tenantVo.getName());
            }
        } catch (IOException | DocumentException ex) {
            if (StringUtils.isNotBlank(currentTenant)) {
                System.out.println("  ✖" + currentTenant);
            }
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取所有激活租户
     *
     * @return 激活的租户
     */
    private List<TenantVo> getActiveTenantList() {
        List<TenantVo> activeTenantList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement tenantStatement = null;
        ResultSet tenantResultSet = null;
        try {
            DataSource datasource = JdbcUtil.getNeatlogicDataSource();
            try {
                connection = datasource.getConnection();
            } catch (Exception exception) {
                System.out.println("ERROR: " + I18nUtils.getStaticMessage("nfb.moduleinitializer.getactivetenantlist.neatlogicdb"));
                System.exit(1);
            }
            String tenantSql = "SELECT a.*,b.* FROM tenant a left join datasource b on a.uuid = b.tenant_uuid where a.is_active = 1 ";
            tenantStatement = connection.prepareStatement(tenantSql);
            tenantResultSet = tenantStatement.executeQuery();
            while (tenantResultSet.next()) {
                TenantVo tenantVo = new TenantVo();
                tenantVo.setUuid(tenantResultSet.getString("uuid"));
                tenantVo.setName(tenantResultSet.getString("name"));
                DatasourceVo datasourceVo = new DatasourceVo();
                datasourceVo.setUrl(tenantResultSet.getString("url"));
                datasourceVo.setUsername(tenantResultSet.getString("username"));
                datasourceVo.setPasswordCipher(tenantResultSet.getString("password"));
                datasourceVo.setDriver(tenantResultSet.getString("driver"));
                datasourceVo.setHost(tenantResultSet.getString("host"));
                datasourceVo.setPort(tenantResultSet.getInt("port"));
                tenantVo.setDatasource(datasourceVo);
                activeTenantList.add(tenantVo);
            }
            //System.out.println("激活的租户:" + activeTenantList.stream().map(o -> o.getName() + "[" + o.getUuid() + "]").collect(Collectors.joining("、")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.closeConnection(connection);
            JdbcUtil.closeStatement(tenantStatement);
            JdbcUtil.closeResultSet(tenantResultSet);
        }
        return activeTenantList;
    }
}
