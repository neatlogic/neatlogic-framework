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

package neatlogic.framework.startup;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RootComponent
public class StartupManager extends ModuleInitializedListenerBase {
    private static final Logger logger = LoggerFactory.getLogger(StartupManager.class);

    private static final List<IStartup> startupList = new ArrayList<>();

    public static List<IStartup> getStartupList() {
        return startupList;
    }

    @Resource
    private TenantMapper tenantMapper;

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        List<IStartup> list = new ArrayList<>();
        ModuleVo moduleVo = ModuleUtil.getModuleById(context.getModuleId());

        //先收集所有启动作业
        Map<String, IStartup> myMap = context.getBeansOfType(IStartup.class);
        for (Map.Entry<String, IStartup> entry : myMap.entrySet()) {
            IStartup startup = entry.getValue();
            startup.setGroup(context.getGroup());
            list.add(startup);
        }
        //要初始化的startup
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        //模块全部加载完毕后再开始启动作业，依赖NeatLogicThread任务会等待全部模块加载完毕后再开始执行逻辑
        List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
        if (CollectionUtils.isNotEmpty(tenantList)) {
            CachedThreadPool.execute(new NeatLogicThread("STARTUP-RUNNER") {
                @Override
                protected void execute() {
                    if (CollectionUtils.isNotEmpty(list)) {
                        for (IStartup startup : list) {
                            for (TenantVo tenantVo : tenantList) {
                                TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
                                List<ModuleGroupVo> activeModuleGroupList = TenantContext.get().getActiveModuleGroupList();
                                List<String> groupList = activeModuleGroupList.stream().map(ModuleGroupVo::getGroup).collect(Collectors.toList());
                                //只有拥有当前模块权限的的租户才会执行startup
                                if (!groupList.contains(moduleVo.getGroup())) {
                                    continue;
                                }

                                TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
                                UserContext.init(SystemUser.SYSTEM);
                                try {
                                    int i = startup.executeForCurrentTenant();
                                    if (i != -999) {
                                        System.out.println("⚡" + $.t("common.startloadstartupjob", $.t(startup.getName()), tenantVo.getName()));
                                    }
                                } catch (Exception ex) {
                                    logger.error(ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                    //还原默认数据库neatlogic
                    TenantContext.get().setUseDefaultDatasource(true);
                    if (CollectionUtils.isNotEmpty(list)) {
                        for (IStartup startup : list) {
                            try {
                                int i = startup.executeForAllTenant();
                                if (i != -999) {
                                    System.out.println("⚡" + $.t("common.startloadstartupjob", $.t(startup.getName()), "All Tenant"));
                                }
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                            }
                        }
                    }
                    TenantContext.get().setUseDefaultDatasource(false);
                }
            });
        }
        startupList.addAll(list);
    }


    @Override
    protected void myInit() {


    }
}
