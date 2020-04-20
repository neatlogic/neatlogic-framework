package codedriver.framework.integration.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.bazaarvoice.jolt.Chainr;

import codedriver.framework.integration.authentication.costvalue.HttpMethod;
import codedriver.framework.integration.authtication.contenttype.core.ContentTypeHandlerFactory;
import codedriver.framework.integration.authtication.contenttype.core.IContentTypeHandler;
import codedriver.framework.integration.authtication.core.AuthenticateHandlerFactory;
import codedriver.framework.integration.authtication.core.IAuthenticateHandler;
import codedriver.framework.integration.dto.IntegrationVo;

public abstract class IntegrationHandlerBase<T> implements IIntegrationHandler {
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

	private static String sendRequest(IntegrationVo integrationVo) {
		String url = integrationVo.getUrl();
		JSONObject config = integrationVo.getConfig();
		JSONObject otherConfig = config.getJSONObject("other");
		JSONArray paramConfig = config.getJSONArray("param");
		JSONObject authConfig = config.getJSONObject("authentication");
		JSONArray headConfig = config.getJSONArray("head");
		JSONObject bodyConfig = config.getJSONObject("body");
		StringBuilder result = new StringBuilder();
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

			// 设置参数
			JSONObject requestParamObj = new JSONObject();
			if (paramConfig != null) {
				for (int i = 0; i < paramConfig.size(); i++) {
					JSONObject item = paramConfig.getJSONObject(i);
					String key = item.getString("key");
					String type = item.getString("type");
					String value = item.getString("value");
					if (type.equals("custom")) {
						requestParamObj.put(key, value);
					} else if (type.equals("mapping")) {
						requestParamObj.put(key, paramObj.get(value));
					}
				}
			}

			// 把参数设到URL上
			if (!requestParamObj.isEmpty()) {
				String queryString = "";
				Iterator<String> itKey = requestParamObj.keySet().iterator();
				while (itKey.hasNext()) {
					if (StringUtils.isNotBlank(queryString)) {
						queryString += "&";
					}
					String key = itKey.next();
					queryString += key + "=" + URLEncoder.encode(requestParamObj.getString(key), "utf-8");
				}
				if (StringUtils.isNotBlank(queryString)) {
					url = url + "?" + queryString;
				}
			}

			URL getUrl = new URL(url);
			connection = (HttpURLConnection) getUrl.openConnection();
			// 设置http method
			connection.setRequestMethod(integrationVo.getMethod());
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// 设置验证
			if (authConfig != null) {
				String type = authConfig.getString("type");
				IAuthenticateHandler handler = AuthenticateHandlerFactory.getHandler(type);
				if (handler != null) {
					handler.authenticate(connection, authConfig);
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
			return result.toString();
		}
		if (connection != null) {
			// 设置body
			if (integrationVo.getMethod().equals(HttpMethod.POST.toString())) {
				if (bodyConfig != null) {
					String type = bodyConfig.getString("type");
					IContentTypeHandler contentTypeHandler = ContentTypeHandlerFactory.getHandler(type);
					if (contentTypeHandler != null) {
						String paramStr = contentTypeHandler.handleData(connection, integrationVo.getParamObj(), bodyConfig);
						try (DataOutputStream out = new DataOutputStream(connection.getOutputStream());) {
							out.write(paramStr.toString().getBytes("utf-8"));
						} catch (Exception e) {
							logger.error("http error :" + e.getMessage(), e);
						}
					}
				}
			}

			// 处理返回值
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));) {
				int code = connection.getResponseCode();
				if (String.valueOf(code).startsWith("2")) {
					String lines;
					while ((lines = reader.readLine()) != null) {
						result.append(lines);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		// connection.disconnect(); //Indicates that other requests to the
		// server are unlikely in the near future. Calling disconnect() should
		// not imply that this HttpURLConnection
		// instance can be reused for other requests.
		return result.toString();
	}

	public static void main2(String[] argv) {
		JSONArray specList = new JSONArray();
		JSONObject specObj = new JSONObject();
		specObj.put("operation", "shift");
		specObj.put("spec", new JSONObject() {
			{
				this.put("Return", new JSONObject() {
					{
						this.put("username", "NewReturn.userId");
					}
				});
			}
		});

		specObj.put("operation", "default");
		specObj.put("spec", new JSONObject() {
			{
				this.put("Return", new JSONObject() {
					{
						this.put("username.default", "chenqw");
					}
				});
			}
		});
		specList.add(specObj);
		Chainr chainr = Chainr.fromSpec(specList);

		JSONObject inputJSON = new JSONObject() {
			{
				this.put("Return", new JSONObject() {
					{
						this.put("username", "CHENQW");
					}
				});
			}
		};

		Object transformedOutput = chainr.transform(inputJSON);
		System.out.println(JSONObject.toJSONString(transformedOutput));
	}

	private void transferJson(JSONObject finalObj, JSONObject returnObj) {

	}

	private static Pattern pattern = Pattern.compile("\\[(\\d+)\\]");

	/**
	 * @Author: chenqiwei
	 * @Time:Apr 19, 2020
	 * @Description: TODO 假设returnVal是a.b.[].c，则生成 {"a": {"b": {"*": {"c":"traget"}
	 *               } } }这样的json
	 * @param @param  specObj
	 * @param @param  returnVal
	 * @param @param  target
	 * @param @return
	 * @return JSONObject
	 */
	private static JSONObject buildShiftSpec(JSONObject specObj, String returnVal, String target) {
		String[] returns = returnVal.split("\\.");
		JSONObject currentObj = specObj;
		Map<String, Integer> indexMap = new HashMap<>();
		for (int i = 0; i < returns.length; i++) {
			String re = returns[i];
			if (i == returns.length - 1) {// 存入值
				StringBuffer temp = new StringBuffer();
				Matcher matcher = pattern.matcher(target);
				while (matcher.find()) {
					if (matcher.groupCount() > 0) {
						Integer index = indexMap.get(matcher.group(1));
						if (index != null) {
							target = target.replaceAll("\\[(\\d+)\\]", "[&]");
							matcher.appendReplacement(temp, "[&" + index + "]");
						}
					}
				}
				matcher.appendTail(temp);
				currentObj.put(re, temp.toString());
			} else {// 存入下一层结构
				if (re.matches("\\[\\d+\\]")) {
					// 记录数组位置
					indexMap.put(re.replaceAll("\\[|\\]", ""), returns.length - i - 1);
					re = "*";
				}

				JSONObject nextObj = currentObj.getJSONObject(re);
				if (nextObj == null) {
					nextObj = new JSONObject();
				}
				currentObj.put(re, nextObj);
				currentObj = nextObj;
			}

		}
		return specObj;
	}

	private static JSONObject buildDefaultSpec(JSONObject specObj, String target, String defaultValue) {
		String[] returns = target.split("\\.");
		JSONObject currentObj = specObj;
		for (int i = 0; i < returns.length; i++) {
			String re = returns[i];
			if (i == returns.length - 1) {// 存入值
				currentObj.put(re, defaultValue);
			} else {// 存入下一层结构
				JSONObject nextObj = currentObj.getJSONObject(re);
				if (nextObj == null) {
					nextObj = new JSONObject();
				}
				currentObj.put(re, nextObj);
				currentObj = nextObj;
			}

		}
		return specObj;
	}

	public static void main(String[] a) {
		JSONArray outputConfig = new JSONArray() {
			{
				this.add(new JSONObject() {
					{
						this.put("target", "[10].username");
						this.put("type", "mapping");
						this.put("return", "return.userList.[10].username");
					}
				});
				this.add(new JSONObject() {
					{
						this.put("target", "[10].password");
						this.put("type", "mapping");
						this.put("return", "return.userList.[10].password");
					}
				});
			}
		};

		// 定义转换和默认值两种描述设置
		JSONArray specList = new JSONArray();
		JSONObject shiftSpecObj = new JSONObject();
		shiftSpecObj.put("operation", "shift");
		shiftSpecObj.put("spec", new JSONObject());
		JSONObject defaultSpecObj = new JSONObject();
		defaultSpecObj.put("operation", "default");
		defaultSpecObj.put("spec", new JSONObject());

		for (int i = 0; i < outputConfig.size(); i++) {
			JSONObject item = outputConfig.getJSONObject(i);
			String target = item.getString("target");
			String type = item.getString("type");
			String returnVal = item.getString("return");
			if (type.equals("custom")) {
				JSONObject specObj = defaultSpecObj.getJSONObject("spec");
				specObj = buildDefaultSpec(specObj, returnVal, target);
				defaultSpecObj.getJSONObject("spec").putAll(specObj);
			} else if (type.equals("mapping")) {
				JSONObject specObj = shiftSpecObj.getJSONObject("spec");
				specObj = buildShiftSpec(specObj, returnVal, target);
				shiftSpecObj.getJSONObject("spec").putAll(specObj);
			}
		}
		specList.add(shiftSpecObj);
		specList.add(defaultSpecObj);
		System.out.println(specList.toJSONString());
	}

	public final T getData(IntegrationVo integrationVo) {
		String result = sendRequest(integrationVo);
		if (StringUtils.isNotBlank(result)) {
			if (integrationVo.getConfig() != null) {
				JSONArray outputConfig = integrationVo.getConfig().getJSONArray("outputConfig");
				if (outputConfig != null && outputConfig.size() > 0) {
					// 定义转换和默认值两种描述设置
					JSONObject shiftSpecObj = new JSONObject();
					shiftSpecObj.put("operation", "shift");
					shiftSpecObj.put("spec", new JSONObject());
					JSONObject defaultSpecObj = new JSONObject();
					defaultSpecObj.put("operation", "default");
					defaultSpecObj.put("spec", new JSONObject());

					for (int i = 0; i < outputConfig.size(); i++) {
						JSONObject item = outputConfig.getJSONObject(i);
						String target = item.getString("target");
						String type = item.getString("type");
						String returnVal = item.getString("return");
						if (type.equals("custom")) {
							JSONObject specObj = defaultSpecObj.getJSONObject("spec");
							specObj = buildDefaultSpec(specObj, returnVal, target);
							defaultSpecObj.getJSONObject("spec").putAll(specObj);
						} else if (type.equals("mapping")) {
							JSONObject specObj = shiftSpecObj.getJSONObject("spec");
							specObj = buildShiftSpec(specObj, returnVal, target);
							shiftSpecObj.getJSONObject("spec").putAll(specObj);
						}
					}
				}
			}

			result = result.trim();
			if (result.startsWith("{")) {
				JSONObject resultObj = JSONObject.parseObject(result);

			} else if (result.startsWith("[")) {
				JSONArray resultList = JSONArray.parseArray(result);
			}
		}
		return null;
	}

	protected abstract T myGetData(IntegrationVo integrationVo);

	private static class NullHostNameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String paramString, SSLSession paramSSLSession) {
			return true;
		}
	}
}
