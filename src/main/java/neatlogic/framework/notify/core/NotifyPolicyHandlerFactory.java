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

package neatlogic.framework.notify.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.notify.dto.NotifyPolicyHandlerVo;
import neatlogic.framework.notify.dto.NotifyTreeVo;
import neatlogic.framework.notify.dto.NotifyTriggerVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RootComponent
public class NotifyPolicyHandlerFactory extends ModuleInitializedListenerBase {

    private static final List<NotifyPolicyHandlerVo> notifyPolicyHandlerList = new ArrayList<>();

    private static final Map<String, INotifyPolicyHandler> notifyPolicyHandlerMap = new HashMap<>();

    private static final List<NotifyTreeVo> moduleTreeVoList = new ArrayList<>();

    private static final Map<String, NotifyTreeVo> moduleTreeVoMap = new HashMap<>();

    private static final List<NotifyTreeVo> notifyPolicyTreeVoList = new ArrayList<>();

    private static final Map<String, NotifyTreeVo> notifyPolicyGroupTreeVoMap = new HashMap<>();

    public static INotifyPolicyHandler getHandler(String handler) {
        return notifyPolicyHandlerMap.get(handler);
    }

    public static List<NotifyPolicyHandlerVo> getNotifyPolicyHandlerList() {
        return notifyPolicyHandlerList;
    }

    public static List<NotifyTreeVo> getModuleTreeVoList() {
        return moduleTreeVoList;
    }

    public static List<String> getTriggerList(String type) {
        NotifyTreeVo targetNode = getTargetNode(moduleTreeVoList, type);
        return getTriggerList(targetNode);
    }

    public static List<String> getAllActiveTriggerList(){
        List<ModuleVo> activeModuleList = TenantContext.get().getActiveModuleList();
        List<String> triggerList = new ArrayList<>();
        for (ModuleVo moduleVo : activeModuleList) {
            NotifyTreeVo targetNode = getTargetNode(moduleTreeVoList, moduleVo.getGroup());
            triggerList.addAll(getTriggerList(targetNode));
        }
        return triggerList;
    }

    /**
     * @Description: ????????????????????????????????????????????????
     * @Author: linbq
     * @Date: 2021/2/23 10:19
     * @Params: [treeVoList, type]
     * @Returns: neatlogic.framework.notify.dto.NotifyTreeVo
     **/
    private static NotifyTreeVo getTargetNode(List<NotifyTreeVo> treeVoList, String type) {
        if (CollectionUtils.isNotEmpty(treeVoList)) {
            for (NotifyTreeVo notifyTreeVo : treeVoList) {
                if (notifyTreeVo.getUuid().equals(type)) {
                    return notifyTreeVo;
                } else {
                    NotifyTreeVo targetNode = getTargetNode(notifyTreeVo.getChildren(), type);
                    if (targetNode != null) {
                        return targetNode;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @Description: ??????????????????????????????????????????????????????????????????
     * @Author: linbq
     * @Date: 2021/2/23 10:18
     * @Params: [notifyTreeVo]
     * @Returns: java.util.List<java.lang.String>
     **/
    private static List<String> getTriggerList(NotifyTreeVo notifyTreeVo) {
        List<String> resultList = new ArrayList<>();
        if (notifyTreeVo != null) {
            List<NotifyTreeVo> children = notifyTreeVo.getChildren();
            if (children == null) {
                resultList.add(notifyTreeVo.getUuid());
            } else {
                for (NotifyTreeVo child : children) {
                    resultList.addAll(getTriggerList(child));
                }
            }
        }
        return resultList;
    }

    public static List<NotifyTreeVo> getNotifyPolicyTreeVoList() {
        return notifyPolicyTreeVoList;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        ModuleVo moduleVo = ModuleUtil.getModuleById(context.getId());
        Map<String, INotifyPolicyHandler> map = context.getBeansOfType(INotifyPolicyHandler.class);
        for (Entry<String, INotifyPolicyHandler> entry : map.entrySet()) {
            INotifyPolicyHandler notifyPolicyHandler = entry.getValue();
            if (notifyPolicyHandler.isPublic()) {
                notifyPolicyHandlerMap.put(notifyPolicyHandler.getClassName(), notifyPolicyHandler);
                notifyPolicyHandlerList.add(new NotifyPolicyHandlerVo(notifyPolicyHandler.getClassName(), notifyPolicyHandler.getName(), notifyPolicyHandler.getAuthName(), moduleVo.getGroup(), notifyPolicyHandler.isAllowMultiPolicy()));

                INotifyPolicyHandlerGroup notifyPolicyHandlerGroup = notifyPolicyHandler.getGroup();
                if (notifyPolicyHandlerGroup == null) {
                    notifyPolicyTreeVoList.add(new NotifyTreeVo(notifyPolicyHandler.getClassName(), notifyPolicyHandler.getName()));
                } else {
                    NotifyTreeVo notifyPolicyGroupTreeVo = notifyPolicyGroupTreeVoMap.get(notifyPolicyHandlerGroup.getValue());
                    if (notifyPolicyGroupTreeVo == null) {
                        notifyPolicyGroupTreeVo = new NotifyTreeVo(notifyPolicyHandlerGroup.getValue(), notifyPolicyHandlerGroup.getText());
                        notifyPolicyGroupTreeVoMap.put(notifyPolicyHandlerGroup.getValue(), notifyPolicyGroupTreeVo);
                        notifyPolicyTreeVoList.add(notifyPolicyGroupTreeVo);
                    }
                    notifyPolicyGroupTreeVo.addChildren(new NotifyTreeVo(notifyPolicyHandler.getClassName(), notifyPolicyHandler.getName()));
                }
            }

            NotifyTreeVo treeVo = new NotifyTreeVo(notifyPolicyHandler.getClassName(), notifyPolicyHandler.getName());
            List<NotifyTreeVo> children = new ArrayList<>();
//            if (CollectionUtils.isNotEmpty(notifyPolicyHandler.getNotifyTriggerListForNotifyTree())) {
//                for (NotifyTriggerVo notifyTriggerVo : notifyPolicyHandler.getNotifyTriggerListForNotifyTree()) {
//                    children.add(new NotifyTreeVo(notifyTriggerVo.getTrigger(), notifyTriggerVo.getTriggerName()));
//                }
//            }
            if (CollectionUtils.isNotEmpty(notifyPolicyHandler.getNotifyTriggerList())) {
                for (NotifyTriggerVo notifyTriggerVo : notifyPolicyHandler.getNotifyTriggerList()) {
                    children.add(new NotifyTreeVo(notifyTriggerVo.getTrigger(), notifyTriggerVo.getTriggerName()));
                }
            }
            treeVo.setChildren(children);
            NotifyTreeVo parentTreeVo = moduleTreeVoMap.get(moduleVo.getGroup());
            if (parentTreeVo == null) {
                parentTreeVo = new NotifyTreeVo(moduleVo.getGroup(), moduleVo.getGroupName());
                moduleTreeVoMap.put(moduleVo.getGroup(), parentTreeVo);
                moduleTreeVoList.add(parentTreeVo);
            }
            parentTreeVo.addChildren(treeVo);
        }
    }

    @Override
    protected void myInit() {

    }

}
