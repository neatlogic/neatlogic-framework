/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        CONTENT_TYPE_APPLICATION_JSON("application/json"),
        CONTENT_TYPE_MULTIPART_FORM_DATA("multipart/form-data; boundary=" + FORM_DATA_BOUNDARY),
        CONTENT_TYPE_TEXT_HTML("text/html"),
        CONTENT_TYPE_APPLICATION_FORM("application/x-www-form-urlencoded"),
        CONTENT_TYPE_MULTIPART_FORM_DATA_FILE_STREAM("multipart/form-data; boundary=" + FORM_DATA_BOUNDARY);
        private final String value;

        ContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    //预定义各种contentType的写入数据方式
    private static final Map<ContentType, IOutputStreamHandler<DataOutputStream, HttpRequestUtil>> OutputStreamHandlerMap = new HashMap<>();
    //默认boundary
    private static final String FORM_DATA_BOUNDARY = "----MyFormBoundarySMFEtUYQG6r5B920";
    //连接地址
    private String url;
    private String method;
    private ContentType contentType = ContentType.CONTENT_TYPE_APPLICATION_JSON;
    private Charset charset = StandardCharsets.UTF_8;
    //租户信息，调用内部接口时使用
    private String tenant;
    //自定义头部
    private final Map<String, String> headerMap = new HashMap<>();
    //连接超时,单位毫秒
    private int connectTimeout = 30000;
    //读超时
    private int readTimeout = 0;
    //payload数据
    private String payload;
    //表单数据
    private JSONObject formData;
    private OutputStream outputStream;
    //附件列表
    private List<FileVo> fileList;
    //文件流map(文件名 -> InputStream)
    private Map<String, InputStream> fileStreamMap;
    //认证方式
    private AuthenticateType authType;
    //用户名
    private String username;
    //密码
    private String password;
    //令牌
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
            //FIXME 后续要补充验证逻辑
            System.out.println("verify " + hostname);
            return true;
        }
    }

    /**
     * 拼接url queryString
     *
     * @param params url 入参
     */
    private void setUrlWithQueryString(JSONObject params) {
        StringBuilder builder = new StringBuilder(this.url);
        if (this.url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.getString(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        this.url = builder.toString();
    }

    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
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
                    // 开始写文件
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
                    // 开始写文件
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

    //保护构造函数
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
            response.reset();//解决 "getOutputStream和getWriter一起用，导致 '当前响应已经调用了方法getOutputStream()' 异常" 问题
            response.setStatus(responseStatus);
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                response.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 下载
     *
     * @param url          地址
     * @param outputStream 输出流
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
     * 设置content-type，默认是applications/json
     */
    public HttpRequestUtil setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * 设置字符集，默认utf-8
     *
     * @param charset 字符集
     */
    public HttpRequestUtil setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 设置当前租户，调用内部接口时需要
     *
     * @param tenant 租户uuid
     */
    public HttpRequestUtil setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    /**
     * 设置连接超时，单位：毫秒
     *
     * @param connectTimeout 毫秒数
     */
    public HttpRequestUtil setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpRequestUtil setQueryString(JSONObject queryString) {
        if (MapUtils.isNotEmpty(queryString)) {
            setUrlWithQueryString(queryString);
        }
        return this;
    }

    /**
     * 设置读超时，单位：毫秒
     *
     * @param readTimeout 毫秒数
     */
    public HttpRequestUtil setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * 增加自定义头部
     *
     * @param key   头部key
     * @param value 头部值
     */
    public HttpRequestUtil addHeader(String key, String value) {
        if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(key)) {
            this.headerMap.put(key, value);
        }
        return this;
    }

    /**
     * 设置payload数据
     *
     * @param payload payload数据
     */
    public HttpRequestUtil setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    /**
     * 设置表单数据
     *
     * @param formData 表单数据
     */
    public HttpRequestUtil setFormData(JSONObject formData) {
        this.formData = formData;
        return this;
    }

    /**
     * 设置需要上传的附件列表
     *
     * @param fileList 附件列表
     */
    public HttpRequestUtil setFileList(List<FileVo> fileList) {
        this.fileList = fileList;
        return this;
    }

    /**
     * 设置需要上传的文件
     *
     * @param fileStreamMap 文件流map
     * @return
     */
    public HttpRequestUtil setFileStreamMap(Map<String, InputStream> fileStreamMap) {
        this.fileStreamMap = fileStreamMap;
        return this;
    }

    /**
     * 设置用户名
     *
     * @param username 登陆用户名
     */
    public HttpRequestUtil setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 设置登陆密码
     *
     * @param password 密码
     */
    public HttpRequestUtil setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * 设置认证token
     *
     * @param token token
     */
    public HttpRequestUtil setToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * 设置认证方式
     *
     * @param authType 认证方式
     */
    public HttpRequestUtil setAuthType(AuthenticateType authType) {
        this.authType = authType;
        return this;
    }

    /**
     * 如果需要处理输出流，需要设置流
     *
     * @param outputStream 输出流
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
            //设置连接参数
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            if (Objects.equals(this.method, "POST")) {
                connection.setDoOutput(true);
            }
            connection.setDoInput(true);
            connection.setConnectTimeout(this.connectTimeout);
            connection.setReadTimeout(this.readTimeout);
            // 设置默认头部
            connection.setRequestProperty("Content-Type", this.contentType.getValue());
            connection.setRequestProperty("Charset", String.valueOf(this.charset));
            if (StringUtils.isNotBlank(this.tenant)) {
                connection.setRequestProperty("Tenant", this.tenant);
            }
            //设置自定义头部
            if (MapUtils.isNotEmpty(this.headerMap)) {
                for (String key : this.headerMap.keySet()) {
                    connection.setRequestProperty(key, this.headerMap.get(key));
                }
            }
            // 设置验证
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
    private String errorMsg; // 异常的简略信息
    private int responseCode;
    //用于将请求的response的header 设置到当前上下文response中
    private List<String> responseHeaderList;


    public HttpRequestUtil sendRequest() {
        HttpURLConnection connection = getConnection();
        DataInputStream input = null;
        if (connection != null) {
            try {
                if (Objects.equals(this.method, "POST")) {
                    try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                        OutputStreamHandlerMap.get(this.contentType).execute(out, this);
                        out.flush();
                    }
                }
                //默认使用原来的Content-Disposition，保留原来文件名
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
                // 处理返回值
                this.responseCode = connection.getResponseCode();
                if (100 <= this.responseCode && this.responseCode <= 399) {
                    input = new DataInputStream(connection.getInputStream());
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
                    input = new DataInputStream(connection.getErrorStream());
                    StringWriter writer = new StringWriter();
                    InputStreamReader reader = new InputStreamReader(input, this.charset);
                    IOUtils.copy(reader, writer);
                    throw new ApiRuntimeException(writer.toString());
                }
            } catch (ApiRuntimeException e) {
                this.error = e.getMessage();
                // 以下针对内部系统之间的接口调用
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
                IOUtils.closeQuietly(input); // 关闭输入流
                IOUtils.closeQuietly(this.outputStream); // 关闭输出流
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
