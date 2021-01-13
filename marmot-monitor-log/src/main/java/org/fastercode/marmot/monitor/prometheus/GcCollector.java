package org.fastercode.marmot.monitor.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GcCollector extends Collector {
    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();

        GaugeMetricFamily gc = new GaugeMetricFamily("gc", "test gc", Arrays.asList("one"));
        mfs.add(gc);

        return mfs;
    }

}
