package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.google.common.collect.Sets;
import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.GcGaugeSet;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class GcCollector extends Collector {

    private static final Pattern REPLACE_CHART = Pattern.compile("[^\\w\\d]+");
    private static final HashSet<String> SKIP_KEYS = Sets.newHashSet("ygc.span.count", "fgc.span.count", "ygc.span.time", "fgc.span.time");

    private final GcGaugeSet gcGaugeSet = new GcGaugeSet();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();

        GaugeMetricFamily gc_group_count = new GaugeMetricFamily("MarmotGc_group_span_count", "test gc", Arrays.asList("name"));
        try {
            gc_group_count.addMetric(Arrays.asList("ygc.span.count"), (long) ((Gauge) gcGaugeSet.getMetrics().get("ygc.span.count")).getValue());
            gc_group_count.addMetric(Arrays.asList("fgc.span.count"), (long) ((Gauge) gcGaugeSet.getMetrics().get("fgc.span.count")).getValue());
            mfs.add(gc_group_count);
        } catch (Exception ignore) {
            // skip
        }

        GaugeMetricFamily gc_group_time = new GaugeMetricFamily("MarmotGc_group_span_time", "test gc", Arrays.asList("name"));
        try {
            gc_group_time.addMetric(Arrays.asList("ygc.span.time"), (long) ((Gauge) gcGaugeSet.getMetrics().get("ygc.span.time")).getValue());
            gc_group_time.addMetric(Arrays.asList("fgc.span.time"), (long) ((Gauge) gcGaugeSet.getMetrics().get("fgc.span.time")).getValue());
            mfs.add(gc_group_time);
        } catch (Exception ignore) {
            // skip
        }

        for (Map.Entry<String, Metric> entry : gcGaugeSet.getMetrics().entrySet()) {
            try {
                if (SKIP_KEYS.contains(entry.getKey())) {
                    continue;
                }
                Gauge v = (Gauge) entry.getValue();
                CounterMetricFamily gc = new CounterMetricFamily("MarmotGc_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", Arrays.asList("name"));
                gc.addMetric(Arrays.asList(entry.getKey()), (long) v.getValue());
                mfs.add(gc);
            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
