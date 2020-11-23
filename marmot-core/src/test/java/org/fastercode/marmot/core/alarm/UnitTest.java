package org.fastercode.marmot.core.alarm;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class UnitTest {
    @Test
    public void testBase() {
        AlarmItem item1 = new AlarmItem();
        AlarmItemTest item2 = new AlarmItemTest();

        log.debug("{}", item1.canEqual(item2));
        log.debug("{}", item1.equals(item2));
        log.debug("{}", item1.hashCode());
        log.debug("{}", item1.toString());

        Alarm alarm1 = new Alarm();
        alarm1.alarm(item1);

        Alarm alarm2 = new Alarm();
        alarm2.alarm(item2);
    }

    @Test
    public void testSingle() {
        Alarm alarm1 = Alarm.getInstance();
        Alarm alarm2 = Alarm.getInstance();
        log.debug("{}", alarm1);
        log.debug("{}", alarm2);
        alarm1.alarm(new AlarmItem());
        alarm2.alarm(new AlarmItem());
        Assert.assertEquals(alarm1, alarm2);
    }
}
