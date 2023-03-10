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

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import neatlogic.framework.exception.httprequest.HttpMethodIrregularException;
import neatlogic.framework.file.core.FileStorageMediumFactory;
import neatlogic.framework.file.core.IFileStorageHandler;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.integration.authentication.core.AuthenticateHandlerFactory;
import neatlogic.framework.integration.authentication.core.IAuthenticateHandler;
import neatlogic.framework.integration.authentication.enums.AuthenticateType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

public class HttpRequestUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

    public enum ContentType {
        CONTENT_TYPE_APPLICATION_JSON("application/json"), CONTENT_TYPE_MULTIPART_FORM_DATA("multipart/form-data; boundary=" + FORM_DATA_BOUNDARY), CONTENT_TYPE_TEXT_HTML("text/html"), CONTENT_TYPE_APPLICATION_FORM("application/x-www-form-urlencoded"), CONTENT_TYPE_MULTIPART_FORM_DATA_FILE_STREAM("multipart/form-data; boundary=" + FORM_DATA_BOUNDARY);
        private final String value;

        ContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    //???????????????contentType?????????????????????
    private static final Map<ContentType, IOutputStreamHandler<DataOutputStream, HttpRequestUtil>> OutputStreamHandlerMap = new HashMap<>();
    //??????boundary
    private static final String FORM_DATA_BOUNDARY = "----MyFormBoundarySMFEtUYQG6r5B920";
    //????????????
    private final String url;
    private String method;
    private ContentType contentType = ContentType.CONTENT_TYPE_APPLICATION_JSON;
    private Charset charset = StandardCharsets.UTF_8;
    //??????????????????????????????????????????
    private String tenant;
    //???????????????
    private final Map<String, String> headerMap = new HashMap<>();
    //????????????,????????????
    private int connectTimeout = 30000;
    //?????????
    private int readTimeout = 0;
    //payload??????
    private String payload;
    //????????????
    private JSONObject formData;
    private OutputStream outputStream;
    //????????????
    private List<FileVo> fileList;
    //?????????map(????????? -> InputStream)
    private Map<String, InputStream> fileStreamMap;
    //????????????
    private AuthenticateType authType;
    //?????????
    private String username;
    //??????
    private String password;
    //??????
    private String token;

    @FunctionalInterface
    public interface IOutputStreamHandler<T, K> {
        void execute(T t, K k) throws Exception;
    }


    static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    private static class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            //FIXME ???????????????????????????
            System.out.println("verify " + hostname);
            return true;
        }
    }

    static {
        OutputStreamHandlerMap.put(ContentType.CONTENT_TYPE_APPLICATION_JSON, (out, _this) -> {
            if (StringUtils.isNotBlank(_this.payload)) {
                out.write(_this.payload.getBytes(_this.charset));
            }
        });

        OutputStreamHandlerMap.put(ContentType.CONTENT_TYPE_APPLICATION_FORM, (out, _this) -> {
            if (MapUtils.isNotEmpty(_this.formData)) {
                String str = "";
                for (String key : _this.formData.keySet()) {
                    if (StringUtils.isNotBlank(str)) {
                        str += "&";
                    }
                    str += (key + "=" + URLEncoder.encode(_this.formData.getString(key), _this.charset.name()));
                }
                out.write(str.getBytes(_this.charset));
            }
        });

        OutputStreamHandlerMap.put(ContentType.CONTENT_TYPE_TEXT_HTML, (out, _this) -> {
        });

        OutputStreamHandlerMap.put(ContentType.CONTENT_TYPE_MULTIPART_FORM_DATA, (out, _this) -> {
            StringBuilder dataBuilder = new StringBuilder("\r\n");
            String endBoundary = "\r\n--" + FORM_DATA_BOUNDARY + "--\r\n";
            // strParams 1:key 2:value
            if (MapUtils.isNotEmpty(_this.formData)) {
                Set<String> keySet = _this.formData.keySet();
                for (String key : keySet) {
                    String value = _this.formData.getString(key);
                    dataBuilder.append("Content-Disposition: form-data; name=").append(key).append("\r\n").append("\r\n").append(value).append("\r\n").append("--").append(FORM_DATA_BOUNDARY).append("\r\n");
                }
            }
            String boundaryMessage = dataBuilder.toString();

            out.write(("--" + FORM_DATA_BOUNDARY + boundaryMessage).getBytes(_this.charset));

            dataBuilder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(_this.fileList)) {
                for (int i = 0, num = _this.fileList.size(); i < num; i++) {
                    FileVo fileVo = _this.fileList.get(i);
                    String prefix = fileVo.getPath().split(":")[0];
                    IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix.toUpperCase());
                    if (handler == null) {
                        throw new FileStorageMediumHandlerNotFoundException(prefix);
                    }
                    String fileName = fileVo.getName();
                    dataBuilder.append("Content-Disposition: form-data; name=").append(fileName).append("; filename=").append(fileName).append("\r\n").append("Content-Type:multipart/form-data ").append("\r\n\r\n");

                    out.write(dataBuilder.toString().getBytes(_this.charset));
                    // ???????????????
                    DataInputStream in = new DataInputStream(handler.getData(fileVo.getPath()));
                    int bytes;
                    byte[] bufferOut = new byte[1024 * 5];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    if (i < num - 1) {
                        out.write(endBoundary.getBytes(_this.charset));
                    }
                    in.close();
                }
            }
            out.write(endBoundary.getBytes(StandardCharsets.UTF_8));
        });

        OutputStreamHandlerMap.put(ContentType.CONTENT_TYPE_MULTIPART_FORM_DATA_FILE_STREAM, (out, _this) -> {
            StringBuilder dataBuilder = new StringBuilder("\r\n");
            String endBoundary = "\r\n--" + FORM_DATA_BOUNDARY + "--\r\n";
            // strParams 1:key 2:value
            if (MapUtils.isNotEmpty(_this.formData)) {
                Set<String> keySet = _this.formData.keySet();
                for (String key : keySet) {
                    String value = _this.formData.getString(key);
                    dataBuilder.append("Content-Disposition: form-data; name=").append(key).append("\r\n").append("\r\n").append(value).append("\r\n").append("--").append(FORM_DATA_BOUNDARY).append("\r\n");
                }
            }
            String boundaryMessage = dataBuilder.toString();

            out.write(("--" + FORM_DATA_BOUNDARY + boundaryMessage).getBytes(_this.charset));

            dataBuilder = new StringBuilder();
            if (MapUtils.isNotEmpty(_this.fileStreamMap)) {
                Set<Map.Entry<String, InputStream>> entrySet = _this.fileStreamMap.entrySet();
                Iterator<Map.Entry<String, InputStream>> iterator = entrySet.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Map.Entry<String, InputStream> next = iterator.next();
                    String fileName = next.getKey();
                    InputStream inputStream = next.getValue();
                    dataBuilder.append("Content-Disposition: form-data; name=").append(fileName).append("; filename=").append(fileName).append("\r\n").append("Content-Type:multipart/form-data ").append("\r\n\r\n");

                    out.write(dataBuilder.toString().getBytes(_this.charset));
                    // ???????????????
                    DataInputStream in = new DataInputStream(inputStream);
                    int bytes;
                    byte[] bufferOut = new byte[1024 * 5];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    if (i < entrySet.size() - 1) {
                        out.write(endBoundary.getBytes(_this.charset));
                    }
                    in.close();
                }
            }
            out.write(endBoundary.getBytes(StandardCharsets.UTF_8));
        });
    }

    //??????????????????
    private HttpRequestUtil(String url) {
        this.url = url;
    }

    public static HttpRequestUtil post(String url) {
        HttpRequestUtil httpRequestUtil = new HttpRequestUtil(url);
        httpRequestUtil.method = "POST";
        return httpRequestUtil;
    }

    public static HttpRequestUtil get(String url) {
        HttpRequestUtil httpRequestUtil = new HttpRequestUtil(url);
        httpRequestUtil.method = "GET";
        return httpRequestUtil;
    }

    public static void resetResponse(HttpServletResponse response) {
        if (response != null) {
            int responseStatus = response.getStatus();
            Map<String, String> headerMap = new HashMap<>();
            Collection<String> headerNames = response.getHeaderNames();
            for (String headerName : headerNames) {
                headerMap.put(headerName, response.getHeader(headerName));
            }
            response.reset();//?????? "getOutputStream???getWriter?????????????????? '?????????????????????????????????getOutputStream()' ??????" ??????
            response.setStatus(responseStatus);
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                response.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * ??????
     *
     * @param url          ??????
     * @param outputStream ?????????
     */
    public static HttpRequestUtil download(String url, String method, OutputStream outputStream) {
        if (!Objects.equals(method, "GET") && !Objects.equals(method, "POST")) {
            throw new HttpMethodIrregularException();
        }
        HttpRequestUtil httpRequestUtil = new HttpRequestUtil(url);
        httpRequestUtil.method = method;
        httpRequestUtil.outputStream = outputStream;
        return httpRequestUtil;
    }

    /**
     * ??????content-type????????????applications/json
     */
    public HttpRequestUtil setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * ????????????????????????utf-8
     *
     * @param charset ?????????
     */
    public HttpRequestUtil setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param tenant ??????uuid
     */
    public HttpRequestUtil setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    /**
     * ????????????????????????????????????
     *
     * @param connectTimeout ?????????
     */
    public HttpRequestUtil setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * ?????????????????????????????????
     *
     * @param readTimeout ?????????
     */
    public HttpRequestUtil setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * ?????????????????????
     *
     * @param key   ??????key
     * @param value ?????????
     */
    public HttpRequestUtil addHeader(String key, String value) {
        if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(key)) {
            this.headerMap.put(key, value);
        }
        return this;
    }

    /**
     * ??????payload??????
     *
     * @param payload payload??????
     */
    public HttpRequestUtil setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    /**
     * ??????????????????
     *
     * @param formData ????????????
     */
    public HttpRequestUtil setFormData(JSONObject formData) {
        this.formData = formData;
        return this;
    }

    /**
     * ?????????????????????????????????
     *
     * @param fileList ????????????
     */
    public HttpRequestUtil setFileList(List<FileVo> fileList) {
        this.fileList = fileList;
        return this;
    }

    /**
     * ???????????????????????????
     *
     * @param fileStreamMap ?????????map
     * @return
     */
    public HttpRequestUtil setFileStreamMap(Map<String, InputStream> fileStreamMap) {
        this.fileStreamMap = fileStreamMap;
        return this;
    }

    /**
     * ???????????????
     *
     * @param username ???????????????
     */
    public HttpRequestUtil setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * ??????????????????
     *
     * @param password ??????
     */
    public HttpRequestUtil setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * ????????????token
     *
     * @param token token
     */
    public HttpRequestUtil setToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * ??????????????????
     *
     * @param authType ????????????
     */
    public HttpRequestUtil setAuthType(AuthenticateType authType) {
        this.authType = authType;
        return this;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param outputStream ?????????
     */
    public HttpRequestUtil setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    private JSONObject getAuthConfig() {
        JSONObject authConfig = new JSONObject();
        if (StringUtils.isNotBlank(this.username)) {
            authConfig.put("username", this.username);
        }
        if (StringUtils.isNotBlank(this.password)) {
            authConfig.put("password", this.password);
        }
        if (StringUtils.isNotBlank(this.token)) {
            authConfig.put("token", this.token);
        }
        return authConfig;
    }

    private HttpURLConnection getConnection() {
        try {
            HttpURLConnection connection;
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URL getUrl = new URL(this.url);
            connection = (HttpURLConnection) getUrl.openConnection();
            //??????????????????
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            if (Objects.equals(this.method, "POST")) {
                connection.setDoOutput(true);
            }
            connection.setDoInput(true);
            connection.setConnectTimeout(this.connectTimeout);
            connection.setReadTimeout(this.readTimeout);
            // ??????????????????
            connection.setRequestProperty("Content-Type", this.contentType.getValue());
            connection.setRequestProperty("Charset", String.valueOf(this.charset));
            if (StringUtils.isNotBlank(this.tenant)) {
                connection.setRequestProperty("Tenant", this.tenant);
            }
            //?????????????????????
            if (MapUtils.isNotEmpty(this.headerMap)) {
                for (String key : this.headerMap.keySet()) {
                    connection.setRequestProperty(key, this.headerMap.get(key));
                }
            }
            // ????????????
            if (this.authType != null) {
                IAuthenticateHandler handler = AuthenticateHandlerFactory.getHandler(this.authType.getValue());
                if (handler != null) {
                    handler.authenticate(connection, this.getAuthConfig());
                }
            }
            connection.connect();
            return connection;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            this.error = ExceptionUtils.getStackTrace(ex);
            this.errorMsg = ex.getMessage();
        }
        return null;
    }

    private String result;
    private String error;
    private String errorMsg; // ?????????????????????
    private int responseCode;
    //??????????????????response???header ????????????????????????response???
    private List<String> responseHeaderList;


    public HttpRequestUtil sendRequest() {
        HttpURLConnection connection = getConnection();
        if (connection != null) {
            try {
                if (Objects.equals(this.method, "POST")) {
                    try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                        OutputStreamHandlerMap.get(this.contentType).execute(out, this);
                        out.flush();
                    }
                }
                //?????????????????????Content-Disposition????????????????????????
                String contentDisPosition = connection.getHeaderField("Content-Disposition");
                if (StringUtils.isNotBlank(contentDisPosition)) {
                    UserContext.get().getResponse().setHeader("Content-Disposition", contentDisPosition);
                }
                if (CollectionUtils.isNotEmpty(responseHeaderList)) {
                    Map<String, List<String>> headersMap = connection.getHeaderFields();
                    for (String header : responseHeaderList) {
                        List<String> buildStatusList = headersMap.get(header);
                        if (CollectionUtils.isNotEmpty(buildStatusList)) {
                            UserContext.get().getResponse().setHeader(header, buildStatusList.get(0));
                        }
                    }
                }
                // ???????????????
                this.responseCode = connection.getResponseCode();
                if (100 <= this.responseCode && this.responseCode <= 399) {
                    DataInputStream input = new DataInputStream(connection.getInputStream());
                    if (this.outputStream == null) {
                        StringWriter writer = new StringWriter();
                        InputStreamReader reader = new InputStreamReader(input, this.charset);
                        IOUtils.copy(reader, writer);
                        result = writer.toString();
                    } else {
                        IOUtils.copy(input, this.outputStream);
                        this.outputStream.flush();
                    }
                } else {
                    DataInputStream input = new DataInputStream(connection.getErrorStream());
                    StringWriter writer = new StringWriter();
                    InputStreamReader reader = new InputStreamReader(input, this.charset);
                    IOUtils.copy(reader, writer);
                    throw new ApiRuntimeException(writer.toString());
                }
            } catch (ApiRuntimeException e) {
                this.error = e.getMessage();
                // ?????????????????????????????????????????????
                String message = e.getMessage();
                try {
                    JSONObject object = JSONObject.parseObject(message);
                    message = object.getString("Message");
                } catch (Exception ignored) {
                }
                this.errorMsg = message;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                this.error = ExceptionUtils.getStackTrace(e);
                this.errorMsg = e.getMessage();
            } finally {
                connection.disconnect();
                if (UserContext.get() != null && UserContext.get().getResponse() != null && !UserContext.get().getResponse().isCommitted()) {
                    resetResponse(UserContext.get().getResponse());
                }
            }
        }
        return this;
    }


    public String getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public HttpRequestUtil setResponseHeaders(List<String> responseHeaderList) {
        this.responseHeaderList = responseHeaderList;
        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }


    public JSONObject getResultJson() {
        if (StringUtils.isNotBlank(result)) {
            return JSONObject.parseObject(result);
        } else {
            return null;
        }
    }

    public JSONArray getResultJsonArray() {
        if (StringUtils.isNotBlank(result)) {
            return JSONObject.parseArray(result);
        } else {
            return null;
        }
    }
}
