package codedriver.framework.integration.core;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.exception.integration.ParamTypeNotFoundException;
import codedriver.framework.exception.type.ParamIrregularException;
import codedriver.framework.exception.type.ParamNotExistsException;
import codedriver.framework.integration.authtication.core.AuthenticateHandlerFactory;
import codedriver.framework.integration.authtication.core.IAuthenticateHandler;
import codedriver.framework.integration.dto.IntegrationAuditVo;
import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.integration.dto.PatternVo;
import codedriver.framework.param.validate.core.ParamValidatorBase;
import codedriver.framework.param.validate.core.ParamValidatorFactory;
import codedriver.framework.util.FreemarkerUtil;
import codedriver.framework.util.JavascriptUtil;

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

	public IntegrationResultVo sendRequest(IntegrationVo integrationVo, IRequestFrom iRequestFrom) {

		String url = integrationVo.getUrl();
		JSONObject config = integrationVo.getConfig();
		JSONObject otherConfig = config.getJSONObject("other");
		JSONObject inputConfig = config.getJSONObject("input");
		JSONObject authConfig = config.getJSONObject("authentication");
		JSONArray headConfig = config.getJSONArray("head");
		JSONObject outputConfig = config.getJSONObject("output");
		JSONObject paramObj = config.getJSONObject("param");
		JSONObject requestParamObj = integrationVo.getParamObj();
		/**
		 * 校验请求参数开始
		 */
		if (paramObj != null && paramObj.getInteger("needValid") != null && paramObj.getInteger("needValid").equals(1)) {
			List<PatternVo> patternList = null;
			// 包含内置参数
			if (this.hasPattern().equals(1)) {
				patternList = this.getInputPattern();
			} else {// 自定义参数
				patternList = new ArrayList<>();
				JSONArray paramList = paramObj.getJSONArray("paramList");
				if (paramList != null && paramList.size() > 0) {
					for (int i = 0; i < paramList.size(); i++) {
						JSONObject pObj = paramList.getJSONObject(i);
						PatternVo patternVo = JSONObject.toJavaObject(pObj, PatternVo.class);
						patternList.add(patternVo);
					}
				}
			}
			if (patternList != null && patternList.size() > 0) {
				for (PatternVo patternVo : patternList) {
					if (patternVo.getIsRequired() != null && patternVo.getIsRequired().equals(1)) {
						if (!requestParamObj.containsKey(patternVo.getName())) {
							throw new ParamNotExistsException("参数：“" + patternVo.getName() + "”不能为空");
						}

					}
					Object paramValue = requestParamObj.get(patternVo.getName());
					ParamType paramType = ParamType.getParamType(patternVo.getType());
					if (paramType == null) {
						throw new ParamTypeNotFoundException(patternVo.getType());
					}
					if (paramValue != null) {
						ParamValidatorBase validator = ParamValidatorFactory.getAuthInstance(paramType);
						if (validator != null && !validator.validate(paramValue, null)) {
							throw new ParamIrregularException("参数“" + patternVo.getName() + "”不符合格式要求");
						}
					}
				}
			}
		}
		/**
		 * 校验请求参数结束
		 */

		/**
		 * 创建审计记录
		 */
		IntegrationAuditVo integrationAuditVo = new IntegrationAuditVo();
		integrationAuditVo.setRequestFrom(iRequestFrom.toString());
		integrationAuditVo.setUserUuid(UserContext.get().getUserUuid());// 用户非必填，因作业不存在登录用户
		integrationAuditVo.setIntegrationUuid(integrationVo.getUuid());
		integrationAuditVo.setStartTime(new Date());
		integrationAuditVo.setParam(requestParamObj.toString());

		IntegrationResultVo resultVo = new IntegrationResultVo();
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
			connection.setConnectTimeout(0);
			connection.setReadTimeout(0);
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
			connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

			connection.connect();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			integrationAuditVo.appendError(e.getMessage());
			resultVo.appendError(e.getMessage());
			integrationAuditVo.setStatus("failed");
		}
		if (connection != null) {
			// 转换输入参数
			// if (integrationVo.getMethod().equals(HttpMethod.POST.toString())) {
			if (inputConfig != null) {
				String content = inputConfig.getString("content");
				// 内容不为空代表需要通过freemarker转换
				if (StringUtils.isNotBlank(content)) {
					try {
						//content = FreemarkerUtil.transform(integrationVo.getParamObj(), content);
						content = JavascriptUtil.transform(integrationVo.getParamObj(), content);
						resultVo.setTransformedParam(content);
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
						resultVo.appendError(ex.getMessage());
						integrationAuditVo.appendError(ex.getMessage());
						integrationAuditVo.setStatus("failed");
					}
				} else {
					content = integrationVo.getParamObj().toJSONString();
				}
				try (DataOutputStream out = new DataOutputStream(connection.getOutputStream());) {
					out.write(content.toString().getBytes("utf-8"));
					out.flush();
					out.close();
					// out.writeBytes(content);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					resultVo.appendError(e.getMessage());
					integrationAuditVo.appendError(e.getMessage());
					integrationAuditVo.setStatus("failed");
				}
			}
			// }

			// 处理返回值
			try {
				InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "utf-8");
				StringWriter writer = new StringWriter();
				IOUtils.copy(reader, writer);
				int code = connection.getResponseCode();
				resultVo.setStatusCode(code);
				if (String.valueOf(code).startsWith("2")) {
					resultVo.appendResult(writer.toString());
				} else {
					resultVo.appendError(writer.toString());
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				resultVo.appendError(e.getMessage());
				integrationAuditVo.appendError(e.getMessage());
				integrationAuditVo.setStatus("failed");
			}

			if (outputConfig != null && StringUtils.isNotBlank(resultVo.getRawResult())) {
				String content = outputConfig.getString("content");
				if (StringUtils.isNotBlank(content)) {
					try {
						if (resultVo.getRawResult().startsWith("{")) {
							resultVo.setTransformedResult(JavascriptUtil.transform(JSONObject.parseObject(resultVo.getRawResult()), content));
						} else if (resultVo.getRawResult().startsWith("[")) {
							resultVo.setTransformedResult(JavascriptUtil.transform(JSONArray.parseArray(resultVo.getRawResult()), content));
						}
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
						resultVo.appendError(ex.getMessage());
						integrationAuditVo.appendError(ex.getMessage());
						integrationAuditVo.setStatus("failed");
					}
				}
			}

			integrationAuditVo.setResult(resultVo.getRawResult());
		}
		if (StringUtils.isBlank(integrationAuditVo.getStatus())) {
			integrationAuditVo.setStatus("succeed");
		}
		integrationAuditVo.setEndTime(new Date());
		CodeDriverThread thread = new IntegrationAuditSaveThread(integrationAuditVo);
		thread.setThreadName("INTEGRATION-AUDIT-SAVER-" + integrationVo.getUuid());
		CommonThreadPool.execute(thread);

		// connection.disconnect(); //Indicates that other requests to the
		// server are unlikely in the near future. Calling disconnect() should
		// not imply that this HttpURLConnection
		// instance can be reused for other requests.
		return resultVo;
	}

}
