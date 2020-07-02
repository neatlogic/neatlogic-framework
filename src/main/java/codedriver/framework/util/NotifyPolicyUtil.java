package codedriver.framework.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.ConditionParamContext;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dto.condition.ConditionConfigVo;
import codedriver.framework.notify.core.INotifyHandler;
import codedriver.framework.notify.core.INotifyTriggerType;
import codedriver.framework.notify.core.NotifyHandlerFactory;
import codedriver.framework.notify.dto.NotifyReceiverVo;
import codedriver.framework.notify.dto.NotifyTemplateVo;
import codedriver.framework.notify.dto.NotifyVo;
import codedriver.framework.notify.dto.ParamMappingVo;
import codedriver.framework.notify.exception.NotifyHandlerNotFoundException;

public class NotifyPolicyUtil {
	
	private static Logger logger = LoggerFactory.getLogger(NotifyPolicyUtil.class);
	
	/**
	 * 
	* @Author: 14378
	* @Time:2020年7月2日
	* @Description: 执行通知策略
	* @param policyConfig 通知策略配置信息
	* @param paramMappingList 引用通知策略时参数映射
	* @param notifyTriggerType 触发类型
	* @param templateParamData 模板参数数据
	* @param conditionParamData 条件参数数据
	* @param receiverMap 可能用到的通知接收对象集合
	* @return void
	 */
	public static void execute(JSONObject policyConfig, List<ParamMappingVo> paramMappingList, INotifyTriggerType notifyTriggerType, JSONObject templateParamData, JSONObject conditionParamData, Map<String, List<NotifyReceiverVo>> receiverMap) {
		if (MapUtils.isNotEmpty(policyConfig)) {
			List<String> adminUserUuidList = JSON.parseArray(policyConfig.getJSONArray("adminUserUuidList").toJSONString(), String.class);
			JSONArray triggerList = policyConfig.getJSONArray("triggerList");
			for (int i = 0; i < triggerList.size(); i++) {
				JSONObject triggerObj = triggerList.getJSONObject(i);
				if (notifyTriggerType.getTrigger().equalsIgnoreCase(triggerObj.getString("trigger"))) {
					JSONArray notifyList = triggerObj.getJSONArray("notifyList");
					if (CollectionUtils.isNotEmpty(notifyList)) {
						Map<Long, NotifyTemplateVo> templateMap = new HashMap<>();
						List<NotifyTemplateVo> templateList = JSON.parseArray(policyConfig.getJSONArray("templateList").toJSONString(), NotifyTemplateVo.class);
						for (NotifyTemplateVo notifyTemplateVo : templateList) {
							templateMap.put(notifyTemplateVo.getId(), notifyTemplateVo);
						}
						for (int j = 0; j < notifyList.size(); j++) {
							JSONObject notifyObj = notifyList.getJSONObject(j);
							JSONObject conditionConfig = notifyObj.getJSONObject("conditionConfig");
							if (MapUtils.isNotEmpty(conditionConfig)) {
								JSONArray conditionGroupList = conditionConfig.getJSONArray("conditionGroupList");
								if (CollectionUtils.isNotEmpty(conditionGroupList)) {
//									JSONObject processFieldData = ProcessTaskUtil.getProcessFieldData(processTaskVo, true);
									// 参数映射
									if (CollectionUtils.isNotEmpty(paramMappingList)) {
										for (ParamMappingVo paramMappingVo : paramMappingList) {
											if ("constant".equals(paramMappingVo.getType())) {
												conditionParamData.put(paramMappingVo.getName(), paramMappingVo.getValue());
											} else if (Objects.equals(paramMappingVo.getName(), paramMappingVo.getValue())) {
												if (!conditionParamData.containsKey(paramMappingVo.getValue())) {
													logger.error("没有找到工单参数'" + paramMappingVo.getValue() + "'信息");
												}
											} else {
												Object processFieldValue = conditionParamData.get(paramMappingVo.getValue());
												if (processFieldValue != null) {
													conditionParamData.put(paramMappingVo.getName(), processFieldValue);
												} else {
													logger.error("没有找到参数'" + paramMappingVo.getValue() + "'信息");
												}
											}
										}
									}

									try {
										ConditionParamContext.init(conditionParamData);
										ConditionConfigVo conditionConfigVo = new ConditionConfigVo(conditionConfig);
										String script = conditionConfigVo.buildScript();
										// System.out.println(script);
										if (!RunScriptUtil.runScript(script)) {
											continue;
										}
									} catch (Exception e) {
										logger.error(e.getMessage(), e);
									} finally {
										ConditionParamContext.get().release();
									}
								}
							}
							JSONArray actionList = notifyObj.getJSONArray("actionList");
							for (int k = 0; k < actionList.size(); k++) {
								JSONObject actionObj = actionList.getJSONObject(k);
								List<String> receiverList = JSON.parseArray(actionObj.getJSONArray("receiverList").toJSONString(), String.class);
								if (CollectionUtils.isNotEmpty(receiverList)) {
									String notifyHandler = actionObj.getString("notifyHandler");
									INotifyHandler handler = NotifyHandlerFactory.getHandler(notifyHandler);
									if (handler == null) {
										throw new NotifyHandlerNotFoundException(notifyHandler);
									}
									NotifyVo.Builder notifyBuilder = new NotifyVo.Builder(notifyTriggerType);
									if (CollectionUtils.isNotEmpty(adminUserUuidList)) {
										notifyBuilder.setExceptionNotifyUserUuidList(adminUserUuidList);
									}
									Long templateId = actionObj.getLong("templateId");
									if (templateId != null) {
										NotifyTemplateVo notifyTemplateVo = templateMap.get(templateId);
										if (notifyTemplateVo != null) {
											notifyBuilder.withContentTemplate(notifyTemplateVo.getContent());
											notifyBuilder.withTitleTemplate(notifyTemplateVo.getTitle());
										}
									}
									/** 注入流程作业信息 不够将来再补充 **/
//									JSONObject processFieldData = ProcessTaskUtil.getProcessFieldData(processTaskVo, false);
									notifyBuilder.addAllData(templateParamData);
									// 参数映射
									if (CollectionUtils.isNotEmpty(paramMappingList)) {
										for (ParamMappingVo paramMappingVo : paramMappingList) {
											if ("constant".equals(paramMappingVo.getType())) {
												notifyBuilder.addData(paramMappingVo.getName(), paramMappingVo.getValue());
											} else if (Objects.equals(paramMappingVo.getName(), paramMappingVo.getValue())) {
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
									for (String receiver : receiverList) {
										String[] split = receiver.split("#");
										if (GroupSearch.USER.getValue().equals(split[0])) {
											notifyBuilder.addUserUuid(split[1]);
										} else if (GroupSearch.TEAM.getValue().equals(split[0])) {
											notifyBuilder.addTeamId(split[1]);
										} else if (GroupSearch.ROLE.getValue().equals(split[0])) {
											notifyBuilder.addRoleUuid(split[1]);
										}else {
											List<NotifyReceiverVo> notifyReceiverList = receiverMap.get(split[1]);
											if(CollectionUtils.isNotEmpty(notifyReceiverList)) {
												for(NotifyReceiverVo notifyReceiverVo : notifyReceiverList) {
													if (GroupSearch.USER.getValue().equals(notifyReceiverVo.getType())) {
														notifyBuilder.addUserUuid(notifyReceiverVo.getUuid());
													} else if (GroupSearch.TEAM.getValue().equals(notifyReceiverVo.getType())) {
														notifyBuilder.addTeamId(notifyReceiverVo.getUuid());
													} else if (GroupSearch.ROLE.getValue().equals(notifyReceiverVo.getType())) {
														notifyBuilder.addRoleUuid(notifyReceiverVo.getUuid());
													}
												}
											}
										}
									}
									NotifyVo notifyVo = notifyBuilder.build();
									handler.execute(notifyVo);
								}
							}
						}
					}
				}
			}
		}			
	}
}
