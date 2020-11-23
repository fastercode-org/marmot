package org.fastercode.marmot.core.properties;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

@Slf4j
public class UnitTest {
    @Test
    public void testBase() {
        CoreProperties coreProperties = new CoreProperties(new Properties());
        int v = coreProperties.getValue(CorePropertyKey.ALARM_CAPACITY_MIN);
        log.info("{}", v);
        Assert.assertEquals(v, 666);
    }
}
