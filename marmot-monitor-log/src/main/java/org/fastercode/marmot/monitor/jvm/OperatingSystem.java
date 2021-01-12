package org.fastercode.marmot.monitor.jvm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huyaolong
 */
public class OperatingSystem {
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
    public OperatingSystem() {
        this(ManagementFactory.getOperatingSystemMXBean());
    }

    /**
     * Creates a new gauge using the given OS bean.
     *
     * @param os an {@link OperatingSystemMXBean}
     */
    public OperatingSystem(OperatingSystemMXBean os) {
        this.os = os;
    }

    public Map<String, Object> get() {
        Map<String, Object> ret = new HashMap<>(16);
        if (unixOperatingSystemMXBeanExists && os instanceof com.sun.management.UnixOperatingSystemMXBean) {
            final com.sun.management.UnixOperatingSystemMXBean unixOs = (com.sun.management.UnixOperatingSystemMXBean) os;
            ret.put("os.df.open", unixOs.getOpenFileDescriptorCount());
            ret.put("os.df.max", unixOs.getMaxFileDescriptorCount());
        }
        ret.put("os.arch", os.getArch());
        ret.put("os.name", os.getName());
        ret.put("os.version", os.getVersion());
        ret.put("os.cpu.num", os.getAvailableProcessors());
        ret.put("os.cpu.load", os.getSystemLoadAverage());
        return ret;
    }

    public static void main(String[] args) {
        OperatingSystem os = new OperatingSystem();
        System.out.println(JSON.toJSONString(os.get(), SerializerFeature.PrettyFormat));
    }

}
