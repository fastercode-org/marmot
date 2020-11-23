package org.fastercode.marmot.alarm.dingtalk;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fastercode.marmot.monitor.log.logback.filter.ErrorLogFilter;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.MDC;

import java.util.LinkedHashMap;

@Slf4j
public class UnitTest {
    @Test
    @Ignore
    @SneakyThrows
    public void testTrigger() {
        for (int i = 0; i < 10; i++) {
            MDC.put("URI", "/fastercode/marmot/testTrigger");
            MDC.put("trace-id", System.currentTimeMillis() + "X" + i);
            log.error("test", new RuntimeException("Test"));
        }
        Thread.sleep(10_000L);
    }

    @Test
    public void testBase() {
        Thread.currentThread().setName("pool-10-thread-235-202af060a5fdcd2758493b38d59d2472");
        ErrorLogFilter filter = new ErrorLogFilter();
        filter.setProjectName("demo-api");
        filter.start();

        LoggingEvent event = new LoggingEvent();
        event.setLevel(Level.ERROR);
        event.setMessage("Test-Err-Log-msg. {}");
        event.setMDCPropertyMap(new LinkedHashMap<String, String>() {{
            put("URI", "/test/api");
            put("SourceIP", "10.10.10.10");
            put("OrderNo", "123456789");
            put("TraceID", "202af060a5fdcd2758493b38d59d2472");
        }});
        event.setArgumentArray(new Object[]{"arg1"});
        Throwable throwable = new RuntimeException("test err msg.");
        event.setThrowableProxy(new ThrowableProxy(throwable));
        filter.decide(event);
    }
}
