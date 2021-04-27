package codedriver.framework.util;

import codedriver.framework.dto.RestVo;
import codedriver.framework.integration.authtication.core.AuthenticateHandlerFactory;
import codedriver.framework.integration.authtication.core.IAuthenticateHandler;
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
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class RestUtil {

    protected static Logger logger = LoggerFactory.getLogger(RestUtil.class);
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

    public static String sendRequest(RestVo restVo) {
        HttpURLConnection connection = null;
        String result = "";
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URL getUrl = new URL(restVo.getUrl());
            connection = (HttpURLConnection) getUrl.openConnection();
            // 设置http method
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // 设置验证
            if (StringUtils.isNotBlank(restVo.getAuthType())) {
                IAuthenticateHandler handler = AuthenticateHandlerFactory.getHandler(restVo.getAuthType());
                if (handler != null) {
                    handler.authenticate(connection, restVo.getAuthConfig());
                }
            }

            // 设置超时时间
            connection.setConnectTimeout(0);
            connection.setReadTimeout(restVo.getTimeout());

            // 设置默认header
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            connection.connect();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        if (connection != null) {
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream());) {
                if (restVo.getPayload() != null) {
                    out.write(restVo.getPayload().toJSONString().getBytes("utf-8"));
                    out.flush();
                }
                out.close();
                // 处理返回值
                InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "utf-8");
                StringWriter writer = new StringWriter();
                IOUtils.copy(reader, writer);
                result = writer.toString();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result = e.getMessage();
            }
        }
        return result;
    }
}
