package org.fastercode.marmot.alarm.dingtalk.ding;

import com.alibaba.fastjson.JSONObject;
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
import java.util.concurrent.TimeUnit;

/**
 * @author huyaolong
 */
public class DingtalkClientUtil {

    private static class HttpInstance {
        private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();

        static {
            CONNECTION_MANAGER.setMaxTotal(50);
            CONNECTION_MANAGER.setDefaultMaxPerRoute(2);
        }

        private static final HttpClient DEFAULT_CLIENT = HttpClients.custom()
                .setConnectionManager(CONNECTION_MANAGER)
                .setConnectionManagerShared(false)
                .evictIdleConnections(10, TimeUnit.SECONDS)
                .build();
    }

    public static SendResult send(final String webhook, final Message message, HttpClient httpClient) throws IOException {
        httpClient = httpClient != null ? httpClient : HttpInstance.DEFAULT_CLIENT;

        HttpPost httppost = new HttpPost(webhook);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        StringEntity se = new StringEntity(message.toJsonString(), "utf-8");
        httppost.setEntity(se);

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

    public static SendResult send(final String webhook, final Message message) throws IOException {
        return send(webhook, message, null);
    }

}
