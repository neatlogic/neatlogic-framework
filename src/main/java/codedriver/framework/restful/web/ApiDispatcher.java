package codedriver.framework.restful.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.exception.type.ApiNotFoundExceptionMessage;
import codedriver.framework.exception.type.ApiRuntimeException;
import codedriver.framework.exception.type.ComponentNotFoundExceptionMessage;
import codedriver.framework.restful.core.ApiComponent;
import codedriver.framework.restful.core.ApiComponentFactory;
import codedriver.framework.restful.core.JsonStreamApiComponent;
import codedriver.framework.restful.dto.ApiVo;
import codedriver.framework.restful.service.ApiService;

@Controller
@RequestMapping("/api/")
public class ApiDispatcher {
	Logger logger = LoggerFactory.getLogger(ApiDispatcher.class);

	@Autowired
	private ApiService apiService;

	private static Map<Integer, String> errorMap = new HashMap<Integer, String>();

	public ApiDispatcher() {
		errorMap.put(408, "请求已超时");
		errorMap.put(400, "由于包含语法错误，当前请求无法被服务器理解");
		errorMap.put(412, "头信息不符合要求");
		errorMap.put(410, "服务不可用");
		errorMap.put(401, "用户验证失败");
		errorMap.put(403, "禁止访问");
		errorMap.put(470, "访问频率过高，请稍后访问");
	}

	private void doIt(HttpServletRequest request, String token, String json, JSONObject jsonObj, String action) throws Exception {
		TenantContext c = TenantContext.get();
		System.out.print(c.getTenantUuid());
		ApiVo interfaceVo = apiService.getApiByToken(token);
		if (interfaceVo == null || !interfaceVo.getIsActive().equals(1)) {
			throw new ApiRuntimeException(new ApiNotFoundExceptionMessage(token));
		}
		ApiComponent restComponent = ApiComponentFactory.getInstance(interfaceVo.getComponentId());
		if (restComponent != null) {
			if (action.equals("doservice")) {
				JSONObject paramJson = null;
				if (json != null && !"".equals(json.trim())) {
					try {
						paramJson = JSONObject.parseObject(json);
					} catch (Exception e) {
						throw new Exception("参数不是json格式,错误信息为：" + e.getMessage());
					}
				} else {
					paramJson = new JSONObject();
				}

				Object returnObj = restComponent.doService(interfaceVo, paramJson);
				jsonObj.put("Return", returnObj);
				jsonObj.put("Status", "OK");
			} else {
				jsonObj.putAll(restComponent.help());
			}
		} else {
			throw new ApiRuntimeException(new ComponentNotFoundExceptionMessage(interfaceVo.getComponentId()));
		}
	}

	@RequestMapping(value = "/rest/**", method = RequestMethod.GET)
	public void dispatcherForGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

