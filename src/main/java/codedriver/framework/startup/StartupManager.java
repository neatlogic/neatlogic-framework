/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.startup;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.thread.ModuleInitApplicationListener;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.TenantMapper;
import codedriver.framework.dto.TenantVo;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RootComponent
public class StartupManager extends ApplicationListenerBase {
    private final static Logger logger = LoggerFactory.getLogger(StartupManager.class);

    private final static List<IStartup> startupList = new ArrayList<>();
    @Resource
    private TenantMapper tenantMapper;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IStartup> myMap = context.getBeansOfType(IStartup.class);
        for (Map.Entry<String, IStartup> entry : myMap.entrySet()) {
            startupList.add(entry.getValue());
        }
        //模块全部加载完毕后再开始启动作业
        if (ModuleInitApplicationListener.getModuleinitphaser().getArrivedParties() == 0) {
            if (CollectionUtils.isNotEmpty(startupList)) {
                startupList.sort(Comparator.comparingInt(IStartup::sort));
                List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
                if (CollectionUtils.isNotEmpty(tenantList)) {
                    CachedThreadPool.execute(new CodeDriverThread("STARTUP-RUNNER") {
                        @Override
                        protected void execute() {
                            for (TenantVo tenantVo : tenantList) {
                                TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
                                for (IStartup startup : startupList) {
                                    try {
                                        startup.execute();
                                    } catch (Exception ex) {
                                        logger.error("租户：" + tenantVo.getName() + "的启动作业：" + startup.getName() + "执行失败：" + ex.getMessage(), ex);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 执行指定租户的所有启动作业，用在创建租户的场景
     *
     * @param tenantVo 租户信息
     */
    public static void executeStartup(TenantVo tenantVo) {
        TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
        for (IStartup startup : startupList) {
            try {
                startup.execute();
            } catch (Exception ex) {
                logger.error("租户：" + tenantVo.getName() + "的启动作业：" + startup.getName() + "执行失败：" + ex.getMessage(), ex);
            }
        }
    }


    @Override
    protected void myInit() {


    }
}
