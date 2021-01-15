package org.fastercode.marmot.monitor.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A set of gauges for the runtime
 *
 * @author huyaolong
 */
public class RuntimeGaugeSet implements MetricSet {
    private final RuntimeMXBean runtime;
    private Map<String, String> systemProperties;

    /**
     * Creates a new set of gauges.
     */
    public RuntimeGaugeSet() {
        this(ManagementFactory.getRuntimeMXBean());
    }

    /**
     * Creates a new set of gauges with the given {@link RuntimeMXBean}.
     *
     * @param runtime JVM management interface with access to system properties
     */
    public RuntimeGaugeSet(RuntimeMXBean runtime) {
        this.runtime = runtime;
        try {
            systemProperties = new HashMap<>(runtime.getSystemProperties());
            systemProperties.remove("java.class.path");
            systemProperties.remove("sun.boot.class.path");
        } catch (Exception ignore) {
            // sikip
        }
    }

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<>();

        gauges.put("jvm.vendor", (Gauge<String>) () -> String.format(Locale.US,
                "%s %s %s (%s)",
                runtime.getVmVendor(),
                runtime.getVmName(),
                runtime.getVmVersion(),
                runtime.getSpecVersion()));

        gauges.put("uptime", (Gauge<Long>) runtime::getUptime);
        gauges.put("start.timestamp", (Gauge<Long>) runtime::getStartTime);

        gauges.put("properties", (Gauge<String>) systemProperties::toString);
        gauges.put("arguments", (Gauge<String>) () -> runtime.getInputArguments().toString());
        gauges.put("library.path", (Gauge<String>) runtime::getLibraryPath);

        return Collections.unmodifiableMap(gauges);
    }

}
