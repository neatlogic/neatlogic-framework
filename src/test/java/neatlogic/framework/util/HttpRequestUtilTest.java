package neatlogic.framework.util;

import com.sun.net.httpserver.HttpServer;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpRequestUtilTest {

    @Test
    public void keepsRawHttpErrorBody() throws Exception {
        String body = "{\"Status\":\"ERROR\",\"Message\":\"downstream failed\"}";
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/error", exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(520, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        try {
            HttpRequestUtil request = HttpRequestUtil
                    .get("http://127.0.0.1:" + server.getAddress().getPort() + "/error")
                    .sendRequest();

            Assert.assertEquals(520, request.getResponseCode());
            Assert.assertEquals(body, request.getErrorResponseBody());
        } finally {
            server.stop(0);
        }
    }
}
