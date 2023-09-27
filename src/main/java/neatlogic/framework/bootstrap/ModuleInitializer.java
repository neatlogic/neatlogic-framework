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
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleInitializer implements WebApplicationInitializer {
    static Logger logger = LoggerFactory.getLogger(ModuleInitializer.class);

    @Override
    public void onStartup(ServletContext context) throws ServletException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        System.out.println("    _   __              __   __                  _          _____    ____ \n" +
                "   / | / /___   ____ _ / /_ / /   ____   ____ _ (_)_____   |__  /   / __ \\\n" +
                "  /  |/ // _ \\ / __ `// __// /   / __ \\ / __ `// // ___/    /_ <   / / / /\n" +
                " / /|  //  __// /_/ // /_ / /___/ /_/ // /_/ // // /__    ___/ /_ / /_/ / \n" +
                "/_/ |_/ \\___/ \\__,_/ \\__//_____/\\____/ \\__, //_/ \\___/   /____/(_)\\____/  \n" +
                "                                      /____/                             \n" +
                "===========================================================================");
        List<ModuleVo> moduleListFromServletContext = getModuleListByServletContext(resolver);
        List<TenantVo> activeTenantList = getActiveTenantList();
        initDmlSql(resolver, activeTenantList, moduleListFromServletContext);
        updateChangeLogVersion(resolver, activeTenantList, moduleListFromServletContext);
        ModuleVo module = null;
        try {
            System.out.println("⚡" + I18nUtils.getStaticMessage("common.startloadmodule"));
            for (ModuleVo moduleFromServletContext : moduleListFromServletContext) {
                module = moduleFromServletContext;
                NeatLogicWebApplicationContext appContext = new NeatLogicWebApplicationContext();
                appContext.setConfigLocation("classpath*:" + module.getPath());
                appContext.setId(module.getId());
                appContext.setModuleId(module.getId());
                appContext.setModuleName(module.getNameWithoutTranslate());
                appContext.setGroupName(module.getGroupNameWithoutTranslate());
                appContext.setGroup(module.getGroup());
                ModuleUtil.addModule(module);
                ServletRegistration.Dynamic sr = context.addServlet(module.getId() + "[" + I18nUtils.getStaticMessage(module.getNameWithoutTranslate()) + "] " + module.getVersion(), new NeatLogicDispatcherServlet(module, appContext));
                if (StringUtils.isNotBlank(module.getUrlMapping())) {
                    sr.addMapping(module.getUrlMapping());
                }
                /* 模块加载开始，计数器加一 **/
                ModuleInitApplicationListener.getModuleinitphaser().register();
                if (module.getId().equalsIgnoreCase("framework")) {
                    sr.addMapping("/");
                    sr.setLoadOnStartup(1);
                } else {
                    sr.setLoadOnStartup(2);
                }
                System.out.println("  ✓" + module.getId() + "·" + I18nUtils.getStaticMessage(module.getNameWithoutTranslate()));
            }
        } catch (Exception ex) {
            ModuleInitApplicationListener.getModuleinitphaser().arrive();
            if (module != null) {
                System.out.println("  ✖" + module.getId() + "·" + I18nUtils.getStaticMessage(module.getNameWithoutTranslate()));
            }
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 根据每个模块的servlet context 获取模块列表
     */
    private List<ModuleVo> getModuleListByServletContext(ResourcePatternResolver resolver) {
        String moduleId = null;
        String moduleName = null;
        List<ModuleVo> moduleVoList = new ArrayList<>();
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
                String urlMapping, moduleDescription, version, group, groupName, groupSort, groupDescription;
                moduleId = neatlogicE.attributeValue("id");
                moduleName = neatlogicE.attributeValue("name");
                urlMapping = neatlogicE.attributeValue("urlMapping");
                moduleDescription = neatlogicE.attributeValue("description");
                group = neatlogicE.attributeValue("group");
                groupName = neatlogicE.attributeValue("groupName");
                groupSort = neatlogicE.attributeValue("groupSort");
                groupDescription = neatlogicE.attributeValue("groupDescription");
                version = Config.getProperty("META-INF/maven/com.neatlogic/neatlogic-" + moduleId + "/pom.properties", "version");
                moduleVoList.add(new ModuleVo(moduleId, moduleName, urlMapping, moduleDescription, version, group, groupName, groupSort, groupDescription, path));
            }
        } catch (IOException | DocumentException ex) {
            if (moduleId != null) {
                System.out.println("  ✖" + moduleId + "·" + I18nUtils.getStaticMessage(moduleName));
            }
            logger.error(ex.getMessage(), ex);
            System.exit(1);
        }
        return moduleVoList;
    }

    /**
     * 执行模块的dml
     */
    private void initDmlSql(ResourcePatternResolver resolver, List<TenantVo> activeTenantList, List<ModuleVo> moduleVoList) {
        String currentTenant = "";
        try {
            System.out.printf("⚡" + (I18nUtils.getStaticMessage("nfb.moduleinitializer.initdmlsql.tenant")) + "%n");
            for (TenantVo tenantVo : activeTenantList) {
                currentTenant = tenantVo.getName();
                for (ModuleVo moduleVo : moduleVoList) {
                    Resource[] dmlResources = resolver.getResources("classpath*:neatlogic/resources/" + moduleVo.getId() + "/sqlscript/dml.sql");
                    if (dmlResources.length == 1) {
                        Resource dmlResource = dmlResources[0];
                        Reader scriptReader = new InputStreamReader(dmlResource.getInputStream());
                        ScriptRunnerManager.runScriptOnceWithJdbc(tenantVo, moduleVo.getId(), scriptReader, false, "sqlscript·dml");
                    }
                }
                System.out.println("  ✓" + tenantVo.getName());
            }
        } catch (IOException ex) {
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

    /**
     * 获取激活租户的对应模块版本
     *
     * @param activeTenantList 激活的租户
     * @return 激活租户对应的模块版本
     */
    private Map<String, Map<String, String>> getTenantModuleVersionMap(List<TenantVo> activeTenantList) {
        Connection connection = null;
        PreparedStatement tenantGroupStatement = null;
        ResultSet tenantGroupResultSet = null;
        Map<String, Map<String, String>> tenantModuleGroupMap = new HashMap<>();
        try {
            DataSource datasource = JdbcUtil.getNeatlogicDataSource();
            try {
                connection = datasource.getConnection();
            } catch (Exception exception) {
                System.out.println("ERROR: " + I18nUtils.getStaticMessage("nfb.moduleinitializer.getactivetenantlist.neatlogicdb"));
                System.exit(1);
            }
            String tenantGroupSql = "SELECT a.* FROM tenant_module a where a.tenant_uuid in (?) ";

            List<String> activeTenantUuidList = activeTenantList.stream().map(TenantVo::getUuid).collect(Collectors.toList());
            tenantGroupStatement = connection.prepareStatement(tenantGroupSql);
            tenantGroupStatement.setString(1, String.join(",", activeTenantUuidList));
            tenantGroupResultSet = tenantGroupStatement.executeQuery();
            while (tenantGroupResultSet.next()) {
                tenantModuleGroupMap.computeIfAbsent(tenantGroupResultSet.getString("tenant_uuid"), k -> new HashMap<>()).put(tenantGroupResultSet.getString("module_id"), tenantGroupResultSet.getString("version"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.closeConnection(connection);
            JdbcUtil.closeStatement(tenantGroupStatement);
            JdbcUtil.closeResultSet(tenantGroupResultSet);
        }
        return tenantModuleGroupMap;
    }

    /**
     * 检查并更新数据库ddl和dml版本
     */
    private void updateChangeLogVersion(ResourcePatternResolver resolver, List<TenantVo> activeTenantList, List<ModuleVo> moduleVoList) {
        System.out.println("⚡" + "开始初始化版本更新...");
        try {
            List<String> errorList = new ArrayList<>();
            Map<String, List<String>> moduleVersionListMap = new HashMap<>();
            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeUtil.YYYY_MM_DD);
            // 定义比较器
            Comparator<String> fileNameComparator = Comparator.comparing((String fileName) -> LocalDate.parse(fileName, formatter)).reversed();
            //获取最新版本
            Map<String, String> moduleLatestVersionMap = new HashMap<>();
            //检查changelog下合法版本
            Resource[] resources = resolver.getResources("classpath*:neatlogic/resources/*/changelog/*/");
            for (Resource resource : resources) {
                //目前
                String fileName = resource.getURL().getPath().substring(0, resource.getURL().getPath().lastIndexOf("/"));
                String version = fileName.substring(fileName.lastIndexOf("/") + 1);
                if (!Objects.equals("changelog", version) && StringUtils.isNotBlank(version)) {
                    String path = resource.getURL().getPath();
                    path = path.substring(path.indexOf("!") + 1);
                    if (StringUtils.isNotBlank(path)) {
                        String moduleId = path.split("/")[3];
                        if (version.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            moduleVersionListMap.computeIfAbsent(moduleId, k -> new ArrayList<>()).add(version);
                        } else {
                            errorList.add(path);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(errorList)) {
                for (String path : errorList) {
                    System.out.println(I18nUtils.getStaticMessage("nfb.moduleinitializer.checkchangelog.invalid", path, TimeUtil.YYYY_MM_DD));
                }
                System.exit(1);
            }
            //获取模块最新版本
            if (MapUtils.isNotEmpty(moduleVersionListMap)) {
                for (Map.Entry<String, List<String>> entry : moduleVersionListMap.entrySet()) {
                    entry.getValue().sort(fileNameComparator);
                    moduleLatestVersionMap.put(entry.getKey(), entry.getValue().get(0));
                }
            }

            Map<String, Map<String, String>> tenantModuleVersionMap = getTenantModuleVersionMap(activeTenantList);
            //循环需要执行的所有模块当前版本
            for (TenantVo activeTenant : activeTenantList) {
                Map<String, String> moduleVersionMap = tenantModuleVersionMap.get(activeTenant.getUuid());
                if (tenantModuleVersionMap.containsKey(activeTenant.getUuid())) {
                    for (ModuleVo moduleVo : moduleVoList) {
                        String moduleId = moduleVo.getId();
                        String latestVersion = moduleLatestVersionMap.get(moduleId);
                        //第一次启用基线。 即该租户该模块没有版本基线，则直接更新版本基线，不执行sql，启动服务后需要手动更新对比schema后重启tomcat实例服务
                        if (!moduleVersionMap.containsKey(moduleId) || StringUtils.isBlank(moduleVersionMap.get(moduleId)) || moduleVersionMap.get(moduleId) == null) {
                            if (StringUtils.isBlank(latestVersion)) {
                                //其他没有最新版本日志的模块则选择昨天作为最新版本，为了后续自动更新版本
                                latestVersion = TimeUtil.descDateStr(new Date(), 1, TimeUtil.YYYY_MM_DD);
                            }
                            insertTenantModuleVersionSql(activeTenant.getUuid(), moduleId, latestVersion);
                            //如果模块版本小于最新版本，则执行sql并更新为最新版本
                        } else if (StringUtils.isNotBlank(latestVersion) && LocalDate.parse(latestVersion).toEpochDay() > LocalDate.parse(moduleVersionMap.get(moduleId)).toEpochDay()) {
                            Resource ddlResource = null;
                            Resource[] ddlResources = resolver.getResources("classpath*:neatlogic/resources/" + moduleId + "/changelog/" + latestVersion + "/dll.sql");
                            if (ddlResources.length == 1) {
                                StringWriter logStrWriter = new StringWriter();
                                PrintWriter logWriter = new PrintWriter(logStrWriter);
                                StringWriter errStrWriter = new StringWriter();
                                PrintWriter errWriter = new PrintWriter(errStrWriter);
                                ddlResource = ddlResources[0];
                                Reader scriptReader = new InputStreamReader(ddlResource.getInputStream());
                                ScriptRunnerManager.runScriptWithJdbc(activeTenant, moduleId, scriptReader, logWriter, errWriter, false);
                                if (StringUtils.isNotBlank(errStrWriter.toString())) {
                                    System.out.println("  ✖" + activeTenant.getName() + "·" + moduleId+"."+latestVersion);
                                    System.out.println(errStrWriter);
                                    System.exit(1);
                                }
                            }
                            Resource dmlResource;
                            Resource[] dmlResources = resolver.getResources("classpath*:neatlogic/resources/" + moduleId + "/changelog/" + latestVersion + "/dml.sql");
                            if (dmlResources.length == 1) {
                                dmlResource = dmlResources[0];
                                Reader scriptReader = new InputStreamReader(dmlResource.getInputStream());
                                ScriptRunnerManager.runScriptOnceWithJdbc(activeTenant, moduleId, scriptReader, false, "changelog·"+latestVersion+"·dml");
                            }
                            insertTenantModuleVersionSql(activeTenant.getUuid(), moduleId, latestVersion);
                            System.out.println("  ✓" + activeTenant.getName() + "·" + moduleId);
                        }
                    }
                } else {
                    //第一次启用基线。 即该租户所有模块没有版本基线，则直接更新版本基线，不执行sql，启动服务后需要手动更新对比schema后重启tomcat实例服务
                    for (ModuleVo moduleVo : moduleVoList) {
                        String latestVersion = moduleLatestVersionMap.get(moduleVo.getId());
                        if (StringUtils.isBlank(latestVersion)) {
                            //其他没有最新版本日志的模块则选择昨天作为最新版本，为了后续自动更新版本
                            latestVersion = TimeUtil.descDateStr(new Date(), 1, TimeUtil.YYYY_MM_DD);
                        }
                        insertTenantModuleVersionSql(activeTenant.getUuid(), moduleVo.getId(), latestVersion);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 插入租户模块信息
     */
    private void insertTenantModuleVersionSql(String tenantUuid, String moduleId, String version) throws Exception {
        PreparedStatement statement = null;
        java.sql.Connection neatlogicConn = null;
        try {
            String sql = "insert into `tenant_module` (`tenant_uuid`,`module_id`,`version`,`fcd`,`lcd`) VALUES (?,?,?,now(),now()) ON DUPLICATE KEY UPDATE version = ?,`lcd` = now()";
            neatlogicConn = JdbcUtil.getNeatlogicDataSource().getConnection();
            statement = neatlogicConn.prepareStatement(sql);
            statement.setString(1, tenantUuid);
            statement.setString(2, moduleId);
            statement.setString(3, version);
            statement.setString(4, version);
            statement.execute();
        } catch (Exception ex) {
            logger.error(String.format("租户%s初始化更新版本失败", tenantUuid));
            logger.error(ex.getMessage(), ex);
        } finally {
            JdbcUtil.closeConnection(neatlogicConn);
            JdbcUtil.closeStatement(statement);
        }
    }
}
