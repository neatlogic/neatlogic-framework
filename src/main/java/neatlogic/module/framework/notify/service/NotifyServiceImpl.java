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

package neatlogic.module.framework.notify.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.notify.core.INotifyPolicyHandler;
import neatlogic.framework.notify.core.NotifyPolicyHandlerFactory;
import neatlogic.framework.notify.crossover.INotifyServiceCrossoverService;
import neatlogic.framework.notify.dao.mapper.NotifyMapper;
import neatlogic.framework.notify.dto.InvokeNotifyPolicyConfigVo;
import neatlogic.framework.notify.dto.NotifyPolicyVo;
import neatlogic.framework.notify.exception.NotifyPolicyHandlerNotFoundException;
import neatlogic.framework.notify.exception.NotifyPolicyNotFoundException;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NotifyServiceImpl implements NotifyService, INotifyServiceCrossoverService {

    @Resource
    private NotifyMapper notifyMapper;

    /**
     * 保存流程、查询流程信息时调用
     * @param notifyPolicyConfig
     * @param clazz
     * @return
     */
    @Override
    public InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(JSONObject notifyPolicyConfig, Class<? extends INotifyPolicyHandler> clazz) {
        InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo = JSONObject.toJavaObject(notifyPolicyConfig, InvokeNotifyPolicyConfigVo.class);
        if (invokeNotifyPolicyConfigVo == null) {
            invokeNotifyPolicyConfigVo = new InvokeNotifyPolicyConfigVo();
        }
        String handler = clazz.getName();
        invokeNotifyPolicyConfigVo.setHandler(handler);
        return doRegulateNotifyPolicyConfig(invokeNotifyPolicyConfigVo);
    }

    @Override
    public InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo, Class<? extends INotifyPolicyHandler> clazz) {
        if (invokeNotifyPolicyConfigVo == null) {
            invokeNotifyPolicyConfigVo = new InvokeNotifyPolicyConfigVo();
        }
        String handler = clazz.getName();
        invokeNotifyPolicyConfigVo.setHandler(handler);
        return doRegulateNotifyPolicyConfig(invokeNotifyPolicyConfigVo);
    }

    private InvokeNotifyPolicyConfigVo doRegulateNotifyPolicyConfig(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo) {
        NotifyPolicyVo notifyPolicyVo = null;
        if (invokeNotifyPolicyConfigVo.getIsCustom() == 1) {
            if (invokeNotifyPolicyConfigVo.getPolicyId() != null) {
                notifyPolicyVo = notifyMapper.getNotifyPolicyById(invokeNotifyPolicyConfigVo.getPolicyId());
            }
        } else {
            notifyPolicyVo = notifyMapper.getDefaultNotifyPolicyByHandler(invokeNotifyPolicyConfigVo.getHandler());
        }
        if (notifyPolicyVo == null) {
            invokeNotifyPolicyConfigVo.setPolicyId(null);
            invokeNotifyPolicyConfigVo.setPolicyName(null);
        } else {
            invokeNotifyPolicyConfigVo.setPolicyId(notifyPolicyVo.getId());
            invokeNotifyPolicyConfigVo.setPolicyName(notifyPolicyVo.getName());
            INotifyPolicyHandler notifyPolicyHandler = NotifyPolicyHandlerFactory.getHandler(notifyPolicyVo.getHandler());
            if (notifyPolicyHandler == null) {
                throw new NotifyPolicyHandlerNotFoundException(notifyPolicyVo.getHandler());
            }
            String moduleGroup = NotifyPolicyHandlerFactory.getModuleGroupIdByHandler(notifyPolicyVo.getHandler());
            if (moduleGroup == null) {
                throw new NotifyPolicyHandlerNotFoundException(notifyPolicyVo.getHandler());
            }
            String moduleGroupName = "";
            ModuleGroupVo moduleGroupVo = ModuleUtil.getModuleGroup(moduleGroup);
            if (moduleGroupVo != null) {
                moduleGroupName = moduleGroupVo.getGroupName();
            }
            String handlerName = $.t(notifyPolicyHandler.getName());
            invokeNotifyPolicyConfigVo.setPolicyPath(moduleGroupName + "/" + handlerName + "/" + notifyPolicyVo.getName());
        }
        return invokeNotifyPolicyConfigVo;
    }
    /**
     * 触发点发送通知时调用
     * @param notifyPolicyConfig
     * @return
     */
    @Override
    public InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(JSONObject notifyPolicyConfig) {
        if (MapUtils.isEmpty(notifyPolicyConfig)) {
            return null;
        }
        InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo = JSONObject.toJavaObject(notifyPolicyConfig, InvokeNotifyPolicyConfigVo.class);
        return regulateNotifyPolicyConfig(invokeNotifyPolicyConfigVo);
    }

    /**
     * 组合工具发送通知时调用
     * @param invokeNotifyPolicyConfigVo
     * @return
     */
    @Override
    public InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo) {
        if (invokeNotifyPolicyConfigVo == null) {
            return null;
        }
        String handler = invokeNotifyPolicyConfigVo.getHandler();
        if (StringUtils.isBlank(handler)) {
            return null;
        }
        return doRegulateNotifyPolicyConfig(invokeNotifyPolicyConfigVo);
    }

    /**
     * 检查通知策略是否存在
     * 1.如果是采用默认模式，不用检查通知策略是否存在，直接返回false
     * 2.如果是采用自定义模式，需要检查通知策略是否存在，存在返回true，不存在则抛异常
     * @param invokeNotifyPolicyConfigVo 引用通知策略配置信息
     * @return 如果返回true，调用方可能需要执行依赖引用关系保存逻辑
     */
    @Override
    public boolean checkNotifyPolicyIsExists(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo) {
        if (invokeNotifyPolicyConfigVo == null) {
            return false;
        }
        if (invokeNotifyPolicyConfigVo.getPolicyId() == null) {
            return false;
        }
        if (invokeNotifyPolicyConfigVo.getIsCustom() == 0) {
            return false;
        }
        if (notifyMapper.checkNotifyPolicyIsExists(invokeNotifyPolicyConfigVo.getPolicyId()) == 0) {
            if (StringUtils.isNotBlank(invokeNotifyPolicyConfigVo.getPolicyPath())) {
                throw new NotifyPolicyNotFoundException(invokeNotifyPolicyConfigVo.getPolicyPath());
            } else {
                throw new NotifyPolicyNotFoundException(invokeNotifyPolicyConfigVo.getPolicyId());
            }
        }
        return true;
    }
}
