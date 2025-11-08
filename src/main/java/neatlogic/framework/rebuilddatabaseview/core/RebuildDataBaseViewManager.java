/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.rebuilddatabaseview.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.SchemaMapper;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.transaction.core.EscapeTransactionJob;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@RootComponent
public class RebuildDataBaseViewManager extends ModuleInitializedListenerBase {

    private final static Map<String, List<IRebuildDataBaseView>> moduleGroup2HandlerListMap = new HashMap<>();

    private static SchemaMapper schemaMapper;

    @Resource
    public void setSchemaMapper(SchemaMapper _schemaMapper) {
        schemaMapper = _schemaMapper;
    }
    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IRebuildDataBaseView> myMap = context.getBeansOfType(IRebuildDataBaseView.class);
        for (Map.Entry<String, IRebuildDataBaseView> entry : myMap.entrySet()) {
            IRebuildDataBaseView bean = entry.getValue();
            moduleGroup2HandlerListMap.computeIfAbsent(context.getGroup(), key -> new ArrayList<>()).add(bean);
        }
    }
    /**
     * 只有视图不存在时才创建视图
     * @return
     */
    public static List<ViewStatusInfo> createViewIfNotExists() {
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
            resultList.addAll(rebuildDataBaseView.createViewIfNotExists());
        }
        return resultList;
    }

    /**
     * 如果视图存在则删除，重新创建视图
     * @return
     */
    public static List<ViewStatusInfo> createOrReplaceView() {
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
            List<ViewStatusInfo> viewStatusInfoList = rebuildDataBaseView.createOrReplaceView();
            EscapeTransactionJob.State s = new EscapeTransactionJob(() -> {
                for (ViewStatusInfo viewStatusInfo : viewStatusInfoList) {
                    if (Objects.equals(viewStatusInfo.getStatus(), ViewStatusInfo.Status.FAILURE.toString())) {
                        String tableType = schemaMapper.checkTableOrViewIsExists(TenantContext.get().getDataDbName(), viewStatusInfo.getName());
                        if (Objects.equals(tableType, "VIEW")) {
                            schemaMapper.deleteView(TenantContext.get().getDataDbName() + "." + viewStatusInfo.getName());
                        }
                    }
                }
            }).execute();
            resultList.addAll(viewStatusInfoList);
        }
        return resultList;
    }

    @Override
    protected void myInit() {

    }
}
