package codedriver.framework.restful.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.util.IpUtil;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Example;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;
import codedriver.framework.threadlocal.UserContext;

public abstract class ApiComponentBase implements ApiComponent, MyApiComponent {
	private static final Logger logger = LoggerFactory.getLogger(ApiComponentBase.class.getName());

	@Autowired
	private ApiMapper restMapper;

	public final Object doService(ApiVo interfaceVo, JSONObject jsonObj) throws Exception {
		String error = "";
		Object result = null;
		boolean status = false;
		long startTime = System.currentTimeMillis();
		String requestIp = IpUtil.getIpAddr(UserContext.get().getRequest());

		try {
			try {
				Object proxy = AopContext.currentProxy();
				Method method = proxy.getClass().getMethod("myDoService", JSONObject.class);
				result = method.invoke(proxy, jsonObj);
			} catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
				result = myDoService(jsonObj);
			} catch (Exception ex) {
				throw ex;
			}
			status = true;
		} catch (Exception e) {
			error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
			throw e;
		} finally {
			if (interfaceVo.getNeedAudit() != null && interfaceVo.getNeedAudit().equals(1)) {
				
			}
		}
		return result;
	}

	public final String getId() {
		return this.getClass().getName();
	}

	@Override
	public final JSONObject help() {
		JSONObject jsonObj = new JSONObject();
		JSONArray inputList = new JSONArray();
		JSONArray outputList = new JSONArray();
		try {
			Method method = this.getClass().getDeclaredMethod("myDoService", JSONObject.class);
			if (method != null && method.isAnnotationPresent(Input.class) || method.isAnnotationPresent(Output.class) || method.isAnnotationPresent(Description.class)) {
				for (Annotation anno : method.getDeclaredAnnotations()) {
					if (anno.annotationType().equals(Input.class)) {
						Input input = (Input) anno;
						Param[] params = input.value();
						if (params != null && params.length > 0) {
							for (Param p : params) {
								JSONObject paramObj = new JSONObject();
								paramObj.put("参数", p.name());
								paramObj.put("类型", p.type());
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
								JSONObject paramObj = new JSONObject();
								paramObj.put("参数", p.name());
								paramObj.put("类型", p.type());
								paramObj.put("说明", p.desc());
								outputList.add(paramObj);
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
		if (!inputList.isEmpty()) {
			jsonObj.put("输入参数", inputList);
		}
		if (!outputList.isEmpty()) {
			jsonObj.put("输出参数", outputList);
		}
		return jsonObj;
	}

}
