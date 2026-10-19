/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.integration.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.exception.type.ParamNotExistsException;
import neatlogic.framework.exception.type.ParamTransferException;
import neatlogic.framework.exception.type.ParamTypeNotFoundException;
import neatlogic.framework.file.core.AuditType;
import neatlogic.framework.file.core.Event;
import neatlogic.framework.file.core.appender.AppenderManager;
import neatlogic.framework.fulltextindex.core.FullTextIndexHandlerFactory;
import neatlogic.framework.fulltextindex.core.IFullTextIndexHandler;
import neatlogic.framework.fulltextindex.enums.FrameworkFullTextIndexType;
import neatlogic.framework.integration.authentication.core.AuthenticateHandlerFactory;
import neatlogic.framework.integration.authentication.core.IAuthenticateHandler;
import neatlogic.framework.integration.crossover.IntegrationCrossoverService;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
import neatlogic.framework.param.validate.core.ParamValidatorBase;
import neatlogic.framework.param.validate.core.ApiParamValidatorBase;
import neatlogic.framework.param.validate.core.ParamValidatorFactory;
import neatlogic.framework.util.javascript.JavascriptUtil;
import neatlogic.module.framework.integration.audit.IntegrationAuditAppendPostProcessor;
import neatlogic.module.framework.integration.audit.IntegrationAuditAppendPreProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

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

    /** 输入准备顺序固定为补齐默认值、原有校验，随后才允许转换和发送。 */
    protected void prepareInputParam(IntegrationVo integrationVo) {
        integrationVo.setParamObj(IntegrationDefaultValueUtil.apply(this, integrationVo.getConfig(), "input", integrationVo.getParamObj()));
        validateInputParam(integrationVo);
    }

    /** 启用校验后同时检查固定和自定义输入，保持原来的必填字段存在性判断。 */
    protected void validateInputParam(IntegrationVo integrationVo) {
        JSONObject config = integrationVo.getConfig();
        JSONObject param = IntegrationPatternUtil.getParamConfig(config);
        if (param == null || !Integer.valueOf(1).equals(param.getInteger("needValid"))) {
            return;
        }
        JSONObject request = integrationVo.getParamObj();
        if (request == null) {
            request = new JSONObject();
        }
        for (PatternVo pattern : IntegrationPatternUtil.merge(this, config, "input")) {
            if (Integer.valueOf(1).equals(pattern.getIsRequired()) && !request.containsKey(pattern.getName())) {
                throw new ParamNotExistsException(pattern.getName());
            }
            validateParamType(pattern, request.get(pattern.getName()));
        }
    }

    /** 优先使用已有自定义类型校验器；无旧校验器时复用 API 类型校验，兼容固定规范类型。 */
    private void validateParamType(PatternVo pattern, Object value) {
        ParamType paramType = ParamType.getParamType(pattern.getType());
        if (paramType != null) {
            ParamValidatorBase validator = ParamValidatorFactory.getAuthInstance(paramType);
            if (validator != null) {
                if (value != null && !validator.validate(value, null)) {
                    throw new ParamIrregularException(pattern.getName());
                }
                return;
            }
        }
        ApiParamType apiType;
        if (paramType == ParamType.NUMBER) {
            apiType = ApiParamType.DOUBLE;
        } else if (paramType == ParamType.ARRAY) {
            apiType = ApiParamType.JSONARRAY;
        } else if (paramType == ParamType.ENUM || paramType == ParamType.DATE) {
            // 旧配置未提供枚举规则或日期格式，继续保留没有专用校验器时的行为。
            return;
        } else {
            apiType = ApiParamType.getApiParamType(pattern.getType());
            // API 类型查询会将未知值回退为 NOAUTH，未知配置仍须明确报错。
            if (apiType == ApiParamType.NOAUTH && !apiType.getValue().equals(pattern.getType())) {
                throw new ParamTypeNotFoundException(pattern.getType());
            }
        }
        ApiParamValidatorBase validator = ParamValidatorFactory.getAuthInstance(apiType);
        if (value != null && validator != null && !validator.validate(value, null)) {
            throw new ParamIrregularException(pattern.getName());
        }
    }

    // 构建带参数的 URL
    private String buildUrlWithParams(String baseURL, String inputParam) {
        StringJoiner sj = new StringJoiner("&");
        try {
            JSONObject inputObj = JSON.parseObject(inputParam);
            if (MapUtils.isNotEmpty(inputObj)) {
                for (Map.Entry<String, Object> entry : inputObj.entrySet()) {
                    //if (entry.getValue() instanceof String || entry.getValue() instanceof Number) {
                    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                    //}
                }
            }
        } catch (Exception ignored) {

        }
        if (StringUtils.isNotBlank(sj.toString())) {
            if (baseURL.contains("?")) {
                return baseURL + "&" + sj;
            } else {
                return baseURL + "?" + sj;
            }
        } else {
            return baseURL;
        }
    }

    public IntegrationResultVo sendRequest(IntegrationVo integrationVo, IRequestFrom iRequestFrom) {

        String url = integrationVo.getUrl();
        JSONObject config = integrationVo.getConfig();
        // 未配置可选项时按空配置处理，运行校验是否开启仍由 needValid 决定。
        if (config == null) {
            config = new JSONObject();
        }
        JSONObject otherConfig = config.getJSONObject("other");
        JSONObject inputConfig = config.getJSONObject("input");
        JSONObject authConfig = config.getJSONObject("authentication");
        JSONArray headConfig = config.getJSONArray("head");
        JSONObject outputConfig = config.getJSONObject("output");
        // 默认值独立于 needValid；先复制并补齐输入，转换和审计读取同一份请求参数。
        prepareInputParam(integrationVo);
        JSONObject requestParamObj = integrationVo.getParamObj();
        if (otherConfig == null) {
            otherConfig = new JSONObject();
        }
        /*
        转换入参开始
         */
        IntegrationAuditVo integrationAuditVo = new IntegrationAuditVo();
        IntegrationResultVo resultVo = new IntegrationResultVo();
        String inputParam = "{}";
        if (inputConfig != null) {
            inputParam = inputConfig.getString("content");
            if (StringUtils.isNotBlank(inputParam)) {
                try {
                    inputParam = JavascriptUtil.transform(integrationVo.getParamObj(), inputParam);
                    resultVo.setTransformedParam(inputParam);
                } catch (Exception ex) {
                    throw new ParamTransferException(ex.getMessage());
                }
            } else {
                inputParam = integrationVo.getParamObj().toJSONString();
            }
        } else {
            inputParam = integrationVo.getParamObj().toJSONString();
        }


        List<String> fullIndexParamValueList = new ArrayList<>();
        integrationAuditVo.setRequestFrom(iRequestFrom.toString());
        integrationAuditVo.setUserUuid(UserContext.get().getUserUuid());// 用户非必填，因作业不存在登录用户
        integrationAuditVo.setIntegrationUuid(integrationVo.getUuid());
        integrationAuditVo.setStartTime(new Date());
        if (MapUtils.isNotEmpty(requestParamObj)) {
            IntegrationCrossoverService integrationCrossoverService = CrossoverServiceFactory.getApi(IntegrationCrossoverService.class);
            List<String> searchAbleInputParamNameList = integrationCrossoverService.getSearchAbleInputParamNameList(integrationVo);
            if (CollectionUtils.isNotEmpty(searchAbleInputParamNameList)) {
                for (String paramName : searchAbleInputParamNameList) {
                    Object paramValue = requestParamObj.get(paramName);
                    if (paramValue != null) {
                        fullIndexParamValueList.add(paramValue.toString());
                    }
                }
            }
            integrationAuditVo.setParam(requestParamObj.toJSONString());
        }

        IntegrationRateLimitManager.acquire(integrationVo);

        HttpURLConnection connection = null;
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


            if (StringUtils.isNotBlank(integrationVo.getMethod()) && integrationVo.getMethod().equalsIgnoreCase("get")) {
                url = buildUrlWithParams(url, inputParam);
            }
            URL getUrl = new URL(url);
            connection = (HttpURLConnection) getUrl.openConnection();
            // 设置http method
            connection.setRequestMethod(integrationVo.getMethod().toUpperCase());
            connection.setUseCaches(false);
            if (StringUtils.isNotBlank(integrationVo.getMethod()) && integrationVo.getMethod().equalsIgnoreCase("post")) {
                connection.setDoOutput(true);
            }


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
            if (MapUtils.isNotEmpty(otherConfig)) {
                if (otherConfig.containsKey("connectTimeout")) {
                    connection.setConnectTimeout(otherConfig.getIntValue("connectTimeout"));
                }
                if (otherConfig.containsKey("readTimeout")) {
                    connection.setReadTimeout(otherConfig.getIntValue("readTimeout"));
                }
            }

            // 设置head
            boolean hasContentType = false;
            if (headConfig != null) {
                for (int i = 0; i < headConfig.size(); i++) {
                    JSONObject item = headConfig.getJSONObject(i);
                    String key = item.getString("key");
                    String value = item.getString("value");
                    connection.setRequestProperty(key, value);
                    if (key.equalsIgnoreCase("Content-Type")) {
                        hasContentType = true;
                    }
                }
            }
            // 设置默认header
            if (!hasContentType) {
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            }
            connection.connect();
        } catch (Exception e) {
            String errorMsg = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
            logger.warn(url + ", " + e.getMessage(), e);
            integrationAuditVo.appendError(errorMsg);
            resultVo.appendError(errorMsg);
            integrationAuditVo.setStatus("failed");
        }
        if (connection != null) {

            if (connection.getDoOutput()) {
                try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                    if (!otherConfig.containsKey("charset")) {
                        out.write(inputParam.getBytes(StandardCharsets.UTF_8));
                    } else {
                        out.write(inputParam.getBytes(otherConfig.getString("charset")));
                    }
                    out.flush();
                } catch (Exception e) {
                    logger.warn(url + ", " + e.getMessage(), e);
                    resultVo.appendError(e.getMessage());
                    integrationAuditVo.appendError(e.getMessage());
                    integrationAuditVo.setStatus("failed");
                }
            }

            // 处理返回值
            try {
                int code = connection.getResponseCode();
                resultVo.setStatusCode(code);
                /* 请求失败时，getInputStream方法会根据状态码抛出不同的异常，比如404时抛出FileNotFoundException
                  故只有请求成功时才能使用getInputStream，否则应该使用getErrorStream
                 */
                InputStreamReader reader;
                if (String.valueOf(code).startsWith("2")) {
                    if (!otherConfig.containsKey("chartset")) {
                        reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
                    } else {
                        reader = new InputStreamReader(connection.getInputStream(), otherConfig.getString("chartset"));
                    }
                } else {
                    if (!otherConfig.containsKey("chartset")) {
                        reader = new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);
                    } else {
                        reader = new InputStreamReader(connection.getErrorStream(), otherConfig.getString("chartset"));
                    }
                }
                StringWriter writer = new StringWriter();
                IOUtils.copy(reader, writer);
                if (String.valueOf(code).startsWith("2") || String.valueOf(code).startsWith("52")) {
                    resultVo.appendResult(writer.toString());
                } else {
                    throw new RuntimeException(writer.toString());
                }
            } catch (Exception e) {
                logger.warn(url + ", " + e.getMessage(), e);
                resultVo.appendError(e.getMessage());
                integrationAuditVo.appendError(e.getMessage());
                integrationAuditVo.setStatus("failed");
            }
            boolean hasTransferred = false;
            if (outputConfig != null && StringUtils.isNotBlank(resultVo.getRawResult())) {
                String content = outputConfig.getString("content");
                if (StringUtils.isNotBlank(content)) {
                    try {
                        if (resultVo.getRawResult().startsWith("{")) {
                            resultVo.setTransformedResult(JavascriptUtil.transform(JSON.parseObject(resultVo.getRawResult()), content));
                        } else if (resultVo.getRawResult().startsWith("[")) {
                            resultVo.setTransformedResult(JavascriptUtil.transform(JSON.parseArray(resultVo.getRawResult()), content));
                        }
                        hasTransferred = true;
                    } catch (Exception ex) {
                        logger.warn(url + ", " + ex.getMessage(), ex);
                        resultVo.appendError(ex.getMessage());
                        integrationAuditVo.appendError(ex.getMessage());
                        integrationAuditVo.setStatus("failed");
                    }
                }
            }
            if (!hasTransferred) {
                resultVo.setTransformedResult(resultVo.getRawResult());
            }

            // 只处理成功的对象响应；输出默认值不能掩盖网络错误或转换失败。
            if (!"failed".equals(integrationAuditVo.getStatus())) {
                try {
                    IntegrationDefaultValueUtil.applyOutput(this, config, resultVo);
                } catch (Exception ex) {
                    resultVo.appendError(ex.getMessage());
                    integrationAuditVo.appendError(ex.getMessage());
                    integrationAuditVo.setStatus("failed");
                }
            }

            integrationAuditVo.setResult(resultVo.getRawResult());
        }
        if (StringUtils.isBlank(integrationAuditVo.getStatus())) {
            integrationAuditVo.setStatus("succeed");
        }
        if (connection != null && MapUtils.isNotEmpty(connection.getHeaderFields())) {
            integrationAuditVo.setHeaders(JSON.parseObject(JSON.toJSONString(connection.getHeaderFields())));
        }
        integrationAuditVo.setEndTime(new Date());
        resultVo.setAuditId(integrationAuditVo.getId());
//        NeatLogicThread thread = new IntegrationAuditSaveThread(integrationAuditVo);
//        thread.setThreadName("INTEGRATION-AUDIT-SAVER-" + integrationVo.getUuid());
//        CachedThreadPool.execute(thread);
        Long auditId = integrationAuditVo.getId();
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
                JSONObject resultObj = JSON.parseObject(resultStr);
                data.put("result", JSON.toJSONString(resultObj, SerializerFeature.PrettyFormat, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect));
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

        //创建全文检索索引
        if (CollectionUtils.isNotEmpty(fullIndexParamValueList)) {
            IFullTextIndexHandler indexHandler = FullTextIndexHandlerFactory.getHandler(FrameworkFullTextIndexType.INTEGRATION_AUDIT);
            if (indexHandler != null) {
                JSONObject dataObj = new JSONObject();
                dataObj.put("param", String.join(",", fullIndexParamValueList));
                indexHandler.createIndex(auditId, dataObj);
            }
        }
        return resultVo;
    }
}
