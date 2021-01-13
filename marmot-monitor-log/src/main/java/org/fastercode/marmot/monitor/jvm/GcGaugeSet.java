package org.fastercode.marmot.monitor.jvm;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.collect.Sets;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.regex.Pattern;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * A set of gauges for the counts and elapsed times of garbage collections.
 */
public class GcGaugeSet implements MetricSet {
    private static final Set<String> FULL_GC_NAME = Sets.newHashSet("ConcurrentMarkSweep", "MarkSweepCompact", "PS MarkSweep", "G1 Old Generation",
            "Garbage collection optimized for short pausetimes Old Collector", "Garbage collection optimized for throughput Old Collector",
            "Garbage collection optimized for deterministic pausetimes Old Collector");

    private static final Set<String> YOUNG_GC_NAME = Sets.newHashSet("ParNew", "Copy", "PS Scavenge", "G1 Young Generation", "Garbage collection optimized for short pausetimes Young Collector",
            "Garbage collection optimized for throughput Young Collector", "Garbage collection optimized for deterministic pausetimes Young Collector");

    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    private long lastYoungGcCollectionCount = -1;
    private long lastYoungGcCollectionTime = -1;
    private long lastFullGcCollectionCount = -1;
    private long lastFullGcCollectionTime = -1;

    private GarbageCollectorMXBean fullGc;
    private GarbageCollectorMXBean yongGc;

    private final List<GarbageCollectorMXBean> garbageCollectors;

    /**
     * Creates a new set of gauges for all discoverable garbage collectors.
     */
    public GcGaugeSet() {
        this(ManagementFactory.getGarbageCollectorMXBeans());
    }

    /**
     * Creates a new set of gauges for the given collection of garbage collectors.
     *
     * @param garbageCollectors the garbage collectors
     */
    public GcGaugeSet(Collection<GarbageCollectorMXBean> garbageCollectors) {
        this.garbageCollectors = new ArrayList<>(garbageCollectors);
        for (final GarbageCollectorMXBean gc : this.garbageCollectors) {
            String name = gc.getName();
            if (YOUNG_GC_NAME.contains(name)) {
                yongGc = gc;
            } else if (FULL_GC_NAME.contains(name)) {
                fullGc = gc;
            }
        }
    }

    public long yongGcCollectionCount() {
        if (yongGc == null) {
            return 0;
        }
        return yongGc.getCollectionCount();
    }

    public long yongGcCollectionTime() {
        if (yongGc == null) {
            return 0;
        }
        return yongGc.getCollectionTime();
    }

    public long fullGcCollectionCount() {
        if (fullGc == null) {
            return 0;
        }
        return fullGc.getCollectionCount();
    }

    public long fullGcCollectionTime() {
        if (fullGc == null) {
            return 0;
        }
        return fullGc.getCollectionTime();
    }

    public long spanYongGcCollectionCount() {

        long current = yongGcCollectionCount();
        if (lastYoungGcCollectionCount == -1) {
            lastYoungGcCollectionCount = current;
            return 0;
        } else {
            long result = current - lastYoungGcCollectionCount;
            lastYoungGcCollectionCount = current;
            return result;
        }
    }

    public long spanYongGcCollectionTime() {
        long current = yongGcCollectionTime();
        if (lastYoungGcCollectionTime == -1) {
            lastYoungGcCollectionTime = current;
            return 0;
        } else {
            long result = current - lastYoungGcCollectionTime;
            lastYoungGcCollectionTime = current;
            return result;
        }
    }

    public long spanFullGcCollectionCount() {
        long current = fullGcCollectionCount();
        if (lastFullGcCollectionCount == -1) {
            lastFullGcCollectionCount = current;
            return 0;
        } else {
            long result = current - lastFullGcCollectionCount;
            lastFullGcCollectionCount = current;
            return result;
        }
    }

    public long spanFullGcCollectionTime() {
        long current = fullGcCollectionTime();
        if (lastFullGcCollectionTime == -1) {
            lastFullGcCollectionTime = current;
            return 0;
        } else {
            long result = current - lastFullGcCollectionTime;
            lastFullGcCollectionTime = current;
            return result;
        }
    }


    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<>();

        // real name gc
        for (final GarbageCollectorMXBean gc : garbageCollectors) {
            final String name = WHITESPACE.matcher(gc.getName()).replaceAll("-");
            gauges.put(name(name, "count"), (Gauge<Long>) gc::getCollectionCount);
            gauges.put(name(name, "time"), (Gauge<Long>) gc::getCollectionTime);
        }

        // ygc & fgc
        gauges.put("ygc.count", (Gauge<Long>) this::yongGcCollectionCount);
        gauges.put("ygc.time", (Gauge<Long>) this::yongGcCollectionTime);
        gauges.put("fgc.count", (Gauge<Long>) this::fullGcCollectionCount);
        gauges.put("fgc.time", (Gauge<Long>) this::fullGcCollectionTime);

        // span: ygc & fgc
        gauges.put("ygc.span.count", (Gauge<Long>) this::spanYongGcCollectionCount);
        gauges.put("ygc.span.time", (Gauge<Long>) this::spanYongGcCollectionTime);
        gauges.put("fgc.span.count", (Gauge<Long>) this::spanFullGcCollectionCount);
        gauges.put("fgc.span.time", (Gauge<Long>) this::spanFullGcCollectionTime);

        return Collections.unmodifiableMap(gauges);
    }

}
