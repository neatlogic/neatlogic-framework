package codedriver.framework.util;

import codedriver.framework.asynchronization.threadlocal.ConditionParamContext;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dto.condition.ConditionConfigVo;
import codedriver.framework.dto.condition.ConditionGroupVo;
import codedriver.framework.message.core.IMessageHandler;
import codedriver.framework.notify.core.INotifyHandler;
import codedriver.framework.notify.core.INotifyTriggerType;
import codedriver.framework.notify.core.NotifyHandlerFactory;
import codedriver.framework.notify.dto.*;
import codedriver.framework.notify.exception.NotifyHandlerNotFoundException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotifyPolicyUtil {

    private static Logger logger = LoggerFactory.getLogger(NotifyPolicyUtil.class);

    /**
     * @param policyConfig       通知策略配置信息
     * @param paramMappingList   引用通知策略时参数映射
     * @param notifyTriggerType  触发类型
     * @param templateParamData  模板参数数据
     * @param conditionParamData 条件参数数据
     * @param receiverMap        可能用到的通知接收对象集合
     * @return void
     * @Author: linbq
     * @Time:2020年7月2日
     * @Description: 执行通知策略
     */
    public static void execute(String notifyPolicyHandler, INotifyTriggerType notifyTriggerType, Class<? extends IMessageHandler> newsHandlerClass, NotifyPolicyConfigVo policyConfig, List<ParamMappingVo> paramMappingList,
                               JSONObject templateParamData, JSONObject conditionParamData,
                               Map<String, List<NotifyReceiverVo>> receiverMap) {
        /** 异常通知用户uuid列表 **/
        List<String> adminUserUuidList = policyConfig.getAdminUserUuidList();
        /** 触发动作列表 **/
        List<NotifyTriggerVo> triggerList = policyConfig.getTriggerList();
        for (NotifyTriggerVo triggerObj : triggerList) {
            /** 找到要触发类型对应的信息 **/
            if (notifyTriggerType.getTrigger().equals(triggerObj.getTrigger())) {
                /** 通知列表 **/
                List<NotifyTriggerNotifyVo> notifyList = triggerObj.getNotifyList();
                if (CollectionUtils.isNotEmpty(notifyList)) {
                    /** 模板列表 **/
                    List<NotifyTemplateVo> templateList = policyConfig.getTemplateList();
                    Map<Long, NotifyTemplateVo> templateMap =
                            templateList.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
                    for (NotifyTriggerNotifyVo notifyObj : notifyList) {
                        /** 条件表达式配置信息，当表达式结果为true时，才发送通知 **/
                        ConditionConfigVo conditionConfig = notifyObj.getConditionConfig();
                        List<ConditionGroupVo> conditionGroupList = conditionConfig.getConditionGroupList();
                        if (CollectionUtils.isNotEmpty(conditionGroupList)) {
                            /** 参数映射 **/
                            if (CollectionUtils.isNotEmpty(paramMappingList)) {
                                for (ParamMappingVo paramMappingVo : paramMappingList) {
                                    if ("constant".equals(paramMappingVo.getType())) {
                                        conditionParamData.put(paramMappingVo.getName(), paramMappingVo.getValue());
                                    }
                                }
                            }

                            try {
                                ConditionParamContext.init(conditionParamData);
                                /* 解析条件表达式，生成javascript脚本，如(true&&false)&&(false||true)||(false||true) **/
                                String script = conditionConfig.buildScript();
                                // System.out.println(script);
                                /* 运行javascript脚本，结果为true，则继续执行下面的发送通知逻辑，结果为false，则跳过，不发送通知 **/
                                if (!RunScriptUtil.runScript(script)) {
                                    continue;
                                }
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            } finally {
                                ConditionParamContext.get().release();
                            }
                        }
                        /* 通知动作列表 **/
                        List<NotifyActionVo> actionList = notifyObj.getActionList();
                        for (NotifyActionVo actionObj : actionList) {
                            /* 接收人列表 **/
                            List<String> receiverList = actionObj.getReceiverList();
                            if (CollectionUtils.isNotEmpty(receiverList)) {
                                String notifyHandler = actionObj.getNotifyHandler();
                                INotifyHandler handler = NotifyHandlerFactory.getHandler(notifyHandler);
                                if (handler == null) {
                                    throw new NotifyHandlerNotFoundException(notifyHandler);
                                }
                                NotifyVo.Builder notifyBuilder = new NotifyVo.Builder(notifyTriggerType, newsHandlerClass, notifyPolicyHandler);
                                /** 设置异常通知接收人 **/
                                if (CollectionUtils.isNotEmpty(adminUserUuidList)) {
                                    notifyBuilder.setExceptionNotifyUserUuidList(adminUserUuidList);
                                }
                                /** 设置通知模板 **/
                                Long templateId = actionObj.getTemplateId();
                                if (templateId != null) {
                                    NotifyTemplateVo notifyTemplateVo = templateMap.get(templateId);
                                    if (notifyTemplateVo != null) {
                                        notifyBuilder.withContentTemplate(notifyTemplateVo.getContent());
                                        notifyBuilder.withTitleTemplate(notifyTemplateVo.getTitle());
                                    }
                                }
                                /** 注入流程作业信息 不够将来再补充 **/
                                notifyBuilder.addAllData(templateParamData);
                                notifyBuilder.addData("notifyTriggerType", notifyTriggerType.getText());
                                /** 参数映射 **/
                                if (CollectionUtils.isNotEmpty(paramMappingList)) {
                                    for (ParamMappingVo paramMappingVo : paramMappingList) {
                                        /** 临时增加逻辑 默认参数不能自定义值 **/
                                        if (templateParamData.containsKey(paramMappingVo.getName())) {
                                            continue;
                                        }
                                        /** end **/
                                        if ("constant".equals(paramMappingVo.getType())) {
                                            notifyBuilder.addData(paramMappingVo.getName(), paramMappingVo.getValue());
                                        } else if (Objects.equals(paramMappingVo.getName(),
                                                paramMappingVo.getValue())) {
                                            if (!templateParamData.containsKey(paramMappingVo.getValue())) {
                                                logger.error("没有找到工单参数'" + paramMappingVo.getValue() + "'信息");
                                            }
                                        } else {
                                            Object processFieldValue = templateParamData.get(paramMappingVo.getValue());
                                            if (processFieldValue != null) {
                                                notifyBuilder.addData(paramMappingVo.getName(), processFieldValue);
                                            } else {
                                                logger.error("没有找到参数'" + paramMappingVo.getValue() + "'信息");
                                            }
                                        }
                                    }
                                }
                                /** 注入结束 **/

                                /** 设置正常接收人 **/
                                for (String receiver : receiverList) {
                                    String[] split = receiver.split("#");
                                    if (GroupSearch.USER.getValue().equals(split[0])) {
                                        notifyBuilder.addUserUuid(split[1]);
                                    } else if (GroupSearch.TEAM.getValue().equals(split[0])) {
                                        notifyBuilder.addTeamUuid(split[1]);
                                    } else if (GroupSearch.ROLE.getValue().equals(split[0])) {
                                        notifyBuilder.addRoleUuid(split[1]);
                                    } else {
                                        List<NotifyReceiverVo> notifyReceiverList = receiverMap.get(split[1]);
                                        if (CollectionUtils.isNotEmpty(notifyReceiverList)) {
                                            for (NotifyReceiverVo notifyReceiverVo : notifyReceiverList) {
                                                if (GroupSearch.USER.getValue().equals(notifyReceiverVo.getType())) {
                                                    notifyBuilder.addUserUuid(notifyReceiverVo.getUuid());
                                                } else if (GroupSearch.TEAM.getValue()
                                                        .equals(notifyReceiverVo.getType())) {
                                                    notifyBuilder.addTeamUuid(notifyReceiverVo.getUuid());
                                                } else if (GroupSearch.ROLE.getValue()
                                                        .equals(notifyReceiverVo.getType())) {
                                                    notifyBuilder.addRoleUuid(notifyReceiverVo.getUuid());
                                                }
                                            }
                                        }
                                    }
                                }

                                NotifyVo notifyVo = notifyBuilder.build();
                                /** 发送通知 **/
                                handler.execute(notifyVo);
                            }
                        }
                    }
                }
                break;
            }
        }
    }
}
