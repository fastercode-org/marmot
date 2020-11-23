package org.fastercode.marmot.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huyaolong
 */
public class DateUtil {

    private DateUtil() {
    }

    private static final ThreadLocal<Map<String, SimpleDateFormat>> SIMPLE_DATE_FMT_WITH_THREAD_LOCAL = ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_TIMESTAMP = "yyyy-MM-dd HH:mm:ss:SSS";

    public static Date now() {
        return new Date();
    }

    /**
     * 日期格式化成  yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String formatToDateTimeStr(Date date) {
        return date == null ? null : getFormat(FORMAT_DATE_TIME).format(date);
    }

    /**
     * 日期格式化成  yyyy-MM-dd HH:mm:ss:SSS
     *
     * @param date
     * @return
     */
    public static String formatToDateStampStr(Date date) {
        return date == null ? null : getFormat(FORMAT_DATE_TIMESTAMP).format(date);
    }

    public static SimpleDateFormat getFormat(String fmtStr) {
        Map<String, SimpleDateFormat> simpleDateFormatMap = SIMPLE_DATE_FMT_WITH_THREAD_LOCAL.get();
        SimpleDateFormat simpleDateFormat = simpleDateFormatMap.get(fmtStr);
        if (simpleDateFormat != null) {
            return simpleDateFormat;
        } else {
            SimpleDateFormat format = new SimpleDateFormat(fmtStr);
            simpleDateFormatMap.put(fmtStr, format);
            return format;
        }
    }
}
