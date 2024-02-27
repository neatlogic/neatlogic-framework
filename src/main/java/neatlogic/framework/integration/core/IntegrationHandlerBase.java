/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.integration.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.exception.type.ParamNotExistsException;
import neatlogic.framework.exception.type.ParamTypeNotFoundException;
import neatlogic.framework.file.core.AuditType;
import neatlogic.framework.file.core.Event;
import neatlogic.framework.file.core.appender.AppenderManager;
import neatlogic.framework.integration.authentication.core.AuthenticateHandlerFactory;
import neatlogic.framework.integration.authentication.core.IAuthenticateHandler;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
import neatlogic.framework.param.validate.core.ParamValidatorBase;
import neatlogic.framework.param.validate.core.ParamValidatorFactory;
import neatlogic.framework.util.javascript.JavascriptUtil;
import neatlogic.module.framework.integration.audit.IntegrationAuditAppendPostProcessor;
import neatlogic.module.framework.integration.audit.IntegrationAuditAppendPreProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class IntegrationHandlerBase implements IIntegrationHandler {
    static Logger logger = LoggerFactory.getLogger(IntegrationHandlerBase.class);

    static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

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
		/*
		  校验请求参数开始
		 */
        if (paramObj != null && paramObj.getInteger("needValid") != null && paramObj.getInteger("needValid").equals(1)) {
            List<PatternVo> patternList;
            // 包含内置参数
            if (this.hasPattern().equals(1)) {
                patternList = this.getInputPattern();
            } else {// 自定义参数
                patternList = new ArrayList<>();
                JSONArray paramList = paramObj.getJSONArray("paramList");
                if (CollectionUtils.isNotEmpty(paramList)) {
                    for (int i = 0; i < paramList.size(); i++) {
                        JSONObject pObj = paramList.getJSONObject(i);
                        PatternVo patternVo = JSON.toJavaObject(pObj, PatternVo.class);
                        patternList.add(patternVo);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(patternList)) {
                for (PatternVo patternVo : patternList) {
                    if (patternVo.getIsRequired() != null && patternVo.getIsRequired().equals(1)) {
                        if (!requestParamObj.containsKey(patternVo.getName())) {
                            throw new ParamNotExistsException(patternVo.getName());
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
                            throw new ParamIrregularException(patternVo.getName());
                        }
                    }
                }
            }
        }
		/*
		  校验请求参数结束
		 */

		/*
		  创建审计记录
		 */
        IntegrationAuditVo integrationAuditVo = new IntegrationAuditVo();
        integrationAuditVo.setRequestFrom(iRequestFrom.toString());
        integrationAuditVo.setUserUuid(UserContext.get().getUserUuid());// 用户非必填，因作业不存在登录用户
        integrationAuditVo.setIntegrationUuid(integrationVo.getUuid());
        integrationAuditVo.setStartTime(new Date());
        if (MapUtils.isNotEmpty(requestParamObj)) {
            integrationAuditVo.setParam(requestParamObj.toJSONString());
        }

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
            String errorMsg = (e instanceof ApiRuntimeException) ? ((ApiRuntimeException) e).getMessage() : e.getMessage();
            logger.error(e.getMessage(), e);
            integrationAuditVo.appendError(errorMsg);
            resultVo.appendError(errorMsg);
            integrationAuditVo.setStatus("failed");
        }
        if (connection != null) {
            // 转换输入参数
            // if (integrationVo.getMethod().equals(HttpMethod.POST.toString())) {
            String inputParam = "{}";
            if (inputConfig != null) {
                inputParam = inputConfig.getString("content");
                // 内容不为空代表需要通过freemarker转换
                if (StringUtils.isNotBlank(inputParam)) {
                    try {
                        // content = FreemarkerUtil.transform(integrationVo.getParamObj(), content);
                        inputParam = JavascriptUtil.transform(integrationVo.getParamObj(), inputParam);
                        resultVo.setTransformedParam(inputParam);
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                        resultVo.appendError(ex.getMessage());
                        integrationAuditVo.appendError(ex.getMessage());
                        integrationAuditVo.setStatus("failed");
                    }
                } else {
                    inputParam = integrationVo.getParamObj().toJSONString();
                }
            } else {
                inputParam = integrationVo.getParamObj().toJSONString();
            }
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                out.write(inputParam.getBytes(StandardCharsets.UTF_8));
                out.flush();
                // out.writeBytes(content);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                resultVo.appendError(e.getMessage());
                integrationAuditVo.appendError(e.getMessage());
                integrationAuditVo.setStatus("failed");
            }
            // }

            // 处理返回值
            try {
                int code = connection.getResponseCode();
                resultVo.setStatusCode(code);
                /* 请求失败时，getInputStream方法会根据状态码抛出不同的异常，比如404时抛出FileNotFoundException
                  故只有请求成功时才能使用getInputStream，否则应该使用getErrorStream
                 */
                InputStreamReader reader;
                if (String.valueOf(code).startsWith("2")) {
                    reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
                } else {
                    reader = new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);
                }
                StringWriter writer = new StringWriter();
                IOUtils.copy(reader, writer);
                if (String.valueOf(code).startsWith("2") || String.valueOf(code).startsWith("52")) {
                    resultVo.appendResult(writer.toString());
                } else {
                    throw new RuntimeException(writer.toString());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                resultVo.appendError("failed\n" + e.getMessage());
                integrationAuditVo.appendError("failed\n" + e.getMessage());
                integrationAuditVo.setStatus("failed");
            }
            boolean hasTransferred = false;
            if (outputConfig != null && StringUtils.isNotBlank(resultVo.getRawResult())) {
                String content = outputConfig.getString("content");
                if (StringUtils.isNotBlank(content)) {
                    try {
                        if (resultVo.getRawResult().startsWith("{")) {
                            resultVo.setTransformedResult(JavascriptUtil.transform(JSONObject.parseObject(resultVo.getRawResult()), content));
                        } else if (resultVo.getRawResult().startsWith("[")) {
                            resultVo.setTransformedResult(JavascriptUtil.transform(JSONArray.parseArray(resultVo.getRawResult()), content));
                        }
                        hasTransferred = true;
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                        resultVo.appendError(ex.getMessage());
                        integrationAuditVo.appendError(ex.getMessage());
                        integrationAuditVo.setStatus("failed");
                    }
                }
            }
            if (!hasTransferred) {
                resultVo.setTransformedResult(resultVo.getRawResult());
            }

            integrationAuditVo.setResult(resultVo.getRawResult());
        }
        if (StringUtils.isBlank(integrationAuditVo.getStatus())) {
            integrationAuditVo.setStatus("succeed");
        }
        if (connection != null && MapUtils.isNotEmpty(connection.getHeaderFields())) {
            integrationAuditVo.setHeaders(JSONObject.parseObject(JSONObject.toJSONString(connection.getHeaderFields())));
        }
        integrationAuditVo.setEndTime(new Date());
        resultVo.setAuditId(integrationAuditVo.getId());
//        NeatLogicThread thread = new IntegrationAuditSaveThread(integrationAuditVo);
//        thread.setThreadName("INTEGRATION-AUDIT-SAVER-" + integrationVo.getUuid());
//        CachedThreadPool.execute(thread);

        JSONObject data = new JSONObject();
        data.put("integrationAudit", integrationAuditVo);
        String param = integrationAuditVo.getParam();
        if (StringUtils.isNotBlank(param)) {
            data.put("param", param);
        }
        Object result = integrationAuditVo.getResult();
        if (result != null) {
            String resultStr = (String) result;
            try {
                JSONObject resultObj = JSONObject.parseObject(resultStr);
                data.put("result", JSONObject.toJSONString(resultObj, SerializerFeature.PrettyFormat, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect));
            } catch (JSONException e) {
                data.put("result", result);
            }
        }
        String error = integrationAuditVo.getError();
        if (StringUtils.isNotBlank(error)) {
            data.put("error", error);
        }
        IntegrationAuditAppendPostProcessor appendPostProcessor = CrossoverServiceFactory.getApi(IntegrationAuditAppendPostProcessor.class);
        IntegrationAuditAppendPreProcessor appendPreProcessor = CrossoverServiceFactory.getApi(IntegrationAuditAppendPreProcessor.class);
        AppenderManager.execute(new Event(integrationVo.getName(), integrationAuditVo.getStartTime().getTime(), data, appendPreProcessor, appendPostProcessor, AuditType.INTEGRATION_AUDIT));

        // connection.disconnect(); //Indicates that other requests to the
        // server are unlikely in the near future. Calling disconnect() should
        // not imply that this HttpURLConnection
        // instance can be reused for other requests.
        return resultVo;
    }
}