		JSONObject json = new JSONObject();
		Enumeration<String> paraNames = request.getParameterNames();
		while (paraNames.hasMoreElements()) {
			String p = paraNames.nextElement();
			String[] vs = request.getParameterValues(p);
			if (vs.length > 1) {
				json.put(p, vs);
			} else {
				json.put(p, request.getParameter(p));
			}
		}
		JSONObject jsonObj = new JSONObject();
		try {
			doIt(request, token, json.toString(), jsonObj, "doservice");
		} catch (ApiRuntimeException ex) {
			response.setStatus(500);
			jsonObj.put("ErrorCode", ex.getErrorCode());
			jsonObj.put("Status", "ERROR");
			jsonObj.put("Message", ex.getMessage());
			logger.error(ex.getMessage(), ex);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(500);
			jsonObj.put("ErrorCode", 500);
			jsonObj.put("Status", "ERROR");
			jsonObj.put("Message", ExceptionUtils.getStackFrames(ex));

		}
		// zouye: 如果在接口实现中已经处理了输出流，则此处不再处理
		/**
		 * response.isCommitted() == true 的几种情况 1、Response buffer has reached
		 * the max buffer size。 2、Some part of the code has called flushed on
		 * the response 3、Some part of the code has flushed the OutputStream or
		 * Writer 4、If you have forwarded to another page, where the response is
		 * both committed and closed.
		 */
		if (!response.isCommitted()) {
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(jsonObj);
		}
	}

	@RequestMapping(value = "/stream/**", method = RequestMethod.POST)
	public void displatcherForPostStream(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

		JSONObject jsonObj = new JSONObject();
		JSONObject paramObj = new JSONObject();
		Enumeration<String> paraNames = request.getParameterNames();
		while (paraNames.hasMoreElements()) {
			String p = paraNames.nextElement();
			String[] vs = request.getParameterValues(p);
			if (vs.length > 1) {
				paramObj.put(p, vs);
			} else {
				paramObj.put(p, request.getParameter(p));
			}
		}
		ApiVo interfaceVo = apiService.getApiByToken(token);
		if (interfaceVo == null || !interfaceVo.getIsActive().equals(1)) {
			throw new ApiRuntimeException(new ApiNotFoundExceptionMessage(token));
		}
		try {
			JsonStreamApiComponent restComponent = ApiComponentFactory.getStreamInstance(interfaceVo.getComponentId());
			if (restComponent != null) {
				Object returnObj = restComponent.doService(interfaceVo, paramObj, new JSONReader(new InputStreamReader(request.getInputStream(), "utf-8")));
				jsonObj.put("Return", returnObj);
				jsonObj.put("Status", "OK");
			} else {
				// throw new Exception410("插件：" + interfaceVo.getComponentId() +
				// "不存在");
			}
		} catch (ApiRuntimeException ex) {
			response.setStatus(500);
			jsonObj.put("Error", ex.getErrorCode());
			jsonObj.put("Status", "ERROR");
			jsonObj.put("Message", ex.getMessage());
			logger.error(ex.getMessage(), ex);
		} catch (Exception ex) {
			response.setStatus(500);
			jsonObj.put("Error", 500);
			jsonObj.put("Status", "ERROR");
			jsonObj.put("Message", ExceptionUtils.getStackTrace(ex));
			logger.error(ex.getMessage(), ex);
		}
		if (!response.isCommitted()) {
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(jsonObj);
		}
	}

	@RequestMapping(value = "/rest/**", method = RequestMethod.POST)
	public void dispatcherForPost(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

		JSONObject json = null;
		if (jsonStr != null && !"".equals(jsonStr.trim())) {
			try {
				json = JSONObject.parseObject(jsonStr);
			} catch (Exception e) {
				throw new Exception("参数不是json格式,错误信息为：" + e.getMessage());
			}
		} else {
			json = new JSONObject();
		}

		Enumeration<String> paraNames = request.getParameterNames();
		while (paraNames.hasMoreElements()) {
			String p = paraNames.nextElement();
			String[] vs = request.getParameterValues(p);
			if (vs.length > 1) {
				json.put(p, vs);
			} else {
				json.put(p, request.getParameter(p));
			}
		}
		JSONObject jsonObj = new JSONObject();
		try {
			doIt(request, token, json.toString(), jsonObj, "doservice");
		} catch (ApiRuntimeException ex) {
			response.setStatus(500);
			jsonObj.put("Error", ex.getErrorCode());
			jsonObj.put("Status", "ERROR");
			jsonObj.put("Message", ex.getMessage());
			logger.error(ex.getMessage(), ex);
		} catch (Exception ex) {
			response.setStatus(500);
			jsonObj.put("Error", 500);
			jsonObj.put("Status", "ERROR");
			jsonObj.put("Message", ExceptionUtils.getStackTrace(ex));
			Throwable a = ex.getCause();
//			logger.error(ex.getCause().getMessage(), ex.getCause());
			logger.error(ex.getMessage(), ex);
		}
		if (!response.isCommitted()) {
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(jsonObj);
		}
	}

	@RequestMapping(value = "/help/**", method = RequestMethod.GET)
	public void help(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

		JSONObject jsonObj = new JSONObject();
		try {
			ApiVo interfaceVo = apiService.getApiByToken(token);
			ApiComponent restComponent = ApiComponentFactory.getInstance(interfaceVo.getComponentId());
			if (restComponent != null) {
				jsonObj.putAll(restComponent.help());
			} else {
				JsonStreamApiComponent restStreamComponent = ApiComponentFactory.getStreamInstance(interfaceVo.getComponentId());
				if (restStreamComponent != null) {
					jsonObj.putAll(restStreamComponent.help());
				} else {
					throw new ApiRuntimeException(new ComponentNotFoundExceptionMessage(interfaceVo.getComponentId()));
				}
			}
		} catch (Exception ex) {
			jsonObj.put("Error", 300);
			jsonObj.put("Status", "ERROR");
			jsonObj.put("Message", ex.getMessage());
			logger.error(ex.getMessage(), ex);
		}

		response.setContentType(Config.RESPONSE_TYPE_JSON);
		response.getWriter().print(jsonObj.toJSONString(4));
	}

}
