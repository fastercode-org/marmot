package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.GcGaugeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class GcCollector extends Collector {

    private static final Pattern REPLACE_CHART = Pattern.compile("[^\\w\\d]+");

    private final GcGaugeSet gaugeSet = new GcGaugeSet();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();

        GaugeMetricFamily gc_group_count = new GaugeMetricFamily("MarmotGc_span_count_group", "", Arrays.asList("name"));
        try {
            gc_group_count.addMetric(Arrays.asList("span.ygc.count"), (long) ((Gauge) gaugeSet.getMetrics().get("span.ygc.count")).getValue());
            gc_group_count.addMetric(Arrays.asList("span.fgc.count"), (long) ((Gauge) gaugeSet.getMetrics().get("span.fgc.count")).getValue());
            mfs.add(gc_group_count);
        } catch (Exception ignore) {
            // skip
        }

        GaugeMetricFamily gc_group_time = new GaugeMetricFamily("MarmotGc_span_time_group", "", Arrays.asList("name"));
        try {
            gc_group_time.addMetric(Arrays.asList("span.ygc.time"), (long) ((Gauge) gaugeSet.getMetrics().get("span.ygc.time")).getValue());
            gc_group_time.addMetric(Arrays.asList("span.fgc.time"), (long) ((Gauge) gaugeSet.getMetrics().get("span.fgc.time")).getValue());
            mfs.add(gc_group_time);
        } catch (Exception ignore) {
            // skip
        }

        for (Map.Entry<String, Metric> entry : gaugeSet.getMetrics().entrySet()) {
            try {
                Gauge v = (Gauge) entry.getValue();
                CounterMetricFamily gc = new CounterMetricFamily("MarmotGc_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", Arrays.asList("name", "value"));
                gc.addMetric(Arrays.asList(entry.getKey(), String.valueOf(v.getValue())), (long) v.getValue());
                mfs.add(gc);
            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
