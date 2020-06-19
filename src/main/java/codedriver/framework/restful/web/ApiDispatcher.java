package codedriver.framework.restful.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

import codedriver.framework.common.config.Config;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.exception.type.ApiNotFoundException;
import codedriver.framework.exception.type.ComponentNotFoundException;
import codedriver.framework.exception.type.PermissionDeniedException;
import codedriver.framework.restful.core.ApiComponentFactory;
import codedriver.framework.restful.core.IApiComponent;
import codedriver.framework.restful.core.IBinaryStreamApiComponent;
import codedriver.framework.restful.core.IJsonStreamApiComponent;
import codedriver.framework.restful.dto.ApiHandlerVo;
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

	private void doIt(HttpServletRequest request, HttpServletResponse response, String token, ApiVo.Type apiType, JSONObject paramObj, JSONObject returnObj, String action) throws Exception {
		ApiVo interfaceVo = ApiComponentFactory.getApiByToken(token);

		if (interfaceVo == null) {
			interfaceVo = apiService.getApiByToken(token);
			if (interfaceVo == null || !interfaceVo.getIsActive().equals(1)) {
				throw new ApiNotFoundException("token为“" + token + "”的接口不存在或已被禁用");
			}
		} else if (interfaceVo.getPathVariableObj() != null) {
			// 融合路径参数
			paramObj.putAll(interfaceVo.getPathVariableObj());
		}

		// 判断是否master模块接口，如果是不允许访问
		ApiHandlerVo apiHandlerVo = ApiComponentFactory.getApiHandlerByHandler(interfaceVo.getHandler());
		if (apiHandlerVo != null) {
			if (apiHandlerVo.getModuleId().equals("master")) {
				throw new PermissionDeniedException();
			}
		} else {
			throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
		}

		if (apiType.equals(ApiVo.Type.OBJECT)) {
			IApiComponent restComponent = ApiComponentFactory.getInstance(interfaceVo.getHandler());
			if (restComponent != null) {
				if (action.equals("doservice")) {
					Long starttime = System.currentTimeMillis();
					Object returnV = restComponent.doService(interfaceVo, paramObj);
					Long endtime = System.currentTimeMillis();
					if (!restComponent.isRaw()) {
						returnObj.put("TimeCost", endtime - starttime);
						returnObj.put("Return", returnV);
						returnObj.put("Status", "OK");
					} else {
						returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
					}
				} else {
					returnObj.putAll(restComponent.help());
				}
			} else {
				throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
			}
		} else if (apiType.equals(ApiVo.Type.STREAM)) {
			IJsonStreamApiComponent restComponent = ApiComponentFactory.getStreamInstance(interfaceVo.getHandler());
			if (restComponent != null) {
				if (action.equals("doservice")) {
					Long starttime = System.currentTimeMillis();
					Object returnV = restComponent.doService(interfaceVo, paramObj, new JSONReader(new InputStreamReader(request.getInputStream(), "utf-8")));
					Long endtime = System.currentTimeMillis();
					if (!restComponent.isRaw()) {
						returnObj.put("TimeCost", endtime - starttime);
						returnObj.put("Return", returnV);
						returnObj.put("Status", "OK");
					} else {
						returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
					}
				} else {
					returnObj.putAll(restComponent.help());
				}
			} else {
				throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
			}
		} else if (apiType.equals(ApiVo.Type.BINARY)) {
			IBinaryStreamApiComponent restComponent = ApiComponentFactory.getBinaryInstance(interfaceVo.getHandler());
			if (restComponent != null) {
				if (action.equals("doservice")) {
					Long starttime = System.currentTimeMillis();
					Object returnV = restComponent.doService(interfaceVo, paramObj, request, response);
					Long endtime = System.currentTimeMillis();
					if (!restComponent.isRaw()) {
						returnObj.put("TimeCost", endtime - starttime);
						returnObj.put("Return", returnV);
						returnObj.put("Status", "OK");
					} else {
						returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
					}
				} else {
					returnObj.putAll(restComponent.help());
				}
			} else {
				throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
			}
		}
	}

	@RequestMapping(value = "/rest/**", method = RequestMethod.GET)
	public void dispatcherForGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

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
		JSONObject returnObj = new JSONObject();
		try {
			doIt(request, response, token, ApiVo.Type.OBJECT, paramObj, returnObj, "doservice");
		} catch (ApiRuntimeException ex) {
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (PermissionDeniedException ex) {
			response.setStatus(523);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
		}
		if (!response.isCommitted()) {
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(returnObj);
		}
	}

	@RequestMapping(value = "/rest/**", method = RequestMethod.POST)
	public void dispatcherForPost(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
		JSONObject returnObj = new JSONObject();
		try {
			JSONObject paramObj = null;
			if (StringUtils.isNotBlank(jsonStr)) {
				try {
					paramObj = JSONObject.parseObject(jsonStr);
				} catch (Exception e) {
					throw new ApiRuntimeException("请求参数需要符合JSON格式");
				}
			} else {
				paramObj = new JSONObject();
			}

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

			doIt(request, response, token, ApiVo.Type.OBJECT, paramObj, returnObj, "doservice");
		} catch (ApiRuntimeException ex) {
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (PermissionDeniedException ex) {
			response.setStatus(523);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (Exception ex) {
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ExceptionUtils.getStackTrace(ex));
			logger.error(ex.getMessage(), ex);
		}
		if (!response.isCommitted()) {
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(returnObj);
		}
	}

	@RequestMapping(value = "/stream/**", method = RequestMethod.POST)
	public void displatcherForPostStream(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

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
		JSONObject returnObj = new JSONObject();
		try {
			doIt(request, response, token, ApiVo.Type.STREAM, paramObj, returnObj, "doservice");
		} catch (ApiRuntimeException ex) {
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (PermissionDeniedException ex) {
			response.setStatus(523);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
		}
		if (!response.isCommitted()) {
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(returnObj.toJSONString());
		}
	}

	@RequestMapping(value = "/binary/**")
	public void displatcherForPostBinary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

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
		JSONObject returnObj = new JSONObject();
		try {
			doIt(request, response, token, ApiVo.Type.BINARY, paramObj, returnObj, "doservice");
		} catch (ApiRuntimeException ex) {
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (PermissionDeniedException ex) {
			response.setStatus(523);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
		}
		if (!response.isCommitted()) {
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(returnObj.toJSONString());
		}
	}

	@RequestMapping(value = "/help/rest/**", method = RequestMethod.GET)
	public void resthelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

		JSONObject returnObj = new JSONObject();
		try {
			doIt(request, response, token, ApiVo.Type.OBJECT, null, returnObj, "help");
		} catch (ApiRuntimeException ex) {
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (PermissionDeniedException ex) {
			response.setStatus(523);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
		}
		response.setContentType(Config.RESPONSE_TYPE_JSON);
		response.getWriter().print(returnObj.toJSONString());
	}

	@RequestMapping(value = "/help/stream/**", method = RequestMethod.GET)
	public void steamhelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

		JSONObject returnObj = new JSONObject();
		try {
			doIt(request, response, token, ApiVo.Type.STREAM, null, returnObj, "help");
		} catch (ApiRuntimeException ex) {
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (PermissionDeniedException ex) {
			response.setStatus(523);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
		}
		response.setContentType(Config.RESPONSE_TYPE_JSON);
		response.getWriter().print(returnObj.toJSONString());
	}

	@RequestMapping(value = "/help/binary/**", method = RequestMethod.GET)
	public void binaryhelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());

		JSONObject returnObj = new JSONObject();
		try {
			doIt(request, response, token, ApiVo.Type.BINARY, null, returnObj, "help");
		} catch (ApiRuntimeException ex) {
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (PermissionDeniedException ex) {
			response.setStatus(523);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(520);
			returnObj.put("Status", "ERROR");
			returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
		}
		response.setContentType(Config.RESPONSE_TYPE_JSON);
		response.getWriter().print(returnObj.toJSONString());
	}

}
