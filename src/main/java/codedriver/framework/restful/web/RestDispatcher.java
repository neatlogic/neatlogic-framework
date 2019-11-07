package codedriver.framework.restful.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.TestMapper;
import codedriver.framework.service.TestService;
import codedriver.framework.startup.DatasourceInitializer;

@Controller
@RequestMapping("/restservices/")
public class RestDispatcher {
	Logger logger = LoggerFactory.getLogger(RestDispatcher.class);

	@Autowired
	private TestMapper testMapper;

	@Autowired
	private TestService testService;

	@Autowired
	private DatasourceInitializer datasourceInitializer;

	private static Map<Integer, String> errorMap = new HashMap<Integer, String>();

	public RestDispatcher() {
		errorMap.put(408, "请求已超时");
		errorMap.put(400, "由于包含语法错误，当前请求无法被服务器理解");
		errorMap.put(412, "头信息不符合要求");
		errorMap.put(410, "服务不可用");
		errorMap.put(401, "用户验证失败");
		errorMap.put(403, "禁止访问");
		errorMap.put(470, "访问频率过高，请稍后访问");
	}

	private void doIt(HttpServletRequest request, String token, String json, JSONObject jsonObj, String action) throws Exception {

	}

	@RequestMapping(value = "/**", method = RequestMethod.GET)
	public void dispatcherForPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
		if (token.equals("adduser")) {
			datasourceInitializer.init();
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("Status", "OK");
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(jsonObj);
		} else {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("name", testMapper.test());
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(jsonObj);
		}
	}

}
