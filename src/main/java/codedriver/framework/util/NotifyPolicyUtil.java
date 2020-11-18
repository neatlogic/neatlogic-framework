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
			/** 异常通知用户uuid列表 **/
			List<String> adminUserUuidList = JSON.parseArray(policyConfig.getJSONArray("adminUserUuidList").toJSONString(), String.class);
			/** 触发动作列表 **/
			JSONArray triggerList = policyConfig.getJSONArray("triggerList");
			for (int i = 0; i < triggerList.size(); i++) {
				JSONObject triggerObj = triggerList.getJSONObject(i);
				/** 找到要触发类型对应的信息**/
				if (notifyTriggerType.getTrigger().equalsIgnoreCase(triggerObj.getString("trigger"))) {
					/** 通知列表 **/
					JSONArray notifyList = triggerObj.getJSONArray("notifyList");
					if (CollectionUtils.isNotEmpty(notifyList)) {
						Map<Long, NotifyTemplateVo> templateMap = new HashMap<>();
						/** 模板列表 **/
						List<NotifyTemplateVo> templateList = JSON.parseArray(policyConfig.getJSONArray("templateList").toJSONString(), NotifyTemplateVo.class);
						for (NotifyTemplateVo notifyTemplateVo : templateList) {
							templateMap.put(notifyTemplateVo.getId(), notifyTemplateVo);
						}
						for (int j = 0; j < notifyList.size(); j++) {
							JSONObject notifyObj = notifyList.getJSONObject(j);
							/** 条件表达式配置信息，当表达式结果为true时，才发送通知 **/
							JSONObject conditionConfig = notifyObj.getJSONObject("conditionConfig");
							if (MapUtils.isNotEmpty(conditionConfig)) {
								JSONArray conditionGroupList = conditionConfig.getJSONArray("conditionGroupList");
								if (CollectionUtils.isNotEmpty(conditionGroupList)) {
									/** 参数映射 **/
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
										/** 解析条件表达式，生成javascript脚本，如(true&&false)&&(false||true)||(false||true) **/
										String script = conditionConfigVo.buildScript();
										// System.out.println(script);
										/** 运行javascript脚本，结果为true，则继续执行下面的发送通知逻辑，结果为false，则跳过，不发送通知 **/
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
							/** 通知动作列表 **/
							JSONArray actionList = notifyObj.getJSONArray("actionList");
							for (int k = 0; k < actionList.size(); k++) {
								JSONObject actionObj = actionList.getJSONObject(k);
								/** 接收人列表**/
								List<String> receiverList = JSON.parseArray(actionObj.getJSONArray("receiverList").toJSONString(), String.class);
								if (CollectionUtils.isNotEmpty(receiverList)) {
									String notifyHandler = actionObj.getString("notifyHandler");
									INotifyHandler handler = NotifyHandlerFactory.getHandler(notifyHandler);
									if (handler == null) {
										throw new NotifyHandlerNotFoundException(notifyHandler);
									}
									NotifyVo.Builder notifyBuilder = new NotifyVo.Builder(notifyTriggerType);
									/** 设置异常通知接收人 **/
									if (CollectionUtils.isNotEmpty(adminUserUuidList)) {
										notifyBuilder.setExceptionNotifyUserUuidList(adminUserUuidList);
									}
									/** 设置通知模板 **/
									Long templateId = actionObj.getLong("templateId");
									if (templateId != null) {
										NotifyTemplateVo notifyTemplateVo = templateMap.get(templateId);
										if (notifyTemplateVo != null) {
											notifyBuilder.withContentTemplate(notifyTemplateVo.getContent());
											notifyBuilder.withTitleTemplate(notifyTemplateVo.getTitle());
										}
									}
									/** 注入流程作业信息 不够将来再补充 **/
									notifyBuilder.addAllData(templateParamData);
									/** 参数映射 **/
									if (CollectionUtils.isNotEmpty(paramMappingList)) {
										for (ParamMappingVo paramMappingVo : paramMappingList) {
										    /** 临时增加逻辑 默认参数不能自定义值 **/
                                            if (templateParamData.containsKey(paramMappingVo.getName())) {
                                                continue;
                                            }
                                            if (templateParamData.containsKey(paramMappingVo.getValue())) {
                                                continue;
                                            }
                                            /** end **/
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
									
									/** 设置正常接收人 **/
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
}
