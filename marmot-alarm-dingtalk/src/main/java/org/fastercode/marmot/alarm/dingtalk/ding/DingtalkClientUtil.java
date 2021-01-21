package org.fastercode.marmot.alarm.dingtalk.ding;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.fastercode.marmot.alarm.dingtalk.ding.message.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author huyaolong
 */
public class DingtalkClientUtil {

    public static class HttpInstance {
        private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();

        static {
            CONNECTION_MANAGER.setMaxTotal(50);
            CONNECTION_MANAGER.setDefaultMaxPerRoute(10);
        }

        public static final HttpClient DEFAULT_CLIENT = HttpClients.custom()
                .setConnectionManager(CONNECTION_MANAGER)
                .setConnectionManagerShared(false)
                .evictIdleConnections(10, TimeUnit.SECONDS)
                .build();
    }

    public static class OkHttpInstance {
        public static final OkHttpClient DEFAULT_CLIENT = new OkHttpClient().newBuilder()
                .connectionPool(new ConnectionPool(10, 3, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .hostnameVerifier((hostname, session) -> true)
                .connectTimeout(3L, TimeUnit.SECONDS)
                .writeTimeout(2L, TimeUnit.SECONDS)
                .readTimeout(2L, TimeUnit.SECONDS)
                .build();
    }

    public static SendResult send(final String webhook, final Message message, HttpClient httpClient) throws IOException {
        httpClient = httpClient != null ? httpClient : HttpInstance.DEFAULT_CLIENT;

        HttpPost httppost = new HttpPost(webhook);
        httppost.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        httppost.setEntity(new StringEntity(message.toJsonString(), StandardCharsets.UTF_8));

        HttpResponse response = httpClient.execute(httppost);

        SendResult sendResult = new SendResult();
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject obj = JSONObject.parseObject(result);
            Integer errcode = obj.getInteger("errcode");
            sendResult.setErrorCode(errcode);
            sendResult.setErrorMsg(obj.getString("errmsg"));
            sendResult.setSuccess(errcode.equals(0));
        }

        return sendResult;
    }

    public static SendResult send(final String webhook, final Message message, OkHttpClient okHttpClient) throws IOException {
        okHttpClient = okHttpClient != null ? okHttpClient : OkHttpInstance.DEFAULT_CLIENT;
        Request request = new Request.Builder()
                .url(webhook)
                .post(RequestBody.create(message.toJsonString(), MediaType.parse("application/json;charset=utf-8")))
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .build();
        Response response = okHttpClient.newCall(request).execute();

        SendResult sendResult = new SendResult();
        try {
            if (response != null && response.isSuccessful()) {
                String result = Objects.requireNonNull(response.body()).string();
                JSONObject obj = JSONObject.parseObject(result);
                Integer errcode = obj.getInteger("errcode");
                sendResult.setErrorCode(errcode);
                sendResult.setErrorMsg(obj.getString("errmsg"));
                sendResult.setSuccess(errcode.equals(0));
            }
        } finally {
            try {
                response.close();
            } catch (Exception ignore) {
                // skip
            }
        }

        return sendResult;
    }

    public static SendResult send(final String webhook, final Message message) throws IOException {
        return send(webhook, message, OkHttpInstance.DEFAULT_CLIENT);
    }

}
