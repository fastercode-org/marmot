package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.GcGaugeSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class GcCollector extends BaseCollector {

    private static final Pattern REPLACE_CHART = Pattern.compile("[^\\w\\d]+");

    private final GcGaugeSet gaugeSet = new GcGaugeSet();

    private final Map<String, Long> spanGcCache = new HashMap<>();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();
        Map<String, Metric> metricsMap = gaugeSet.getMetrics();

        GaugeMetricFamily gc_group_count = new GaugeMetricFamily("MarmotGc_span_count_group", "", labelNames("name"));
        try {
            long ygcCount = (long) ((Gauge) metricsMap.get("span.ygc.count")).getValue();
            long fgcCount = (long) ((Gauge) metricsMap.get("span.fgc.count")).getValue();
            spanGcCache.put("span.ygc.count", ygcCount);
            spanGcCache.put("span.fgc.count", fgcCount);
            gc_group_count.addMetric(labelValues("span.ygc.count"), ygcCount);
            gc_group_count.addMetric(labelValues("span.fgc.count"), fgcCount);
            mfs.add(gc_group_count);
        } catch (Exception ignore) {
            // skip
        }

        GaugeMetricFamily gc_group_time = new GaugeMetricFamily("MarmotGc_span_time_group", "", labelNames("name"));
        try {
            long ygcTime = (long) ((Gauge) metricsMap.get("span.ygc.time")).getValue();
            long fgcTime = (long) ((Gauge) metricsMap.get("span.fgc.time")).getValue();
            spanGcCache.put("span.ygc.time", ygcTime);
            spanGcCache.put("span.fgc.time", fgcTime);
            gc_group_time.addMetric(labelValues("span.ygc.time"), ygcTime);
            gc_group_time.addMetric(labelValues("span.fgc.time"), fgcTime);
            mfs.add(gc_group_time);
        } catch (Exception ignore) {
            // skip
        }

        for (Map.Entry<String, Metric> entry : metricsMap.entrySet()) {
            try {
                Gauge v = (Gauge) entry.getValue();
                if (entry.getKey().startsWith("span.")) {
                    GaugeMetricFamily gc = new GaugeMetricFamily("MarmotGc_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", labelNames("name"));
                    Long spanVal = spanGcCache.get(entry.getKey());
                    gc.addMetric(labelValues(entry.getKey()), (spanVal != null ? spanVal : 0));
                    mfs.add(gc);
                } else {
                    CounterMetricFamily gc = new CounterMetricFamily("MarmotGc_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", labelNames("name"));
                    gc.addMetric(labelValues(entry.getKey()), (long) v.getValue());
                    mfs.add(gc);
                }
            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
