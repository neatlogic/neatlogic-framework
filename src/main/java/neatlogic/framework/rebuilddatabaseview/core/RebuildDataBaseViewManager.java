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

package neatlogic.framework.rebuilddatabaseview.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dto.module.ModuleGroupVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@RootComponent
public class RebuildDataBaseViewManager extends ModuleInitializedListenerBase {

    private final static Map<String, List<IRebuildDataBaseView>> moduleGroup2HandlerListMap = new HashMap<>();

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IRebuildDataBaseView> myMap = context.getBeansOfType(IRebuildDataBaseView.class);
        for (Map.Entry<String, IRebuildDataBaseView> entry : myMap.entrySet()) {
            IRebuildDataBaseView bean = entry.getValue();
            moduleGroup2HandlerListMap.computeIfAbsent(context.getGroup(), key -> new ArrayList<>()).add(bean);
        }
    }

    public static List<ViewStatusInfo> execute() {
        List<ViewStatusInfo> resultList = new ArrayList<>();
        List<IRebuildDataBaseView> list = new ArrayList<>();
        List<ModuleGroupVo> activeModuleGroupList = TenantContext.get().getActiveModuleGroupList();
        for (ModuleGroupVo moduleGroupVo : activeModuleGroupList) {
            List<IRebuildDataBaseView> handlerList = moduleGroup2HandlerListMap.get(moduleGroupVo.getGroup());
            if (CollectionUtils.isNotEmpty(handlerList)) {
                list.addAll(handlerList);
            }
        }
        list.sort(Comparator.comparing(IRebuildDataBaseView::getSort));
        for (IRebuildDataBaseView rebuildDataBaseView : list) {
            resultList.addAll(rebuildDataBaseView.execute());
        }
        return resultList;
    }

    @Override
    protected void myInit() {

    }
}
