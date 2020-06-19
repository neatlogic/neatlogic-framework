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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.auth.core.AuthActionChecker;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.util.IpUtil;
import codedriver.framework.exception.type.ParamIrregularException;
import codedriver.framework.exception.type.ParamNotExistsException;
import codedriver.framework.exception.type.ParamValueTooLongException;
import codedriver.framework.exception.type.ParamValueTooShortException;
import codedriver.framework.exception.type.PermissionDeniedException;
import codedriver.framework.param.validate.core.ParamValidatorFactory;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.restful.annotation.Example;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.NotDefined;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.audit.ApiAuditSaveThread;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;

public class ApiValidateAndHelpBase {
	private static Logger logger = LoggerFactory.getLogger(ApiValidateAndHelpBase.class);


	protected void saveAudit(ApiVo apiVo, JSONObject paramObj, Object result, String error, Long startTime, Long endTime) {
		ApiAuditVo audit = new ApiAuditVo();
		audit.setToken(apiVo.getToken());
		audit.setTenant(TenantContext.get().getTenantUuid());
		audit.setStatus(StringUtils.isNotBlank(error) ? ApiAuditVo.FAILED : ApiAuditVo.SUCCEED);
		audit.setServerId(Config.SCHEDULE_SERVER_ID);
		audit.setStartTime(new Date(startTime));
		audit.setEndTime(new Date(endTime));
		audit.setTimeCost(endTime - startTime);
		UserContext userContext = UserContext.get();
		audit.setUserUuid(userContext.getUserUuid(true));
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
		CommonThreadPool.execute(new ApiAuditSaveThread(audit));
	}

	private static void escapeXss(JSONObject paramObj, String key) {
		Object value = paramObj.get(key);
		if (value instanceof String) {
			try {
				JSONObject valObj = JSONObject.parseObject(value.toString());
				escapeXss(valObj);
				paramObj.replace(key, valObj.toJSONString());
			} catch (Exception ex) {
				try {
					JSONArray valList = JSONArray.parseArray(value.toString());
					encodeHtml(valList);
					paramObj.replace(key, valList.toJSONString());
				} catch (Exception e) {
					paramObj.replace(key, escapeXss(value.toString()));
				}
			}
		} else if (value instanceof JSONObject) {
			escapeXss((JSONObject) value);
			paramObj.replace(key, value);
		} else if (value instanceof JSONArray) {
			encodeHtml((JSONArray) value);
			paramObj.replace(key, value);
		}
	}

