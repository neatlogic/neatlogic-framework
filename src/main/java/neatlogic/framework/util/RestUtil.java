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

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.dto.RestVo;
import neatlogic.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import neatlogic.framework.file.core.FileStorageMediumFactory;
import neatlogic.framework.file.core.IFileStorageHandler;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.integration.authentication.core.AuthenticateHandlerFactory;
import neatlogic.framework.integration.authentication.core.IAuthenticateHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Deprecated
public class RestUtil {
    private static final Map<String, Action<DataOutputStream, RestVo>> actionMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);
    private static final String FORM_DATA_BOUNDARY = "----MyFormBoundarySMFEtUYQG6r5B920";
    public final static String APPLICATION_JSON = "application/json;charset=UTF-8";
    public final static String MULTI_FORM_DATA = "multipart/form-data; boundary=" + FORM_DATA_BOUNDARY;

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

    public static String sendPostRequest(RestVo restVo) {
        String result = "";
        HttpURLConnection connection = null;
        try {
            connection = getConnection(restVo, HttpMethod.POST);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        if (connection != null) {
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                actionMap.get(restVo.getContentType()).execute(out, restVo);
                out.flush();
                out.close();
                // 处理返回值
                InputStreamReader reader = null;
                if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
                    reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
                } else {
                    reader = new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);
                }
                StringWriter writer = new StringWriter();
                IOUtils.copy(reader, writer);
                result = writer.toString();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result = e.getMessage();
            } finally {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * 发送POST请求，并将返回值写入response
     * 用于文件流下载
     *
     * @param restVo 请求入参
     */
    public static String sendPostRequestForStream(RestVo restVo) {
        HttpServletResponse response = UserContext.get().getResponse();
        HttpURLConnection connection = null;
        String result = "";
        try {
            connection = getConnection(restVo, HttpMethod.POST);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        if (connection != null) {
            DataInputStream input = null;
            OutputStream os = null;
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                actionMap.get(restVo.getContentType()).execute(out, restVo);
                out.flush();
                out.close();
                if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
                    input = new DataInputStream(connection.getInputStream());
                } else {
                    input = new DataInputStream(connection.getErrorStream());
                }
                os = response.getOutputStream();
                IOUtils.copy(input, os);
                os.flush();
            } catch (Exception e) {
                result = e.getMessage();
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    result = e.getMessage();
                    logger.error(e.getMessage());
                }
                connection.disconnect();
            }
        }
        return result;
    }


    static {
        actionMap.put(APPLICATION_JSON, (out, restVo) -> {
            out.write(restVo.getPayload().toJSONString().getBytes(StandardCharsets.UTF_8));
        });

        actionMap.put(MULTI_FORM_DATA, (out, restVo) -> {
            StringBuffer resSB = new StringBuffer("\r\n");
            String endBoundary = "\r\n--" + FORM_DATA_BOUNDARY + "--\r\n";
            // strParams 1:key 2:value
            if (restVo.getFormData() != null) {
                Set<String> keySet = restVo.getFormData().keySet();
                for (String key : keySet) {
                    String value = restVo.getFormData().getString(key);
                    resSB.append("Content-Disposition: form-data; name=").append(key).append("\r\n").append("\r\n").append(value).append("\r\n").append("--").append(FORM_DATA_BOUNDARY).append("\r\n");
                }
            }
            String boundaryMessage = resSB.toString();

            out.write(("--" + FORM_DATA_BOUNDARY + boundaryMessage).getBytes(StandardCharsets.UTF_8));

            resSB = new StringBuffer();
            if (restVo.getFileVoList() != null) {
                for (int i = 0, num = restVo.getFileVoList().size(); i < num; i++) {
                    FileVo fileVo = restVo.getFileVoList().get(i);
                    String prefix = fileVo.getPath().split(":")[0];
                    IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix.toUpperCase());
                    if (handler == null) {
                        throw new FileStorageMediumHandlerNotFoundException(prefix);
                    }
                    String fileName = fileVo.getName();
                    resSB.append("Content-Disposition: form-data; name=").append(fileName).append("; filename=").append(fileName).append("\r\n").append("Content-Type:multipart/form-data ").append("\r\n\r\n");

                    out.write(resSB.toString().getBytes(StandardCharsets.UTF_8));
                    // 开始写文件
                    DataInputStream in = new DataInputStream(handler.getData(fileVo.getPath()));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024 * 5];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }

                    if (i < num - 1) {
                        out.write(endBoundary.getBytes(StandardCharsets.UTF_8));
                    }

                    in.close();
                }
            }
            out.write(endBoundary.getBytes(StandardCharsets.UTF_8));
        });
    }


    @FunctionalInterface
    public interface Action<T, K> {
        void execute(T t, K k) throws Exception;
    }


    private static HttpURLConnection getConnection(RestVo restVo, HttpMethod method) throws Exception {
        HttpURLConnection connection;
        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        URL getUrl = new URL(restVo.getUrl());
        connection = (HttpURLConnection) getUrl.openConnection();
        // 设置默认header
        connection.setRequestProperty("Content-Type", restVo.getContentType());
        connection.setRequestProperty("Charset", String.valueOf(StandardCharsets.UTF_8));
        if (StringUtils.isNotBlank(restVo.getTenant())) {
            connection.setRequestProperty("Tenant", restVo.getTenant());
        }
        // 设置http method
        connection.setRequestMethod(method.name());
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 设置验证
        if (StringUtils.isNotBlank(restVo.getAuthType())) {
            IAuthenticateHandler handler = AuthenticateHandlerFactory.getHandler(restVo.getAuthType());
            if (handler != null) {
                handler.authenticate(connection, restVo.getAuthConfig());
            }
        }

        // 设置超时时间
        connection.setConnectTimeout(restVo.getConnectTimeout());
        connection.setReadTimeout(restVo.getReadTimeout());


        connection.connect();
        return connection;
    }
}
