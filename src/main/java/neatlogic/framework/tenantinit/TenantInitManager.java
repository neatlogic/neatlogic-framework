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

package neatlogic.framework.tenantinit;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RootComponent
public class TenantInitManager extends ModuleInitializedListenerBase {
    private final static Logger logger = LoggerFactory.getLogger(TenantInitManager.class);

    private final static List<ITenantInit> tenantInitList = new ArrayList<>();

    public static List<ITenantInit> getTenantInitList() {
        return tenantInitList.stream().sorted(Comparator.comparing(ITenantInit::sort)).collect(Collectors.toList());
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        List<ITenantInit> list = new ArrayList<>();
        //先收集所有启动作业
        Map<String, ITenantInit> myMap = context.getBeansOfType(ITenantInit.class);
        for (Map.Entry<String, ITenantInit> entry : myMap.entrySet()) {
            ITenantInit tenantInit = entry.getValue();
            tenantInit.setGroup(context.getGroup());
            list.add(tenantInit);
        }
        tenantInitList.addAll(list);
    }

    /**
     * 执行指定租户的所有启动作业，用在创建租户的场景，初始化数据
     *
     * @param tenantVo 租户信息
     */
    public static void execute(TenantVo tenantVo) {
        TenantContext.get().switchTenant(tenantVo.getUuid());
        List<ModuleGroupVo> activeModuleGroupList = TenantContext.get().getActiveModuleGroupList();
        List<String> groupList = activeModuleGroupList.stream().map(ModuleGroupVo::getGroup).collect(Collectors.toList());
        List<ITenantInit> iTenantInits = getTenantInitList();
        iTenantInits.sort(Comparator.comparing(ITenantInit::sort));
        for (ITenantInit tenantInit : iTenantInits) {
            try {
                if (groupList.contains(tenantInit.getGroup())) {
                    tenantInit.execute();
                }
            } catch (Exception ex) {
                logger.error("租户“" + tenantVo.getName() + "”初始化数据“" + tenantInit.getName() + "”失败：" + ex.getMessage(), ex);
            }
        }
        TenantContext.get().setUseDefaultDatasource(true);
    }


    @Override
    protected void myInit() {


    }
}
