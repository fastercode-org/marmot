package org.fastercode.marmot.alarm.dingtalk.ding;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.fastercode.marmot.alarm.dingtalk.ding.message.Message;
import org.fastercode.marmot.util.HttpUtil;

import java.io.IOException;

public class DingtalkChatbotClient {

    HttpClient httpclient = HttpClients.createDefault();

    public SendResult send2(String webhook, Message message) throws IOException {

        HttpPost httppost = new HttpPost(webhook);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        StringEntity se = new StringEntity(message.toJsonString(), "utf-8");
        httppost.setEntity(se);

        SendResult sendResult = new SendResult();
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject obj = JSONObject.parseObject(result);

            Integer errcode = obj.getInteger("errcode");
            sendResult.setErrorCode(errcode);
            sendResult.setErrorMsg(obj.getString("errmsg"));
            sendResult.setIsSuccess(errcode.equals(0));
        }

        return sendResult;
    }

    public static String send(String webhook, Message message) {
        return HttpUtil.getInstance().postJson(webhook, message.toJsonString());
    }

}
