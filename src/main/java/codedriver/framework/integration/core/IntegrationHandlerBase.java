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
import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.util.FreemarkerUtil;

public abstract class IntegrationHandlerBase implements IIntegrationHandler {
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

	protected abstract void beforeSend(IntegrationVo integrationVo);

	protected abstract void afterReturn(IntegrationVo integrationVo);

	public IntegrationResultVo sendRequest(IntegrationVo integrationVo) {
		String url = integrationVo.getUrl();
		JSONObject config = integrationVo.getConfig();
		JSONObject otherConfig = config.getJSONObject("other");
		JSONObject inputConfig = config.getJSONObject("input");
		JSONObject authConfig = config.getJSONObject("authentication");
		JSONArray headConfig = config.getJSONArray("head");
		JSONObject outputConfig = config.getJSONObject("output");
		IntegrationResultVo resultVo = new IntegrationResultVo();
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

			URL getUrl = new URL(url);
			connection = (HttpURLConnection) getUrl.openConnection();
			// 设置http method
			connection.setRequestMethod(integrationVo.getMethod().toUpperCase());
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
					String value = item.getString("value");
					connection.setRequestProperty(key, value);
				}
			}
			// 设置默认header
			connection.setRequestProperty("Content-Type", "application/json; utf-8");

			connection.connect();
		} catch (Exception e) {
			resultVo.appendError(e.getMessage());
		}
		if (connection != null) {
			// 转换输入参数
			// if (integrationVo.getMethod().equals(HttpMethod.POST.toString())) {
			if (inputConfig != null) {
				String content = inputConfig.getString("content");
				// 内容不为空代表需要通过freemarker转换
				if (StringUtils.isNotBlank(content)) {
					try {
						content = FreemarkerUtil.transform(integrationVo.getParamObj(), content);
						resultVo.setTransformedParam(content);
					} catch (Exception ex) {
						resultVo.appendError(ex.getMessage());
					}
				} else {
					content = integrationVo.getParamObj().toJSONString();
				}
				try (DataOutputStream out = new DataOutputStream(connection.getOutputStream());) {
					 out.write(content.toString().getBytes("utf-8"));
					//out.writeBytes(content);
				} catch (Exception e) {
					resultVo.appendError(e.getMessage());
				}
			}
			// }

			// 处理返回值
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));) {
				int code = connection.getResponseCode();
				resultVo.setStatusCode(code);
				if (String.valueOf(code).startsWith("2")) {
					String lines;
					while ((lines = reader.readLine()) != null) {
						resultVo.appendResult(lines);
					}
				} else {
					String lines;
					while ((lines = reader.readLine()) != null) {
						resultVo.appendError(lines);
					}
				}
			} catch (Exception e) {
				resultVo.appendError(e.getMessage());
			}

			if (outputConfig != null && StringUtils.isNotBlank(resultVo.getRawResult())) {
				String content = outputConfig.getString("content");
				if (StringUtils.isNotBlank(content)) {
					try {
						if (resultVo.getRawResult().startsWith("{")) {
							resultVo.setTransformedResult(FreemarkerUtil.transform(JSONObject.parseObject(resultVo.getRawResult()), content));
						} else if (resultVo.getRawResult().startsWith("[")) {
							resultVo.setTransformedResult(FreemarkerUtil.transform(JSONArray.parseArray(resultVo.getRawResult()), content));
						}
					} catch (Exception ex) {
						resultVo.appendError(ex.getMessage());
					}
				}
			}
		}
		// connection.disconnect(); //Indicates that other requests to the
		// server are unlikely in the near future. Calling disconnect() should
		// not imply that this HttpURLConnection
		// instance can be reused for other requests.
		return resultVo;
	}

}
