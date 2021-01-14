package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.GcGaugeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GcCollector extends Collector {

    private final GcGaugeSet gcGaugeSet = new GcGaugeSet();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();

        GaugeMetricFamily gc = new GaugeMetricFamily("gc", "test gc", Arrays.asList("name", "k2"));

        Map<String, Metric> gauges = gcGaugeSet.getMetrics();

        for (Map.Entry<String, Metric> entry : gauges.entrySet()) {
            try {
                Gauge v = (Gauge) entry.getValue();
                gc.addMetric(Arrays.asList(entry.getKey(), "v2"), (long) v.getValue());
            } catch (Exception ignore) {
            }
        }

        mfs.add(gc);

        return mfs;
    }

}
