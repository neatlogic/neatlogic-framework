/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.bootstrap;

import neatlogic.framework.asynchronization.thread.ModuleInitApplicationListener;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.DatasourceVo;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import neatlogic.framework.util.I18nUtils;
import neatlogic.framework.util.JdbcUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
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
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ModuleInitializer implements WebApplicationInitializer {
    static Logger logger = LoggerFactory.getLogger(ModuleInitializer.class);

    @Override
    public void onStartup(ServletContext context) throws ServletException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //System.out.println("    _   __              __   __                  _          _____    ____ \n" + "   / | / /___   ____ _ / /_ / /   ____   ____ _ (_)_____   |__  /   / __ \\\n" + "  /  |/ // _ \\ / __ `// __// /   / __ \\ / __ `// // ___/    /_ <   / / / /\n" + " / /|  //  __// /_/ // /_ / /___/ /_/ // /_/ // // /__    ___/ /_ / /_/ / \n" + "/_/ |_/ \\___/ \\__,_/ \\__//_____/\\____/ \\__, //_/ \\___/   /____/(_)\\____/  \n" + "                                      /____/                             \n" + "===========================================================================");
        /*System.out.println("░▒▓███████▓▒░░▒▓████████▓▒░░▒▓██████▓▒░▒▓████████▓▒░▒▓█▓▒░      ░▒▓██████▓▒░ ░▒▓██████▓▒░░▒▓█▓▒░░▒▓██████▓▒░▒▓███████▓▒░       ░▒▓████████▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░     ░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░▒▓█▓▒░            ░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓██████▓▒░ ░▒▓████████▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒▒▓███▓▒░▒▓█▓▒░▒▓█▓▒░     ░▒▓███████▓▒░       ░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░            ░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░     ░▒▓█▓▒░▒▓██▓▒░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓████████▓▒░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓████████▓▒░▒▓██████▓▒░ ░▒▓██████▓▒░░▒▓█▓▒░░▒▓██████▓▒░▒▓███████▓▒░░▒▓██▓▒░▒▓████████▓▒░ \n" +
                "                                                                                                                                              ");*/
        System.out.println("███╗   ██╗███████╗ █████╗ ████████╗██╗      ██████╗  ██████╗ ██╗ ██████╗    ██████╗     ██████╗ \n" +
                "████╗  ██║██╔════╝██╔══██╗╚══██╔══╝██║     ██╔═══██╗██╔════╝ ██║██╔════╝    ╚════██╗   ██╔═████╗\n" +
                "██╔██╗ ██║█████╗  ███████║   ██║   ██║     ██║   ██║██║  ███╗██║██║          █████╔╝   ██║██╔██║\n" +
                "██║╚██╗██║██╔══╝  ██╔══██║   ██║   ██║     ██║   ██║██║   ██║██║██║          ╚═══██╗   ████╔╝██║\n" +
                "██║ ╚████║███████╗██║  ██║   ██║   ███████╗╚██████╔╝╚██████╔╝██║╚██████╗    ██████╔╝██╗╚██████╔╝\n" +
                "╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝   ╚═╝   ╚══════╝ ╚═════╝  ╚═════╝ ╚═╝ ╚═════╝    ╚═════╝ ╚═╝ ╚═════╝ \n" +
                "                                                                                                ");
        //生成地址:http://patorjk.com/software/taag/#p=display&v=1&f=ANSI%20Shadow&t=neatlogic%203.0
        ModuleVo module = null;
        try {
            List<ModuleVo> moduleListFromServletContext = getModuleListByServletContext(resolver);
            List<TenantVo> activeTenantList = getAllTenantList();
            //ChangelogUtil.updateChangeLogVersion(resolver, activeTenantList, moduleListFromServletContext);
            //ChangelogUtil.initDmlSql(resolver, activeTenantList, moduleListFromServletContext);
            System.out.println("⚡" + I18nUtils.getStaticMessage("common.startloadmodule"));
            List<ModuleVo> parentModuleList = moduleListFromServletContext.stream().filter(d -> d.getParent() == null).collect(Collectors.toList());
            List<ModuleVo> childModuleList = moduleListFromServletContext.stream().filter(d -> d.getParent() != null).collect(Collectors.toList());
            Set<String> successLoadedParentSet = new HashSet<>();
            for (ModuleVo moduleFromServletContext : parentModuleList) {
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
                successLoadedParentSet.add(module.getId());
                module = null;
            }
            for (ModuleVo childModule : childModuleList) {
                if (successLoadedParentSet.contains(childModule.getParent())) {
                    ModuleUtil.addModule(childModule);
                    if (!childModule.isCommercial()) {
                        //商业模块需要通过license校验后自行打印成功加载信息
                        System.out.println("  ✓" + childModule.getId() + "·" + I18nUtils.getStaticMessage(childModule.getNameWithoutTranslate()));
                    }
                }
            }
        } catch (ModuleInitRuntimeException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            if (ModuleInitApplicationListener.getModuleinitphaser().getRegisteredParties() > 0) {
                ModuleInitApplicationListener.getModuleinitphaser().arriveAndDeregister();
            }
            logger.error(ex.getMessage(), ex);
        } finally {
            if (module != null) {
                System.out.println("  ✖" + module.getId() + "·" + I18nUtils.getStaticMessage(module.getNameWithoutTranslate()));
            }
        }
    }

    /**
     * 根据每个模块的servlet context 获取模块列表
     */
    private List<ModuleVo> getModuleListByServletContext(ResourcePatternResolver resolver) throws Exception {
        List<ModuleVo> moduleVoList = new ArrayList<>();
        Resource[] resources = resolver.getResources("classpath*:neatlogic/**/*-servlet-context.xml");
        for (Resource resource : resources) {
            String path = resource.getURL().getPath();
//            path = path.substring(path.indexOf("!") + 1);
            path = path.substring(path.lastIndexOf("/neatlogic/") + 1);
            SAXReader reader = new SAXReader();
            Document document = reader.read(resource.getURL());
            Element rootE = document.getRootElement();
            Element neatlogicE = rootE.element("module");
            String parent = neatlogicE.attributeValue("parent");
            boolean isCommercial = false;
            if (StringUtils.isNotBlank(neatlogicE.attributeValue("isCommercial"))) {
                isCommercial = Boolean.parseBoolean(neatlogicE.attributeValue("isCommercial"));
            }
            String moduleId, moduleName, urlMapping, moduleDescription, version, group, groupName, groupSort, groupDescription;
            moduleId = neatlogicE.attributeValue("id");
            moduleName = neatlogicE.attributeValue("name");
            urlMapping = neatlogicE.attributeValue("urlMapping");
            moduleDescription = neatlogicE.attributeValue("description");
            group = neatlogicE.attributeValue("group");
            groupName = neatlogicE.attributeValue("groupName");
            groupSort = neatlogicE.attributeValue("groupSort");
            groupDescription = neatlogicE.attributeValue("groupDescription");
            ModuleVo moduleVo = new ModuleVo(moduleId, moduleName, urlMapping, moduleDescription, group, groupName, groupSort, groupDescription, path, parent, isCommercial);
            moduleVoList.add(moduleVo);
            setVersionAndLastModified(moduleVo);
        }
        return moduleVoList;
    }

    /**
     * 根据context.xml path 获取 pom.properties path
     * 并设置模块的版本和最后修改时间
     */
    private void setVersionAndLastModified(ModuleVo moduleVo) {
        // 获取资源的URL
        String pomPropertiesPath = null;
        URL resourceUrl = Config.class.getClassLoader().getResource(moduleVo.getPath());
        if (resourceUrl != null) {
            try {
                // 如果资源在JAR文件中，获取JAR文件的URL
                String jarUrl = resourceUrl.toString().replaceFirst("jar:file:", "").replaceFirst("!.*", "");
                File jar = new File(jarUrl);
                if (jar.exists()) {
                    moduleVo.setLastModified(new Date(jar.lastModified()));
                }
                try (JarFile jarFile = new JarFile(jarUrl)) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.endsWith("pom.properties")) {
                            pomPropertiesPath = entryName;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (StringUtils.isBlank(pomPropertiesPath)) {
            pomPropertiesPath = "META-INF/maven/com.neatlogic/neatlogic-" + moduleVo.getId() + "/pom.properties";
        }
        moduleVo.setVersion(Config.getProperty(pomPropertiesPath, "version"));
    }

    /**
     * 从数据库查询所有激活租户
     *
     * @return 激活的租户
     */
    private List<TenantVo> getAllTenantList() throws Exception {
        List<TenantVo> activeTenantList = new ArrayList<>();
        Connection neatlogicConn = null;
        PreparedStatement tenantStatement = null;
        ResultSet tenantResultSet = null;
        try {
            neatlogicConn = JdbcUtil.getNeatlogicConnection();
            String tenantSql = "SELECT a.*,b.* FROM tenant a left join datasource b on a.uuid = b.tenant_uuid where a.is_active =1 ";
            tenantStatement = neatlogicConn.prepareStatement(tenantSql);
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
        } catch (Throwable ex) {
            logger.error("从数据库查询所有激活租户时发生异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            JdbcUtil.closeResultSet(tenantResultSet);
            JdbcUtil.closeStatement(tenantStatement);
            JdbcUtil.closeConnection(neatlogicConn);
        }
        return activeTenantList;
    }
}
