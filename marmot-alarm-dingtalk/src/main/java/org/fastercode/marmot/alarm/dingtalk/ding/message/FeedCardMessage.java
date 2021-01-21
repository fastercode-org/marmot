package org.fastercode.marmot.alarm.dingtalk.ding.message;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author harold
 */
public class FeedCardMessage implements Message {

    private List<FeedCardMessageItem> feedItems;

    public List<FeedCardMessageItem> getFeedItems() {
        return feedItems;
    }

    public void setFeedItems(List<FeedCardMessageItem> feedItems) {
        this.feedItems = feedItems;
    }

    @Override
    public String toJsonString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("msgtype", "feedCard");

        Map<String, Object> feedCard = new HashMap<String, Object>();

        if (feedItems == null || feedItems.isEmpty()) {
            throw new IllegalArgumentException("feedItems should not be null or empty");
        }
        for (FeedCardMessageItem item : feedItems) {
            if (Strings.isNullOrEmpty(item.getTitle())) {
                throw new IllegalArgumentException("title should not be blank");
            }
            if (Strings.isNullOrEmpty(item.getMessageURL())) {
                throw new IllegalArgumentException("messageURL should not be blank");
            }
            if (Strings.isNullOrEmpty(item.getPicURL())) {
                throw new IllegalArgumentException("picURL should not be blank");
            }
        }
        feedCard.put("links", feedItems);
        items.put("feedCard", feedCard);

        return JSON.toJSONString(items);
    }
}
