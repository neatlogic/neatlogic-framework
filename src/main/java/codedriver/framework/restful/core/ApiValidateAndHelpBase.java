package codedriver.framework.restful.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamFactory;
import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.auth.core.AuthActionChecker;
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

public class ApiValidateAndHelpBase {
	private static Logger logger = LoggerFactory.getLogger(ApiValidateAndHelpBase.class);

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
							// 判断是否必填
							if (p.isRequired() && !paramObj.containsKey(p.name())) {
								throw new ParamNotExistsException("参数：“" + p.name() + "”不能为空");
							}
							// 参数类型校验
							Object paramValue = paramObj.get(p.name());
							// 判断长度
							if (p.length() > 0 && paramValue != null && paramValue instanceof String) {
								if (paramValue.toString().length() > p.length()) {
									throw new ParamValueTooLongException(p.name(), paramValue.toString().length(), p.length());
								}
							}
							if (paramValue != null && !ApiParamFactory.getAuthInstance(p.type()).validate(paramValue, p.rule())) {
								throw new ParamIrregularException("参数“" + p.name() + "”不符合格式要求");
							}
						}
					}
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
								paramObj.put("description", p.desc());
								inputList.add(paramObj);
							}
						}
					} else if (anno.annotationType().equals(Output.class)) {
						Output output = (Output) anno;
						Param[] params = output.value();
						if (params != null && params.length > 0) {
							for (Param p : params) {
								if (!p.explode().getName().equals(NotDefined.class.getName())) {
									if (!p.explode().isArray()) {
										for (Field field : p.explode().getDeclaredFields()) {
											Annotation[] annotations = field.getDeclaredAnnotations();
											if (annotations != null && annotations.length > 0) {
												for (Annotation annotation : annotations) {
													if (annotation.annotationType().equals(EntityField.class)) {
														EntityField entityField = (EntityField) annotation;
														JSONObject paramObj = new JSONObject();
														paramObj.put("name", field.getName());
														paramObj.put("type", entityField.type().getValue() + "(" + entityField.type().getText() + ")");
														paramObj.put("description", entityField.name());
														outputList.add(paramObj);
														break;
													}
												}
											}
										}
									} else {
										JSONObject paramObj = new JSONObject();
										paramObj.put("name", p.name());
										paramObj.put("type", ApiParamType.JSONARRAY.getValue() + "(" + ApiParamType.JSONARRAY.getText() + ")");
										JSONArray elementObjList = new JSONArray();
										for (Field field : p.explode().getComponentType().getDeclaredFields()) {
											Annotation[] annotations = field.getDeclaredAnnotations();
											if (annotations != null && annotations.length > 0) {
												for (Annotation annotation : annotations) {
													if (annotation.annotationType().equals(EntityField.class)) {
														EntityField entityField = (EntityField) annotation;
														JSONObject elementObj = new JSONObject();
														elementObj.put("name", field.getName());
														elementObj.put("type", entityField.type().getValue() + "(" + entityField.type().getText() + ")");
														elementObj.put("description", entityField.name());
														elementObjList.add(elementObj);
														break;
													}
												}
											}
										}
										paramObj.put("member", elementObjList);
										paramObj.put("description", p.desc());
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
