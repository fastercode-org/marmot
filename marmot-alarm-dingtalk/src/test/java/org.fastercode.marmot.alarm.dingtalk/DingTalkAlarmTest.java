package org.fastercode.marmot.alarm.dingtalk;

import lombok.SneakyThrows;
import org.fastercode.marmot.alarm.dingtalk.ding.DingtalkClientUtil;
import org.fastercode.marmot.alarm.dingtalk.ding.message.TextMessage;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

public class DingTalkAlarmTest {
    @Test
    @Ignore
    @SneakyThrows
    public void test() {
        TextMessage message = new TextMessage("订单测试");
        message.setAtMobiles(Arrays.asList("18515666600"));
        DingtalkClientUtil.send("https://oapi.dingtalk.com/robot/send?access_token=322da78a9750364b72e4b6cf68e545653cb4aa8fc8b36d2ca66fab8b4d555520", message);
    }
}
