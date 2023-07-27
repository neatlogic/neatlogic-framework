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

package neatlogic.framework.startup;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.sqlfile.ScriptRunnerManager;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.Resource;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RootComponent
public class StartupManager extends ModuleInitializedListenerBase {
    private final static Logger logger = LoggerFactory.getLogger(StartupManager.class);

    private final static List<IStartup> startupList = new ArrayList<>();

    public static List<IStartup> getStartupList() {
        return startupList;
    }

    @Resource
    private TenantMapper tenantMapper;

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        List<IStartup> list = new ArrayList<>();
        //获取对应模块dml 数据初始化
        org.springframework.core.io.Resource resource = null;
        ModuleVo moduleVo = ModuleUtil.getModuleById(context.getModuleId());
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath*:neatlogic/resources/" + moduleVo.getId() + "/sqlscript/dml.sql");
            if (resources.length == 1) {
                resource = resources[0];
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        //先收集所有启动作业
        Map<String, IStartup> myMap = context.getBeansOfType(IStartup.class);
        for (Map.Entry<String, IStartup> entry : myMap.entrySet()) {
            IStartup startup = entry.getValue();
            startup.setGroup(context.getGroup());
            list.add(startup);
        }
        //如果没有要执行的dml 和 要初始化的startup
        if (resource == null && CollectionUtils.isEmpty(list)) {
            return;
        }
        //模块全部加载完毕后再开始启动作业，依赖NeatLogicThread任务会等待全部模块加载完毕后再开始执行逻辑
        List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
        if (CollectionUtils.isNotEmpty(tenantList)) {
            org.springframework.core.io.Resource finalResource = resource;
            CachedThreadPool.execute(new NeatLogicThread("STARTUP-RUNNER") {
                @Override
                protected void execute() {
                    for (TenantVo tenantVo : tenantList) {
                        TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
                        List<ModuleGroupVo> activeModuleGroupList = TenantContext.get().getActiveModuleGroupList();
                        List<String> groupList = activeModuleGroupList.stream().map(ModuleGroupVo::getGroup).collect(Collectors.toList());
                        //只有拥有当前模块权限的的租户才会执行startup 和 dml
                        if (!groupList.contains(moduleVo.getGroup())) {
                            continue;
                        }
                        //执行dml
                        if (finalResource != null) {
                            StringWriter logStrWriter = new StringWriter();
                            PrintWriter logWriter = new PrintWriter(logStrWriter);
                            StringWriter errStrWriter = new StringWriter();
                            PrintWriter errWriter = new PrintWriter(errStrWriter);
                            try {
                                Reader scriptReader = new InputStreamReader(finalResource.getInputStream());
                                ScriptRunnerManager.runScriptOnce(tenantVo, moduleVo.getId(), scriptReader, logWriter, errWriter);
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                                logger.error(errStrWriter.toString());
                            }
                        }
                        TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
                        if (CollectionUtils.isNotEmpty(list)) {
                            for (IStartup startup : list) {
                                try {

                                    int i = startup.executeForCurrentTenant();
                                    if (i != -999) {
                                        System.out.println("⚡[" + tenantVo.getName() + "]" + $.t("common.startupjob") + " " + $.t(startup.getName()) + " " + $.t("common.successload"));
                                    }
                                } catch (Exception ex) {
                                    System.out.println("⚡[" + tenantVo.getName() + "]" + $.t("common.startupjob") + " " + $.t(startup.getName()) + " " + $.t("common.failedload"));
                                    logger.error(ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(list)) {
                        for (IStartup startup : list) {
                            try {
                                int i = startup.executeForAllTenant();
                                if (i != -999) {
                                    System.out.println("⚡[All Tenant]" + $.t("common.startupjob") + " " + $.t(startup.getName()) + " " + $.t("common.successload"));
                                }
                            } catch (Exception ex) {
                                System.out.println("⚡[All Tenant]" + $.t("common.startupjob") + " " + $.t(startup.getName()) + " " + $.t("common.failedload"));
                                logger.error(ex.getMessage(), ex);
                            }
                        }
                    }
                }
            });
        }
        startupList.addAll(list);
    }


    @Override
    protected void myInit() {


    }
}
