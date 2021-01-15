package org.fastercode.marmot.monitor.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A set of gauges for the runtime
 *
 * @author huyaolong
 */
public class RuntimeGaugeSet implements MetricSet {
    private final RuntimeMXBean runtime;
    private final Set<String> systemProperties = new HashSet<>();

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
            Map<String, String> properties = new HashMap<>(runtime.getSystemProperties());
            // remove too many characters
            properties.remove("java.class.path");
            properties.remove("sun.boot.class.path");
            properties.remove("java.ext.dirs");
            properties.remove("java.library.path");
            properties.remove("java.endorsed.dirs");
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                systemProperties.add(entry.getKey() + "=" + entry.getValue());
            }
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
        gauges.put("start.date", (Gauge<String>) () -> (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(new Date(runtime.getStartTime())));

        gauges.put("properties", (Gauge<Set<String>>) () -> systemProperties);
        gauges.put("arguments", (Gauge<List<String>>) runtime::getInputArguments);
        gauges.put("library.path", (Gauge<String>) runtime::getLibraryPath);

        return Collections.unmodifiableMap(gauges);
    }

}