	//private static Pattern scriptPattern = Pattern.compile("<script(.*?)</script>", Pattern.CASE_INSENSITIVE);
	//private static Pattern javascriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
	//private static Pattern evalPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	private static String escapeXss(String str) {
		if (StringUtils.isNotBlank(str)) {
			// 处理js xss注入
			// str = scriptPattern.matcher(str).replaceAll("");
			// str = javascriptPattern.matcher(str).replaceAll("");
			// str = evalPattern.matcher(str).replaceAll("");
			str = str.replace("&", "&amp;");
			str = str.replace("<", "&lt;");
			str = str.replace(">", "&gt;");
			str = str.replace("'", "&#39;");
			str = str.replace("\"", "&quot;");

			return str;
		}
		return "";
	}

	public static void main(String[] argcv) {
		String a = "<Script>alert</Script>asdasd\neval(\"abc\")df";
		System.out.println(escapeXss(a));
	}

	private static void escapeXss(JSONObject j) {
		Set<String> set = j.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			String newKey = escapeXss(key);
			if (!newKey.equals(key)) {
				Object value = j.get(key);
				j.remove(key);
				j.replace(newKey, value);
			}
			if (j.get(newKey) instanceof JSONObject) {
				escapeXss(j.getJSONObject(newKey));
			} else if (j.get(newKey) instanceof JSONArray) {
				encodeHtml(j.getJSONArray(newKey));
			} else if (j.get(newKey) instanceof String) {
				j.replace(newKey, escapeXss(j.getString(newKey)));
			}
		}
	}

	private static void encodeHtml(JSONArray j) {
		for (int i = 0; i < j.size(); i++) {
			if (j.get(i) instanceof JSONObject) {
				escapeXss(j.getJSONObject(i));
			} else if (j.get(i) instanceof JSONArray) {
				encodeHtml(j.getJSONArray(i));
			} else if (j.get(i) instanceof String) {
				j.set(i, escapeXss(j.getString(i)));
			}
		}
	}

	protected void validApi(Class<?> apiClass, JSONObject paramObj, Class<?>... classes) throws NoSuchMethodException, SecurityException,PermissionDeniedException {
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
							// xss过滤
							if (p.xss() && paramObj.containsKey(p.name())) {
								escapeXss(paramObj, p.name());
							}

							Object paramValue = null;

							if (paramObj.containsKey(p.name())) {
								// 参数类型校验
								paramValue = paramObj.get(p.name());
								// 如果值为null，则remove（前端约定不使用的接口参数会传null过来，故去掉）
								if (paramValue == null) {
									paramObj.remove(p.name());
								}
							}
							// 前后去空格
							if (p.type().equals(ApiParamType.STRING)) {
								if (paramValue != null) {
									paramObj.replace(p.name(), paramValue.toString().trim());
								}
							}
							// 判断是否必填
							if (p.isRequired() && !paramObj.containsKey(p.name())) {
								throw new ParamNotExistsException("参数：“" + p.name() + "”不能为空");
							}

							// 判断最大长度
							if (p.maxLength() > 0 && paramValue != null && paramValue instanceof String) {
								if (paramValue.toString().trim().length() > p.maxLength()) {
									throw new ParamValueTooLongException(p.name(), paramValue.toString().length(), p.maxLength());
								}
							}
							// 判断最小长度
							if (p.minLength() > 0 && paramValue != null && paramValue instanceof String) {
								if (paramValue.toString().trim().length() < p.minLength()) {
									throw new ParamValueTooShortException(p.name(), paramValue.toString().length(), p.minLength());
								}
							}
							if (paramValue != null && !ParamValidatorFactory.getAuthInstance(p.type()).validate(paramValue, p.rule())) {
								throw new ParamIrregularException("参数“" + p.name() + "”不符合格式要求");
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
					paramObj.put("type", entityField.type().getValue() + "[" + entityField.type().getText() + "]");
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
								/*
								 * if (!p.explode().getName().equals(NotDefined.class.getName())) { String
								 * paramNamePrefix = p.name(); if (!p.explode().isArray()) { paramNamePrefix =
								 * StringUtils.isBlank(paramNamePrefix) || "Return".equals(paramNamePrefix) ? ""
								 * : paramNamePrefix + "."; for (Field field : p.explode().getDeclaredFields())
								 * { drawFieldMessageRecursive(field, inputList, true); } } else { JSONObject
								 * paramObj = new JSONObject(); paramObj.put("name", p.name());
								 * paramObj.put("type", ApiParamType.JSONARRAY.getValue() + "[" +
								 * ApiParamType.JSONARRAY.getText() + "]"); paramObj.put("description",
								 * p.desc()); JSONArray elementObjList = new JSONArray(); for (Field field :
								 * p.explode().getComponentType().getDeclaredFields()) {
								 * drawFieldMessageRecursive(field, elementObjList, true); } if
								 * (elementObjList.size() > 0) { paramObj.put("children", elementObjList); }
								 * 
								 * inputList.add(paramObj); } } else {
								 */
								JSONObject paramObj = new JSONObject();
								paramObj.put("name", p.name());
								paramObj.put("type", p.type().getValue() + "[" + p.type().getText() + "]");
								paramObj.put("isRequired", p.isRequired());
								String description = p.desc();
								if (StringUtils.isNotBlank(p.rule())) {
									description = description + "，规则：" + p.rule();
								}
								paramObj.put("description", description);
								inputList.add(paramObj);
								// }
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
										paramObj.put("type", ApiParamType.JSONARRAY.getValue() + "[" + ApiParamType.JSONARRAY.getText() + "]");
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
									paramObj.put("type", p.type().getValue() + "[" + p.type().getText() + "]");
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
