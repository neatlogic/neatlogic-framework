package codedriver.framework.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;

public final class ReturnJson {

	private static final String TSPAGE_NEEDPAGE = "TSPAGE_NEEDPAGE";
	private static final String TSPAGE_PAGESIZE = "TSPAGE_PAGESIZE";
	private static final String TSPAGE_CURRENTPAGE = "TSPAGE_CURRENTPAGE";
	private static final String TSPAGE_PAGECOUNT = "TSPAGE_PAGECOUNT";
	private static final String TSPAGE_ROWNUM = "TSPAGE_ROWNUM";

	public static void error(Throwable exception, HttpServletResponse response) {
		String message = "";
		if (exception.getMessage() != null) {
			message = exception.getMessage();
		} else {
			message = ExceptionUtils.getStackTrace(exception);
		}
		error(message, null, response);
	}

	public static void error(String message, HttpServletResponse response) {
		error(message, null, response);
	}

	public static void error(String message, JSONObject data, HttpServletResponse response) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("Status", "ERROR");
		jsonObj.put("Message", message);
		if (data != null) {
			Iterator<String> iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				jsonObj.put(key, data.get(key));
			}
		}
		writeOut(response, jsonObj);
	}

	public static void success(HttpServletResponse response) {
		success(null, response);
	}

	public static void success(JSONObject data, HttpServletResponse response) {
		if (data == null) {
			data = new JSONObject();
		}
		data.put("Status", "OK");

		writeOut(response, data);
	}

	public static void page(String key, List list, HttpServletResponse response) {
		JSONObject data = new JSONObject();
		data.put(key, list);
		page(data, response);
	}

	public static void page(JSONObject data, HttpServletResponse response) {
		UserContext context = UserContext.get();
		HttpServletRequest request;
		if (context != null) {
			request = context.getRequest();
			if (response != null && request != null) {
				page(data, request, response);
			}
		}
	}

	public static void page(JSONObject data, HttpServletRequest request, HttpServletResponse response) {
		if (data == null) {
			data = new JSONObject();
		}

		Boolean needPage = (Boolean) request.getAttribute(TSPAGE_NEEDPAGE);
		if (needPage) {
			data.put("pageSize", request.getAttribute(TSPAGE_PAGESIZE));
			data.put("currentPage", request.getAttribute(TSPAGE_CURRENTPAGE));
			data.put("pageCount", request.getAttribute(TSPAGE_PAGECOUNT));
			data.put("rowNum", request.getAttribute(TSPAGE_ROWNUM));
		}
		writeOut(response, data);
	}

	private static void writeOut(HttpServletResponse response, JSONObject jsonObj) {
		response.setContentType(Config.RESPONSE_TYPE_JSON);
		try {
			PrintWriter out = response.getWriter();
			out.print(jsonObj.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
