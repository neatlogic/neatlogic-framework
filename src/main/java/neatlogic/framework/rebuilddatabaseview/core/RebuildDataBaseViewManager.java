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
