package codedriver.framework.integration.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.integration.authtication.core.AuthenticateHandlerFactory;
import codedriver.framework.integration.authtication.core.IAuthenticateHandler;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.util.FreemarkerUtil;

public abstract class IntegrationHandlerBase<T> implements IIntegrationHandler<T> {
	static Logger logger = LoggerFactory.getLogger(IntegrationHandlerBase.class);

	static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			return;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			return;
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	} };

	private static class NullHostNameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String paramString, SSLSession paramSSLSession) {
			return true;
		}
	}

	private static String sendRequest(IntegrationVo integrationVo) {
		String url = integrationVo.getUrl();
		JSONObject config = integrationVo.getConfig();
		JSONObject otherConfig = config.getJSONObject("other");
		JSONObject inputConfig = config.getJSONObject("input");
		JSONObject authConfig = config.getJSONObject("authentication");
		JSONArray headConfig = config.getJSONArray("head");
		JSONObject outputConfig = config.getJSONObject("output");
		String returnString = "";
		JSONObject paramObj = integrationVo.getParamObj();
		if (paramObj == null) {
			paramObj = new JSONObject();
		}
		HttpURLConnection connection = null;
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			/*
			 * // 设置参数 JSONObject requestParamObj = new JSONObject(); if (paramConfig !=
			 * null) { for (int i = 0; i < paramConfig.size(); i++) { JSONObject item =
			 * paramConfig.getJSONObject(i); String key = item.getString("key"); String type
			 * = item.getString("type"); String value = item.getString("value"); if
			 * (type.equals("custom")) { } else if (type.equals("mapping")) {
			 * requestParamObj.put(key, paramObj.get(value)); } } }
			 * 
			 * // 把参数设到URL上 if (!requestParamObj.isEmpty()) { String queryString = "";
			 * Iterator<String> itKey = requestParamObj.keySet().iterator(); while
			 * (itKey.hasNext()) { if (StringUtils.isNotBlank(queryString)) { queryString +=
			 * "&"; } String key = itKey.next(); queryString += key + "=" +
			 * URLEncoder.encode(requestParamObj.getString(key), "utf-8"); } if
			 * (StringUtils.isNotBlank(queryString)) { url = url + "?" + queryString; } }
			 */

			URL getUrl = new URL(url);
			connection = (HttpURLConnection) getUrl.openConnection();
			// 设置http method
			connection.setRequestMethod(integrationVo.getMethod());
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// 设置验证
			if (authConfig != null) {
				String type = authConfig.getString("type");
				JSONObject authConf = authConfig.getJSONObject("config");
				IAuthenticateHandler handler = AuthenticateHandlerFactory.getHandler(type);
				if (handler != null) {
					handler.authenticate(connection, authConf);
				}
			}

			// 设置超时时间
			if (otherConfig != null) {
				if (otherConfig.containsKey("connectTimeout")) {
					connection.setConnectTimeout(otherConfig.getIntValue("connectTimeout"));
				}
				if (otherConfig.containsKey("readTimeout")) {
					connection.setReadTimeout(otherConfig.getIntValue("readTimeout"));
				}
			}

			// 设置head
			if (headConfig != null) {
				for (int i = 0; i < headConfig.size(); i++) {
					JSONObject item = headConfig.getJSONObject(i);
					String key = item.getString("key");
					String type = item.getString("type");
					String value = item.getString("value");
					if (type.equals("custom")) {
						connection.setRequestProperty(key, value);
					} else if (type.equals("mapping")) {
						connection.setRequestProperty(key, paramObj.getString(value));
					}

				}
			}

			connection.connect();
		} catch (Exception e) {
			logger.error("connect: " + url + " failed", e);
		}
		if (connection != null) {
			// 转换输入参数
			// if (integrationVo.getMethod().equals(HttpMethod.POST.toString())) {
			if (inputConfig != null) {
				String content = inputConfig.getString("content");
				// 内容不为空代表需要通过freemarker转换
				if (StringUtils.isNotBlank(content)) {
					content = FreemarkerUtil.transform(integrationVo.getParamObj(), content);
				} else {
					content = integrationVo.getParamObj().toJSONString();
				}
				try (DataOutputStream out = new DataOutputStream(connection.getOutputStream());) {
					out.write(content.toString().getBytes("utf-8"));
				} catch (Exception e) {
					logger.error("http error :" + e.getMessage(), e);
				}
			}
			// }

			// 处理返回值
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));) {
				int code = connection.getResponseCode();
				if (String.valueOf(code).startsWith("2")) {
					StringBuilder result = new StringBuilder();
					String lines;
					while ((lines = reader.readLine()) != null) {
						result.append(lines);
					}
					returnString = result.toString();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			if (outputConfig != null && StringUtils.isNotBlank(returnString)) {
				String content = outputConfig.getString("content");
				if (StringUtils.isNotBlank(content)) {
					JSONObject output = new JSONObject();
					returnString = returnString.trim();
					if (returnString.startsWith("{")) {
						output.put("Return", JSONObject.parseObject(returnString));
					} else if (returnString.startsWith("[")) {
						output.put("Return", JSONArray.parseArray(returnString));
					}
					returnString = FreemarkerUtil.transform(output, content);
				}
			}
		}
		// connection.disconnect(); //Indicates that other requests to the
		// server are unlikely in the near future. Calling disconnect() should
		// not imply that this HttpURLConnection
		// instance can be reused for other requests.
		return returnString;
	}

	public final T getData(IntegrationVo integrationVo) {
		String result = sendRequest(integrationVo);
		return myGetData(result);
	}

	protected abstract T myGetData(String result);

}
