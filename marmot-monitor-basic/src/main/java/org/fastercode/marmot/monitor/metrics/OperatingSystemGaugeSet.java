package org.fastercode.marmot.monitor.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huyaolong
 */
public class OperatingSystemGaugeSet implements MetricSet {
    private static boolean unixOperatingSystemMXBeanExists = false;

    private final OperatingSystemMXBean os;

    static {
        try {
            Class.forName("com.sun.management.UnixOperatingSystemMXBean");
            unixOperatingSystemMXBeanExists = true;
        } catch (ClassNotFoundException e) {
            // do nothing
        }
    }

    /**
     * Creates a new gauge using the platform OS bean.
     */
    public OperatingSystemGaugeSet() {
        this(ManagementFactory.getOperatingSystemMXBean());
    }

    /**
     * Creates a new gauge using the given OS bean.
     *
     * @param os an {@link OperatingSystemMXBean}
     */
    public OperatingSystemGaugeSet(OperatingSystemMXBean os) {
        this.os = os;
    }

    @Override
    public Map<String, Metric> getMetrics() {
        Map<String, Metric> gauges = new HashMap<>(16);
        if (unixOperatingSystemMXBeanExists && os instanceof com.sun.management.UnixOperatingSystemMXBean) {
            final com.sun.management.UnixOperatingSystemMXBean unixOs = (com.sun.management.UnixOperatingSystemMXBean) os;
            gauges.put("fd.open", (Gauge<Long>) unixOs::getOpenFileDescriptorCount);
            gauges.put("fd.max", (Gauge<Long>) unixOs::getMaxFileDescriptorCount);
        }

        gauges.put("cpu.num", (Gauge<Long>) () -> (long) os.getAvailableProcessors());
        gauges.put("cpu.load", (Gauge<Double>) os::getSystemLoadAverage);

        gauges.put("arch", (Gauge<String>) os::getArch);
        gauges.put("name", (Gauge<String>) os::getName);
        gauges.put("version", (Gauge<String>) os::getVersion);

        return Collections.unmodifiableMap(gauges);
    }

}
