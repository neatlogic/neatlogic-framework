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
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.TenantVo;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
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
        //先收集所有启动作业
        Map<String, IStartup> myMap = context.getBeansOfType(IStartup.class);
        for (Map.Entry<String, IStartup> entry : myMap.entrySet()) {
            IStartup startup = entry.getValue();
            startup.setGroupName(context.getGroupName());
            list.add(startup);
        }
        //模块全部加载完毕后再开始启动作业，依赖NeatLogicThread任务会等待全部模块加载完毕后再开始执行逻辑
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort(Comparator.comparingInt(IStartup::sort));
            List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
            if (CollectionUtils.isNotEmpty(tenantList)) {
                CachedThreadPool.execute(new NeatLogicThread("STARTUP-RUNNER") {
                    @Override
                    protected void execute() {
                        for (TenantVo tenantVo : tenantList) {
                            TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
                            List<ModuleGroupVo> activeModuleGroupList = TenantContext.get().getActiveModuleGroupList();
                            List<String> groupNameList = activeModuleGroupList.stream().map(ModuleGroupVo::getGroupName).collect(Collectors.toList());
                            for (IStartup startup : list) {
                                //只有拥有当前模块权限的的租户才会执行startup里的逻辑
                                if (groupNameList.contains(startup.getGroupName())) {
                                    try {
                                        startup.executeForCurrentTenant();
                                    } catch (Exception ex) {
                                        logger.error("租户“" + tenantVo.getName() + "”的启动作业“" + startup.getName() + "”执行失败：" + ex.getMessage(), ex);
                                    }
                                }
                            }
                        }
                        for (IStartup startup : list) {
                            try {
                                startup.executeForAllTenant();
                            } catch (Exception ex) {
                                logger.error("启动作业“" + startup.getName() + "“执行失败：" + ex.getMessage(), ex);
                            }
                        }
                    }
                });
            }
            startupList.addAll(list);
        }
    }

    /**
     * 执行指定租户的所有启动作业，用在创建租户的场景
     *
     * @param tenantVo 租户信息
     */
    public static void executeStartup(TenantVo tenantVo) {
        TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
        List<ModuleGroupVo> activeModuleGroupList = TenantContext.get().getActiveModuleGroupList();
        List<String> groupNameList = activeModuleGroupList.stream().map(ModuleGroupVo::getGroupName).collect(Collectors.toList());
        for (IStartup startup : startupList) {
            if (groupNameList.contains(startup.getGroupName())) {
                try {
                    startup.executeForCurrentTenant();
                } catch (Exception ex) {
                    logger.error("租户：" + tenantVo.getName() + "的启动作业：" + startup.getName() + "执行失败：" + ex.getMessage(), ex);
                }
            }
        }
    }


    @Override
    protected void myInit() {


    }
}
