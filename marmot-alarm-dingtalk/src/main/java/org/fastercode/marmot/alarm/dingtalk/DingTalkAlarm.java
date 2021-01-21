package org.fastercode.marmot.alarm.dingtalk;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.fastercode.marmot.alarm.dingtalk.ding.DingtalkClientUtil;
import org.fastercode.marmot.alarm.dingtalk.ding.message.MarkdownMessage;
import org.fastercode.marmot.alarm.dingtalk.properties.AlarmDingtalkProperties;
import org.fastercode.marmot.alarm.dingtalk.properties.AlarmDingtalkPropertyKey;
import org.fastercode.marmot.core.alarm.AlarmItem;
import org.fastercode.marmot.core.alarm.AlarmType;
import org.fastercode.marmot.core.alarm.hook.AlarmHook;
import org.fastercode.marmot.core.alarm.hook.AlarmReply;
import org.fastercode.marmot.util.DateUtil;

import java.util.Map;

/**
 * @author huyaolong
 */
@Slf4j
public class DingTalkAlarm implements AlarmHook {
    @Override
    public AlarmReply alarm(AlarmItem item) {
        if (log.isDebugEnabled()) {
            log.debug("hit the alarmHook: [{}]", DingTalkAlarm.class.getSimpleName());
        }

        DingTalkAlarmTrigger.enqueue(item);
        return AlarmReply.CONTINUE;
    }

    public static void doAlarm(AlarmItem item) {
        StringBuilder markdownString = new StringBuilder();
        markdownString.append("### ").append(item.getEnv()).append("环境 - ").append(item.getProject());
        if (item.getIps() != null && item.getIps().size() == 1) {
            markdownString.append(" ").append(item.getIps());
        }
        markdownString.append("\n");
        if (item.getIps() != null && item.getIps().size() > 1) {
            markdownString.append("### ").append(item.getIps()).append("\n");
        }
        markdownString.append("---\n");
        markdownString.append("##### 时间: ").append(DateUtil.formatToDateTimeStr(item.getDate())).append("\n");
        markdownString.append("##### 主机: ").append(item.getHostName()).append("\n");
        markdownString.append("##### 线程: ").append(item.getThreadName()).append("\n---\n");

        if (item.getMdcMap() != null) {
            for (Map.Entry<String, String> entry : item.getMdcMap().entrySet()) {
                if (Strings.isNullOrEmpty(entry.getKey()) || Strings.isNullOrEmpty(entry.getValue()))
                    continue;
                markdownString.append("##### ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        markdownString.append("---\n");
        markdownString.append("### ").append(item.getTitle()).append("\n");
        markdownString.append("#### ").append(item.getContent().get(AlarmType.ContentKey.ERROR_THROWABLE)).append("\n");
        markdownString.append("```\n").append(item.getContent().get(AlarmType.ContentKey.ERROR_STACK_TRACE)).append("```\n");

        if (item.getPerMinute() > 0) {
            markdownString.append("#### **").append(item.getMinute()).append("分钟内, 次数 {").append(item.getHit()).append("} ≥ {").append(item.getHitCount()).append("}**\n");
        }

        String title = item.getProject() + "-" + item.getTitle();

        if (log.isDebugEnabled()) {
            log.debug("\n================\n{}\n================", markdownString.toString());
        }

        try {
            String url = AlarmDingtalkProperties.get(AlarmDingtalkPropertyKey.URL);
            if (Strings.isNullOrEmpty(url)) {
                log.info("{} was empty.", AlarmDingtalkPropertyKey.URL.toString());
                return;
            }

            // alarm to dingtalk
            MarkdownMessage message = new MarkdownMessage();
            message.setTitle(title);
            message.add(markdownString.toString());
            DingtalkClientUtil.send(url, message);
        } catch (Exception e) {
            log.warn("DingTalkAlarm has error [{}]", e.getMessage(), e);
        }

    }
}
