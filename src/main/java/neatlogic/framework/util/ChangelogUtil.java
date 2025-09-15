/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.util;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.DatasourceVo;
import neatlogic.framework.dto.ExecuteSqlParamVo;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.sqlfile.ScriptRunnerManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ChangelogUtil {
    static Logger logger = LoggerFactory.getLogger(ChangelogUtil.class);


    /**
     * 根据每个模块的servlet context 获取模块列表
     */
    public static List<ModuleVo> getModuleListByServletContext(ResourcePatternResolver resolver) throws Exception {
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
            ChangelogUtil.setVersionAndLastModified(moduleVo);
        }
        return moduleVoList;
    }

    /**
     * 根据context.xml path 获取 pom.properties path
     * 并设置模块的版本和最后修改时间
     */
    public static void setVersionAndLastModified(ModuleVo moduleVo) {
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
     * @param allTenantList 所有激活的租户
     * @return 租户对应已执行过的sql hash
     */
    public static Map<String, List<String>> getAllActiveTenantChangelogSqlHashMap(List<TenantVo> allTenantList, Connection connection) throws Exception {
        Map<String, List<String>> allTenantChangelogSqlHashMap = new HashMap<>();
        List<String> tenantUuidList = allTenantList.stream().map(TenantVo::getUuid).collect(Collectors.toList());
        String placeholders = tenantUuidList.stream().map(id -> "?").collect(Collectors.joining(","));
        placeholders = placeholders + " , ?";
        ResultSet sqlMd5ResultSet = null;
        try (PreparedStatement sqlMd5Statement = connection.prepareStatement("select sql_hash,`tenant_uuid` from changelog_audit where `tenant_uuid` in (" + placeholders + ")  and (`sql_status` = 1 or `ignored` = 1) order by tenant_uuid")) {
            for (int i = 0; i < tenantUuidList.size(); i++) {
                sqlMd5Statement.setString(i + 1, tenantUuidList.get(i));
            }
            sqlMd5Statement.setString(tenantUuidList.size() + 1, "0");
            sqlMd5ResultSet = sqlMd5Statement.executeQuery();
            while (sqlMd5ResultSet.next()) {
                String tenantUuid = sqlMd5ResultSet.getString("tenant_uuid");
                List<String> sqlHashList;
                if (!allTenantChangelogSqlHashMap.containsKey(tenantUuid)) {
                    sqlHashList = new ArrayList<>();
                    allTenantChangelogSqlHashMap.put(tenantUuid, sqlHashList);
                } else {
                    sqlHashList = allTenantChangelogSqlHashMap.get(tenantUuid);
                }

                sqlHashList.add(sqlMd5ResultSet.getString("sql_hash"));
            }
            return allTenantChangelogSqlHashMap;
        } catch (Exception ex) {
            logger.error("查询租户changlog_audit时发生异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            if (sqlMd5ResultSet != null) {
                sqlMd5ResultSet.close();
            }
        }
    }

    /**
     * @param allTenantList 所有激活的租户
     * @return 租户对应已执行过的dml sql hash
     */
    public static Map<String, List<String>> getAllActiveTenantDmlSqlHashMap(List<TenantVo> allTenantList, Connection connection) throws Exception {
        Map<String, List<String>> allTenantDmlSqlHashMap = new HashMap<>();
        List<String> tenantUuidList = allTenantList.stream().map(TenantVo::getUuid).collect(Collectors.toList());
        String placeholders = tenantUuidList.stream().map(id -> "?").collect(Collectors.joining(","));
        placeholders = placeholders + " , ?";
        ResultSet sqlMd5ResultSet = null;
        try (PreparedStatement sqlMd5Statement = connection.prepareStatement("select sql_uuid,`tenant_uuid` from tenant_module_dmlsql where `tenant_uuid` in (" + placeholders + ")  and (`sql_status` = 1 or `ignored` = 1) order by tenant_uuid")) {
            for (int i = 0; i < tenantUuidList.size(); i++) {
                sqlMd5Statement.setString(i + 1, tenantUuidList.get(i));
            }
            sqlMd5Statement.setString(tenantUuidList.size() + 1, "0");
            sqlMd5ResultSet = sqlMd5Statement.executeQuery();
            while (sqlMd5ResultSet.next()) {
                String tenantUuid = sqlMd5ResultSet.getString("tenant_uuid");
                List<String> sqlHashList;
                if (!allTenantDmlSqlHashMap.containsKey(tenantUuid)) {
                    sqlHashList = new ArrayList<>();
                    allTenantDmlSqlHashMap.put(tenantUuid, sqlHashList);
                } else {
                    sqlHashList = allTenantDmlSqlHashMap.get(tenantUuid);
                }

                sqlHashList.add(sqlMd5ResultSet.getString("sql_uuid"));
            }
            return allTenantDmlSqlHashMap;
        } catch (Exception ex) {
            logger.error("查询租户tenant_module_dmlsql时发生异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            if (sqlMd5ResultSet != null) {
                sqlMd5ResultSet.close();
            }
        }
    }

    /**
     * 从数据库查询所有激活租户
     *
     * @return 激活的租户
     */
    public static List<TenantVo> getAllTenantList(Connection connection) throws Exception {
        List<TenantVo> activeTenantList = new ArrayList<>();
        try (PreparedStatement tenantStatement = connection.prepareStatement("SELECT a.*,b.* FROM tenant a left join datasource b on a.uuid = b.tenant_uuid where a.is_active =1 "); ResultSet tenantResultSet = tenantStatement.executeQuery();) {
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
        }
        return activeTenantList;
    }


    /**
     * 执行模块的dml
     */
    public static void initDmlSql(ResourcePatternResolver resolver, List<TenantVo> activeTenantList, List<ModuleVo> moduleVoList, Connection neatlogicConn) throws Exception {
        System.out.printf("⚡" + (I18nUtils.getStaticMessage("nfb.moduleinitializer.initdmlsql.tenant")) + "%n");
        Map<String, List<String>> allActiveTenantDmlSqlHashMap = ChangelogUtil.getAllActiveTenantDmlSqlHashMap(activeTenantList, neatlogicConn);
        boolean isError = false;
        for (TenantVo tenantVo : activeTenantList) {
            try (Connection tenantConnection = JdbcUtil.getNeatlogicTenantConnection(tenantVo, false)) {
                for (ModuleVo moduleVo : moduleVoList) {
                    Resource[] dmlResources = resolver.getResources("classpath*:neatlogic/resources/" + moduleVo.getId() + "/**/sqlscript/dml.sql");
                    for (Resource resource : dmlResources) {
                        List<String> tenantDmlSqlList = allActiveTenantDmlSqlHashMap.get(tenantVo.getUuid());
                        if (CollectionUtils.isEmpty(tenantDmlSqlList)) {
                            tenantDmlSqlList = new ArrayList<>();
                        }
                        ExecuteSqlParamVo executeSqlParamVo = new ExecuteSqlParamVo(tenantVo, moduleVo.getId(), resource, tenantDmlSqlList, tenantConnection, neatlogicConn, false);
                        ScriptRunnerManager.runDmlScriptWithJdbc(executeSqlParamVo);
                        if (executeSqlParamVo.isError()) {
                            isError = true;
                        }
                    }
                }
                System.out.println("  ✓" + tenantVo.getName());
            }
        }
//        if (isError) {
//            System.exit(1);
//        }
    }

    /**
     * 检查并更新数据库ddl和dml版本
     */
    public static void updateChangeLogVersion(ResourcePatternResolver resolver, List<TenantVo> activeTenantList, List<ModuleVo> moduleVoList, Connection neatlogicConn) throws Exception {
        System.out.println("⚡" + "开始初始化版本更新...");
        List<String> errorList = new ArrayList<>();
        Map<String, List<String>> moduleVersionListMap = new HashMap<>();
        //检查changelog下合法版本
        Resource[] resources = resolver.getResources("classpath*:neatlogic/resources/**/changelog/*/*");
        for (Resource resource : resources) {
            //目前
            String fileName = resource.getURL().getPath().substring(0, resource.getURL().getPath().lastIndexOf("/"));
            String version = fileName.substring(fileName.lastIndexOf("/") + 1);
            if (!Objects.equals("changelog", version) && StringUtils.isNotBlank(version)) {
                String path = resource.getURL().getPath();
//                path = path.substring(path.indexOf("!") + 1);
                path = path.substring(path.lastIndexOf("/neatlogic/resources/"));
                if (StringUtils.isNotBlank(path)) {
                    String moduleId = path.split("/")[3];
                    if (version.matches("\\d{4}-\\d{2}-\\d{2}(-\\d{2})?")) {
                        List<String> list = moduleVersionListMap.computeIfAbsent(moduleId, k -> new ArrayList<>());
                        if (!list.contains(version)) {
                            list.add(version);
                        }
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
            //System.exit(1);
        }
        Map<String, List<String>> allActiveTenantChangelogSqlHashMap = ChangelogUtil.getAllActiveTenantChangelogSqlHashMap(activeTenantList, neatlogicConn);
        updateNeatlogicDatabase(resolver, allActiveTenantChangelogSqlHashMap, neatlogicConn);
        updateTenantDatabase(resolver, moduleVersionListMap, activeTenantList, moduleVoList, allActiveTenantChangelogSqlHashMap, neatlogicConn);
    }

    /**
     * 获取激活租户的对应模块版本
     *
     * @param activeTenantList 激活的租户
     * @return 激活租户对应的模块版本
     */
    private static Map<String, Map<String, String>> getTenantModuleVersionMap(List<TenantVo> activeTenantList, Connection neatlogicConn) throws Exception {
        PreparedStatement tenantGroupStatement = null;
        ResultSet tenantGroupResultSet = null;
        Map<String, Map<String, String>> tenantModuleGroupMap = new HashMap<>();
        try {
            List<String> activeTenantUuidList = activeTenantList.stream().map(TenantVo::getUuid).collect(Collectors.toList());
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < activeTenantUuidList.size(); i++) {
                placeholders.append("?");
                if (i < activeTenantUuidList.size() - 1) {
                    placeholders.append(",");
                }
            }
            String tenantGroupSql = "SELECT a.* FROM tenant_module a where a.tenant_uuid in (" + placeholders + ") ";
            tenantGroupStatement = neatlogicConn.prepareStatement(tenantGroupSql);
            for (int i = 1; i <= activeTenantUuidList.size(); i++) {
                tenantGroupStatement.setString(i, activeTenantUuidList.get(i - 1));
            }
            tenantGroupResultSet = tenantGroupStatement.executeQuery();
            while (tenantGroupResultSet.next()) {
                tenantModuleGroupMap.computeIfAbsent(tenantGroupResultSet.getString("tenant_uuid"), k -> new HashMap<>()).put(tenantGroupResultSet.getString("module_id"), tenantGroupResultSet.getString("version"));
            }
        } catch (Exception ex) {
            logger.error("获取激活租户的对应模块版本时发生异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            JdbcUtil.closeStatement(tenantGroupStatement);
            JdbcUtil.closeResultSet(tenantGroupResultSet);
        }
        return tenantModuleGroupMap;
    }

    /**
     * 更新neatlogic库
     *
     * @param resolver Strategy interface for resolving a location pattern (for example, an Ant-style path pattern) into Resource objects.
     */
    private static void updateNeatlogicDatabase(ResourcePatternResolver resolver, Map<String, List<String>> allActiveTenantChangelogSqlHashMap, Connection neatlogicConn) throws Exception {
        String currentVersion = getNeatlogicVersion(neatlogicConn);
        //如果版本为空,说明第一次建立基线，需要手动比对更新数据库后，再重启服务
        if (StringUtils.isBlank(currentVersion)) {
            currentVersion = TimeUtil.descDateStr(new Date(), 1, TimeUtil.YYYY_MM_DD);
            insertNeatLogicVersion(currentVersion, neatlogicConn);
        } else {
            List<String> versionList = new ArrayList<>();
            Resource[] resources = resolver.getResources("classpath*:neatlogic/resources/framework/**/changelog/*/neatlogic.sql");
            for (Resource resource : resources) {
                //目前仅支持dll
                String fileName = resource.getURL().getPath().substring(0, resource.getURL().getPath().lastIndexOf("/"));
                String version = fileName.substring(fileName.lastIndexOf("/") + 1);
                int versionTmp = Integer.parseInt((version.replace("-", StringUtils.EMPTY) + "00").substring(0, 10));
                int currentVersionTmp = Integer.parseInt((currentVersion.replace("-", StringUtils.EMPTY) + "00").substring(0, 10));
                if (versionTmp >= currentVersionTmp) {
                    versionList.add(version);
                }
            }
            // 定义正序比较器
            Comparator<String> fileNameComparator = Comparator.naturalOrder();
            versionList.sort(fileNameComparator);
            List<String> changelogSqlHashList = allActiveTenantChangelogSqlHashMap.get("0");
            for (String version : versionList) {
                Resource[] versionResources = resolver.getResources("classpath*:neatlogic/resources/framework/**/changelog/" + version + "/neatlogic.sql");
                for (Resource resource : versionResources) {
                    ExecuteSqlParamVo executeSqlParamVo = new ExecuteSqlParamVo("framework", version, resource, neatlogicConn);
                    executeSqlParamVo.setChangelogSqlHashList(changelogSqlHashList);
                    ScriptRunnerManager.runScriptWithJdbc(executeSqlParamVo);
                }
                updateNeatLogicVersion(currentVersion, version, neatlogicConn);
                currentVersion = version;
            }
        }
    }

    /**
     * 从数据库查询所有激活租户
     *
     * @return 激活的租户
     */
    private static String getNeatlogicVersion(Connection connection) throws Exception {
        try (PreparedStatement versionStatement = connection.prepareStatement("SELECT * FROM version order by version desc limit 1"); ResultSet versionResultSet = versionStatement.executeQuery();) {
            if (versionResultSet.next()) {
                return versionResultSet.getString("version");
            }
        } catch (Exception ex) {
            logger.error("从数据库查询所有激活租户时发生异常: " + ex.getMessage(), ex);
            System.out.println("从数据库查询所有激活租户时发生异常: " + ex.getMessage());
            throw new Exception(ex);
        }
        return null;
    }


    /**
     * 更新租户库
     *
     * @param resolver             Strategy interface for resolving a location pattern (for example, an Ant-style path pattern) into Resource objects.
     * @param moduleVersionListMap 模块类路径定义的版本
     * @param allTenantList        所有的租户
     * @param moduleVoList         所有来自context文件定义的模块
     * @throws Exception 异常
     */
    private static void updateTenantDatabase(ResourcePatternResolver resolver, Map<String, List<String>> moduleVersionListMap, List<TenantVo> allTenantList, List<ModuleVo> moduleVoList, Map<String, List<String>> allActiveTenantChangelogSqlHashMap, Connection neatlogicConn) throws Exception {
        boolean isError = false;

        // 定义倒序比较器
        Comparator<String> fileNameComparatorReversed = Comparator.reverseOrder();
        // 定义正序比较器
        Comparator<String> fileNameComparator = Comparator.naturalOrder();
        //获取最新版本
        Map<String, String> moduleLatestVersionMap = new HashMap<>();
        //获取模块最新版本
        if (MapUtils.isNotEmpty(moduleVersionListMap)) {
            for (Map.Entry<String, List<String>> entry : moduleVersionListMap.entrySet()) {
                entry.getValue().sort(fileNameComparatorReversed);
                moduleLatestVersionMap.put(entry.getKey(), entry.getValue().get(0));
            }
        }

        Map<String, Map<String, String>> tenantModuleVersionMap = getTenantModuleVersionMap(allTenantList, neatlogicConn);
        //循环需要执行的所有模块当前版本
        for (TenantVo tenant : allTenantList) {
            try (Connection tenantConnection = JdbcUtil.getNeatlogicTenantConnection(tenant, false)) {
                Map<String, String> moduleVersionMap = tenantModuleVersionMap.get(tenant.getUuid());
                if (tenantModuleVersionMap.containsKey(tenant.getUuid())) {
                    List<String> tenantChangelogSqlHashList = allActiveTenantChangelogSqlHashMap.get(tenant.getUuid());
                    if (tenantChangelogSqlHashList == null) {
                        tenantChangelogSqlHashList = new ArrayList<>();
                    }
                    for (ModuleVo moduleVo : moduleVoList) {
                        String moduleId = moduleVo.getId();

                        //String latestVersion = moduleLatestVersionMap.get(moduleId); //
                        //第一次启用基线。 即该租户该模块没有版本基线，则直接更新版本基线，不执行sql，启动服务后需要手动更新对比schema后重启tomcat实例服务
                        if (!moduleVersionMap.containsKey(moduleId) || StringUtils.isBlank(moduleVersionMap.get(moduleId)) || moduleVersionMap.get(moduleId) == null) {
                            //忽略今天及以前的版本，选择明天作为最新版本，为了后续自动更新版本
                            String latestVersion = TimeUtil.addDateStrByDay(new Date(), 1, TimeUtil.YYYY_MM_DD);
                            insertTenantModuleVersionSql(tenant.getUuid(), moduleId, latestVersion, neatlogicConn);
                            //如果模块版本小于最新版本，则执行sql并更新为最新版本
                        } else {
                            //循环执行所有
                            List<String> versionList = moduleVersionListMap.get(moduleId);
                            if (CollectionUtils.isNotEmpty(versionList)) {
                                versionList.sort(fileNameComparator);
                                for (String version : versionList) {
                                    int versionTmp = Integer.parseInt((version.replace("-", StringUtils.EMPTY) + "00").substring(0, 10));
                                    int currentVersionTmp = Integer.parseInt((moduleVersionMap.get(moduleId).replace("-", StringUtils.EMPTY) + "00").substring(0, 10));
                                    if (versionTmp >= currentVersionTmp) {
                                        Resource[] resources = resolver.getResources("classpath*:neatlogic/resources/" + moduleId + "/**/changelog/" + version + "/neatlogic_tenant.sql");
                                        for (Resource resource : resources) {
                                            ExecuteSqlParamVo executeSqlParamVo = new ExecuteSqlParamVo(tenant, moduleId, version, resource, tenantChangelogSqlHashList, tenantConnection, neatlogicConn, true);
                                            boolean isErrorTmp = ScriptRunnerManager.runScriptWithJdbc(executeSqlParamVo);
                                            if (isErrorTmp) {
                                                isError = true;
                                            }
                                        }
                                        //执行整个sql文件
                                        Resource[] resourcesAll = resolver.getResources("classpath*:neatlogic/resources/" + moduleId + "/**/changelog/" + version + "/neatlogic_tenant_all.sql");
                                        for (Resource resourceAll : resourcesAll) {
                                            ExecuteSqlParamVo executeSqlParamVo = new ExecuteSqlParamVo(tenant, moduleId, version, resourceAll, tenantChangelogSqlHashList, tenantConnection, neatlogicConn, true);
                                            executeSqlParamVo.setAll(true);
                                            boolean isErrorTmp = ScriptRunnerManager.runScriptWithJdbc(executeSqlParamVo);
                                            if (isErrorTmp) {
                                                isError = true;
                                            }
                                        }
                                        insertTenantModuleVersionSql(tenant.getUuid(), moduleId, version, neatlogicConn);
                                        System.out.println("  ✓" + tenant.getName() + "·" + moduleId);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //第一次启用基线。 即该租户所有模块没有版本基线，则直接更新版本基线，不执行sql，启动服务后需要手动更新对比schema后重启tomcat实例服务
                    for (ModuleVo moduleVo : moduleVoList) {
                        //忽略今天及以前的版本，选择明天作为最新版本，为了后续自动更新版本
                        String latestVersion = TimeUtil.addDateStrByDay(new Date(), 1, TimeUtil.YYYY_MM_DD);
                        insertTenantModuleVersionSql(tenant.getUuid(), moduleVo.getId(), latestVersion, neatlogicConn);
                    }
                }
            }
        }
//        if (isError) {
//            System.exit(1);
//        }
    }

    /**
     * 插入租户模块信息
     */
    private static void insertTenantModuleVersionSql(String tenantUuid, String moduleId, String version, Connection neatlogicConn) throws Exception {
        try (PreparedStatement statement = neatlogicConn.prepareStatement("insert into `tenant_module` (`tenant_uuid`,`module_id`,`version`,`fcd`,`lcd`) VALUES (?,?,?,now(),now()) ON DUPLICATE KEY UPDATE version = ?,`lcd` = now()")) {
            statement.setString(1, tenantUuid);
            statement.setString(2, moduleId);
            statement.setString(3, version);
            statement.setString(4, version);
            statement.execute();
        } catch (Exception ex) {
            logger.error("插入租户模块信息时发生异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        }
    }

    /**
     * 插入neatlogic版本
     */
    private static void insertNeatLogicVersion(String version, Connection neatlogicConn) throws Exception {
        try (PreparedStatement statement = neatlogicConn.prepareStatement("insert into `version` (`version`,`fcd`,`lcd`) VALUES (?,now(),now())")) {
            statement.setString(1, version);
            statement.execute();
        } catch (Exception ex) {
            logger.error("插入neatlogic版本时发生异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        }
    }

    /**
     * 更新neatlogic版本
     */
    private static void updateNeatLogicVersion(String oldVersion, String version, Connection neatlogicConn) throws Exception {
        try (PreparedStatement statement = neatlogicConn.prepareStatement("UPDATE version SET version = ?, lcd = NOW() WHERE version = ?")) {
            statement.setString(1, version);
            statement.setString(2, oldVersion);
            statement.execute();
        } catch (Exception ex) {
            logger.error("更新neatlogic版本时发生异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        }
    }
}
