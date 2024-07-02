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
        if (clazz != null) {
            String handler = clazz.getName();
            invokeNotifyPolicyConfigVo.setHandler(handler);
        }
        return doRegulateNotifyPolicyConfig(invokeNotifyPolicyConfigVo);
    }

    @Override
    public InvokeNotifyPolicyConfigVo regulateNotifyPolicyConfig(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo, Class<? extends INotifyPolicyHandler> clazz) {
        if (invokeNotifyPolicyConfigVo == null) {
            invokeNotifyPolicyConfigVo = new InvokeNotifyPolicyConfigVo();
        }
        if (clazz != null) {
            String handler = clazz.getName();
            invokeNotifyPolicyConfigVo.setHandler(handler);
        }
        return doRegulateNotifyPolicyConfig(invokeNotifyPolicyConfigVo);
    }

    private InvokeNotifyPolicyConfigVo doRegulateNotifyPolicyConfig(InvokeNotifyPolicyConfigVo invokeNotifyPolicyConfigVo) {
        NotifyPolicyVo notifyPolicyVo = null;
        if (invokeNotifyPolicyConfigVo.getIsCustom() == 1) {
            if (invokeNotifyPolicyConfigVo.getPolicyId() != null) {
                notifyPolicyVo = notifyMapper.getNotifyPolicyById(invokeNotifyPolicyConfigVo.getPolicyId());
            }
        } else {
            if (invokeNotifyPolicyConfigVo.getHandler() != null) {
                notifyPolicyVo = notifyMapper.getDefaultNotifyPolicyByHandler(invokeNotifyPolicyConfigVo.getHandler());
            }
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
