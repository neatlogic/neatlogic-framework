package codedriver.framework.restful.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamFactory;
import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.auth.core.AuthActionChecker;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.IpUtil;
import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dto.ConfigVo;
import codedriver.framework.exception.type.ParamIrregularException;
import codedriver.framework.exception.type.ParamNotExistsException;
import codedriver.framework.exception.type.ParamValueTooLongException;
import codedriver.framework.exception.type.PermissionDeniedException;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.restful.annotation.Example;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.NotDefined;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.audit.ApiAuditManager;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;

public class ApiValidateAndHelpBase {
	private static Logger logger = LoggerFactory.getLogger(ApiValidateAndHelpBase.class);
	private static final String API_AUDIT_CONFIG_KEY = "api_audit_config";
	@Autowired
	private ApiMapper apiMapper;

	@Autowired
	private ConfigMapper configMapper;

	protected void saveAudit(ApiVo apiVo, JSONObject paramObj, Object result, String error, Long startTime, Long endTime) {
		ConfigVo configVo = configMapper.getConfigByKey(API_AUDIT_CONFIG_KEY);
		ApiAuditVo audit = new ApiAuditVo();
		audit.setToken(apiVo.getToken());
		audit.setTenant(TenantContext.get().getTenantUuid());
		audit.setStatus(StringUtils.isNotBlank(error) ? ApiAuditVo.FAILED : ApiAuditVo.SUCCEED);
		audit.setServerId(Config.SCHEDULE_SERVER_ID);
		audit.setStartTime(new Date(startTime));
		audit.setEndTime(new Date(endTime));
		audit.setTimeCost(endTime - startTime);
		if (configVo != null && StringUtils.isNotBlank(configVo.getValue())) {
			try {
				JSONObject auditConfig = JSONObject.parseObject(configVo.getValue());
				if (auditConfig.containsKey("savepath")) {
					audit.setLogPath(auditConfig.getString("savepath"));
				}
			} catch (Exception ex) {

			}
		}
		UserContext userContext = UserContext.get();
		audit.setUserId(userContext.getUserId());
		HttpServletRequest request = userContext.getRequest();
		String requestIp = IpUtil.getIpAddr(request);
		audit.setIp(requestIp);
		audit.setAuthtype(apiVo.getAuthtype());
		if (paramObj != null) {
			audit.setParam(paramObj.toJSONString());
		}
		if (error != null) {
			audit.setError(error);
		}
		if (result != null) {
			audit.setResult(result);
		}
		apiMapper.insertApiAudit(audit);
		ApiAuditManager.saveAudit(audit);
	}

	private static void encodeHtml(JSONObject paramObj, String key) {
		Object value = paramObj.get(key);
		if (value instanceof String) {
			try {
				JSONObject valObj = JSONObject.parseObject(value.toString());
				encodeHtml(valObj);
				paramObj.replace(key, valObj.toJSONString());
			} catch (Exception ex) {
				try {
					JSONArray valList = JSONArray.parseArray(value.toString());
					encodeHtml(valList);
					paramObj.replace(key, valList.toJSONString());
				} catch (Exception e) {
					paramObj.replace(key, encodeHtml(value.toString()));
				}
			}
		} else if (value instanceof JSONObject) {
			encodeHtml((JSONObject) value);
			paramObj.replace(key, value);
		} else if (value instanceof JSONArray) {
			encodeHtml((JSONArray) value);
			paramObj.replace(key, value);
		}
	}

	private static String encodeHtml(String str) {
		if (StringUtils.isNotBlank(str)) {
			// str = str.replace("&", "&amp;");
			str = str.replace("<", "&lt;");
			str = str.replace(">", "&gt;");
			str = str.replace("'", "&#39;");
			str = str.replace("\"", "&quot;");
			return str;
		}
		return "";
	}

