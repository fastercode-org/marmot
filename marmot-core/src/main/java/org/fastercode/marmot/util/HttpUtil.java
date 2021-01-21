package org.fastercode.marmot.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    private final CloseableHttpClient httpClient;

    private static class HttpUtilInstance {
        private static final HttpUtil httputil = new HttpUtil();
    }

    public static HttpUtil getInstance() {
        return HttpUtilInstance.httputil;
    }

    private HttpUtil() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(50);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(5);

        httpClient = HttpClientBuilder.create().setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(2000)
                        .setConnectTimeout(1000)
                        .setSocketTimeout(1000)
                        .build()
                )
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setTcpNoDelay(true)
                        .build()
                )
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .build();

        new IdleConnectionMonitorThread(poolingHttpClientConnectionManager).start();
    }

    public String postJson(String url, String json) {
        String res = null;
        CloseableHttpResponse response = null;
        HttpPost request = new HttpPost(url);
        request.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON);
        request.setEntity(new StringEntity(json, CHARSET_UTF8));

        try {
            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity e = response.getEntity();
            res = EntityUtils.toString(e, CHARSET_UTF8);
            EntityUtils.consume(e);
        } catch (Exception e) {
            logger.info(url, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ignored) {
                }
            }
        }
        return res;
    }

    /**
     * 监控有异常的链接
     */
    private static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
            shutdown = false;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // 关闭失效的连接
                        connMgr.closeExpiredConnections();
                        // 可选的, 关闭30秒内不活动的连接
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                logger.info("ex.getMessage()={}", ex.getMessage(), ex);
            }
        }
    }
}
