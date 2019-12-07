package codedriver.framework.restful.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.restful.annotation.Example;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.NotDefined;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;

public class ApiHelpBase {
	private static Logger logger = LoggerFactory.getLogger(ApiHelpBase.class);
	public final JSONObject getApiComponentHelp(Class<?>... arg) {
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
								paramObj.put("参数", p.name());
								paramObj.put("类型", p.type().getValue() + "(" + p.type().getText() + ")");
								paramObj.put("是否必填", p.isRequired());
								paramObj.put("说明", p.desc());
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
														paramObj.put("参数", entityField.name());
														paramObj.put("类型", entityField.type().getValue() + "(" + entityField.type().getText() + ")");
														paramObj.put("说明", entityField.desc());
														outputList.add(paramObj);
														break;
													}
												}
											}
										}
									} else {
										JSONObject paramObj = new JSONObject();
										paramObj.put("参数", p.name());
										paramObj.put("类型", ApiParamType.JSONARRAY.getValue() + "(" + ApiParamType.JSONARRAY.getText() + ")");
										JSONArray elementObjList = new JSONArray();
										for (Field field : p.explode().getComponentType().getDeclaredFields()) {
											Annotation[] annotations = field.getDeclaredAnnotations();
											if (annotations != null && annotations.length > 0) {
												for (Annotation annotation : annotations) {
													if (annotation.annotationType().equals(EntityField.class)) {
														EntityField entityField = (EntityField) annotation;
														JSONObject elementObj = new JSONObject();
														elementObj.put("参数", entityField.name());
														elementObj.put("类型", entityField.type().getValue() + "(" + entityField.type().getText() + ")");
														elementObj.put("说明", entityField.desc());
														elementObjList.add(elementObj);
														break;
													}
												}
											}
										}
										paramObj.put("成员", elementObjList);
										paramObj.put("说明", p.desc());
										outputList.add(paramObj);
									}
								} else {
									JSONObject paramObj = new JSONObject();
									paramObj.put("参数", p.name());
									paramObj.put("类型", p.type().getValue() + "(" + p.type().getText() + ")");
									paramObj.put("说明", p.desc());
									outputList.add(paramObj);
								}
							}
						}
					} else if (anno.annotationType().equals(Description.class)) {
						Description description = (Description) anno;
						jsonObj.put("接口说明", description.desc());
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
							jsonObj.put("范例", content);
						}
					}
				}
			}
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error(e.getMessage());
		}
		if (!outputList.isEmpty()) {
			jsonObj.put("输出参数", outputList);
		}
		if (!inputList.isEmpty()) {
			jsonObj.put("输入参数", inputList);
		}
		return jsonObj;
	}
}