	private static void encodeHtml(JSONObject j) {
		Set<String> set = j.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			String newKey = encodeHtml(key);
			if (!newKey.equals(key)) {
				Object value = j.get(key);
				j.remove(key);
				j.replace(newKey, value);
			}
			if (j.get(newKey) instanceof JSONObject) {
				encodeHtml(j.getJSONObject(newKey));
			} else if (j.get(newKey) instanceof JSONArray) {
				encodeHtml(j.getJSONArray(newKey));
			} else if (j.get(newKey) instanceof String) {
				j.replace(newKey, encodeHtml(j.getString(newKey)));
			}
		}
	}

	private static void encodeHtml(JSONArray j) {
		for (int i = 0; i < j.size(); i++) {
			if (j.get(i) instanceof JSONObject) {
				encodeHtml(j.getJSONObject(i));
			} else if (j.get(i) instanceof JSONArray) {
				encodeHtml(j.getJSONArray(i));
			} else if (j.get(i) instanceof String) {
				j.set(i, encodeHtml(j.getString(i)));
			}
		}
	}

	protected void validApi(Class<?> apiClass, JSONObject paramObj, Class<?>... classes) throws NoSuchMethodException, SecurityException {
		// 获取目标类
		Boolean isAuth = false;
		if (apiClass != null) {
			AuthAction action = apiClass.getAnnotation(AuthAction.class);
			if (null != action && StringUtils.isNotBlank(action.name())) {
				String actionName = action.name();
				// 判断用户角色是否拥有接口权限
				if (AuthActionChecker.check(actionName)) {
					isAuth = true;
				}
			} else {
				isAuth = true;
			}

			if (!isAuth) {
				throw new PermissionDeniedException();
			}
			// 判断参数是否合法
			Method method = apiClass.getMethod("myDoService", classes);
			if (method != null) {
				Input input = method.getAnnotation(Input.class);
				if (input != null) {
					Param[] params = input.value();
					if (params != null && params.length > 0) {
						for (Param p : params) {
							Object paramValue = null;
							if (paramObj.containsKey(p.name())) {
								// 参数类型校验
								paramValue = paramObj.get(p.name());
								// 如果值为null，则remove（前端约定不使用的接口参数会传null过来，故去掉）
								if (paramValue == null) {
									paramObj.remove(p.name());
								}
							}
							// 判断是否必填
							if (p.isRequired() && !paramObj.containsKey(p.name())) {
								throw new ParamNotExistsException("参数：“" + p.name() + "”不能为空");
							}

							// xss过滤
							if (p.xss() && paramObj.containsKey(p.name())) {
								encodeHtml(paramObj, p.name());
							}

							// 判断长度
							if (p.length() > 0 && paramValue != null && paramValue instanceof String) {
								if (paramValue.toString().length() > p.length()) {
									throw new ParamValueTooLongException(p.name(), paramValue.toString().length(), p.length());
								}
							}
							if (paramValue != null && !ApiParamFactory.getAuthInstance(p.type()).validate(paramValue, p.rule())) {
								throw new ParamIrregularException("参数“" + p.name() + "”不符合格式要求");
							}
							if (p.type().equals(ApiParamType.STRING)) {
								if (paramValue != null) {
									paramObj.put(p.name(), paramValue.toString().trim());
								}
							}

						}
					}
				}
			}
		}
	}

	/**
	 * @Author: chenqiwei
	 * @Time:Apr 1, 2020
	 * @Description: TODO
	 * @param @param field
	 * @param @param paramList
	 * @param @param loop 是否继续递归
	 * @return void
	 */
	private void drawFieldMessageRecursive(Field field, JSONArray paramList, boolean loop) {
		Annotation[] annotations = field.getDeclaredAnnotations();
		if (annotations != null && annotations.length > 0) {
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(EntityField.class)) {
					EntityField entityField = (EntityField) annotation;
					JSONObject paramObj = new JSONObject();
					paramObj.put("name", field.getName());
					paramObj.put("type", entityField.type().getValue() + "(" + entityField.type().getText() + ")");
					paramObj.put("description", entityField.name());

					if (loop && field.getType().isAssignableFrom(List.class)) {
						Type genericType = field.getGenericType();
						if (genericType != null && genericType instanceof ParameterizedType) {
							ParameterizedType parameterizedType = (ParameterizedType) genericType;
							Type actualType = parameterizedType.getActualTypeArguments()[0];
							Class<?> integerClass = (Class<?>) actualType;
							JSONArray subParamList = new JSONArray();
							for (Field subField : integerClass.getDeclaredFields()) {
								drawFieldMessageRecursive(subField, subParamList, false);
							}
							paramObj.put("children", subParamList);
						}
					}
					paramList.add(paramObj);
				}
			}
		}
	}

	protected final JSONObject getApiComponentHelp(Class<?>... arg) {
		JSONObject jsonObj = new JSONObject();
		JSONArray inputList = new JSONArray();
		JSONArray outputList = new JSONArray();
		try {
			Method method = this.getClass().getDeclaredMethod("myDoService", arg);
			if (method != null && method.isAnnotationPresent(Input.class) || method.isAnnotationPresent(Output.class) || method.isAnnotationPresent(Description.class)) {
				for (Annotation anno : method.getDeclaredAnnotations()) {
					if (anno.annotationType().equals(Input.class)) {
						Input input = (Input) anno;
						Param[] params = input.value();
						if (params != null && params.length > 0) {
							for (Param p : params) {
								JSONObject paramObj = new JSONObject();
								paramObj.put("name", p.name());
								paramObj.put("type", p.type().getValue() + "(" + p.type().getText() + ")");
								paramObj.put("isRequired", p.isRequired());
								String description = p.desc();
								if (StringUtils.isNotBlank(p.rule())) {
									description = description + "，规则：" + p.rule();
								}
								paramObj.put("description", description);
								inputList.add(paramObj);
							}
						}
					} else if (anno.annotationType().equals(Output.class)) {
						Output output = (Output) anno;
						Param[] params = output.value();
						if (params != null && params.length > 0) {
							for (Param p : params) {
								if (!p.explode().getName().equals(NotDefined.class.getName())) {
									String paramNamePrefix = p.name();
									if (!p.explode().isArray()) {
										paramNamePrefix = StringUtils.isBlank(paramNamePrefix) || "Return".equals(paramNamePrefix) ? "" : paramNamePrefix + ".";
										for (Field field : p.explode().getDeclaredFields()) {
											drawFieldMessageRecursive(field, outputList, true);
										}
									} else {
										JSONObject paramObj = new JSONObject();
										paramObj.put("name", p.name());
										paramObj.put("type", ApiParamType.JSONARRAY.getValue() + "(" + ApiParamType.JSONARRAY.getText() + ")");
										paramObj.put("description", p.desc());
										JSONArray elementObjList = new JSONArray();
										for (Field field : p.explode().getComponentType().getDeclaredFields()) {
											drawFieldMessageRecursive(field, elementObjList, true);
										}
										if (elementObjList.size() > 0) {
											paramObj.put("children", elementObjList);
										}

										outputList.add(paramObj);
									}
								} else {
									JSONObject paramObj = new JSONObject();
									paramObj.put("name", p.name());
									paramObj.put("type", p.type().getValue() + "(" + p.type().getText() + ")");
									paramObj.put("description", p.desc());
									outputList.add(paramObj);
								}
							}
						}
					} else if (anno.annotationType().equals(Description.class)) {
						Description description = (Description) anno;
						jsonObj.put("description", description.desc());
					} else if (anno.annotationType().equals(Example.class)) {
						Example example = (Example) anno;
						String content = example.example();
						if (StringUtils.isNotBlank(content)) {
							try {
								content = JSONObject.parseObject(content).toString();
							} catch (Exception ex) {
								try {
									content = JSONArray.parseArray(content).toString();
								} catch (Exception ex2) {

								}
							}
							jsonObj.put("example", content);
						}
					}
				}
			}
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error(e.getMessage());
		}
		if (!outputList.isEmpty()) {
			jsonObj.put("output", outputList);
		}
		if (!inputList.isEmpty()) {
			jsonObj.put("input", inputList);
		}
		return jsonObj;
	}
}
