/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.startup;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.thread.ModuleInitApplicationListener;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dao.mapper.ModuleMapper;
import codedriver.framework.dao.mapper.TenantMapper;
import codedriver.framework.dto.ModuleGroupVo;
import codedriver.framework.dto.TenantVo;
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
    @Resource
    private TenantMapper tenantMapper;
    @Resource
    private ModuleMapper moduleMapper;

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        List<IStartup> list = new ArrayList<>();
        //先收集所有启动作业
        Map<String, IStartup> myMap = context.getBeansOfType(IStartup.class);
        for (Map.Entry<String, IStartup> entry : myMap.entrySet()) {
            IStartup startup = entry.getValue();
            startup.setGroupName(context.getGroupName());
            list.add(startup);
        }
        //模块全部加载完毕后再开始启动作业，依赖CodeDriverThread任务会等待全部模块加载完毕后再开始执行逻辑
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort(Comparator.comparingInt(IStartup::sort));
            List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
            if (CollectionUtils.isNotEmpty(tenantList)) {
                CachedThreadPool.execute(new CodeDriverThread("STARTUP-RUNNER") {
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
