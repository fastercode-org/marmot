package org.fastercode.marmot.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IpUtil {

    /**
     * IP地址的正则表达式.
     */
    public static final String IPV4_REGEX = "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})";

    private static volatile String cachedIpAddress;
    private static volatile Collection<String> cachedIpAddressList;

    /**
     * 获取本机IP地址.
     *
     * <p>
     * 有限获取外网IP地址.
     * 也有可能是链接着路由器的最终IP地址.
     * </p>
     *
     * @return 本机IP地址
     */
    public static String getIp() {
        if (null != cachedIpAddress) {
            return cachedIpAddress;
        }
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException ex) {
            throw new RuntimeException(ex);
        }
        String localIpAddress = null;
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
            while (ipAddresses.hasMoreElements()) {
                InetAddress ipAddress = ipAddresses.nextElement();
                if (isPublicIpAddress(ipAddress)) {
                    String publicIpAddress = ipAddress.getHostAddress();
                    cachedIpAddress = publicIpAddress;
                    return publicIpAddress;
                }
                if (isLocalIpAddress(ipAddress)) {
                    localIpAddress = ipAddress.getHostAddress();
                }
            }
        }
        cachedIpAddress = localIpAddress;
        return localIpAddress;
    }

    private static boolean isPublicIpAddress(final InetAddress ipAddress) {
        return !ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isLocalIpAddress(final InetAddress ipAddress) {
        return ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isV6IpAddress(final InetAddress ipAddress) {
        return ipAddress.getHostAddress().contains(":");
    }

    /**
     * 获取本机Host名称.
     *
     * @return 本机Host名称
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException ex) {
            return "unknown";
        }
    }

    private static Collection<InetAddress> getAllHostAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            Collection<InetAddress> addresses = new ArrayList<>();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    addresses.add(inetAddress);
                }
            }

            return addresses;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Collection<String> getAllNoLoopbackAddresses() {
        if (cachedIpAddressList != null) {
            return cachedIpAddressList;
        }

        Collection<String> noLoopbackAddresses = new ArrayList<>();
        Collection<InetAddress> allInetAddresses = getAllHostAddress();
        if (allInetAddresses == null || allInetAddresses.size() == 0) {
            return new ArrayList<>();
        }

        for (InetAddress address : allInetAddresses) {
            if (!address.isLoopbackAddress() && isIPv4LiteralAddress(address.getHostAddress())) {
                noLoopbackAddresses.add(address.getHostAddress());
            }
        }

        cachedIpAddressList = noLoopbackAddresses;
        return noLoopbackAddresses;
    }

    private static boolean isIPv4LiteralAddress(String ipStr) {
        return textToNumericFormatV4(ipStr) != null;
    }

    private static byte[] textToNumericFormatV4(String var0) {
        byte[] var1 = new byte[4];
        long var2 = 0L;
        int var4 = 0;
        boolean var5 = true;
        int var6 = var0.length();
        if (var6 != 0 && var6 <= 15) {
            for (int var7 = 0; var7 < var6; ++var7) {
                char var8 = var0.charAt(var7);
                if (var8 == '.') {
                    if (var5 || var2 < 0L || var2 > 255L || var4 == 3) {
                        return null;
                    }

                    var1[var4++] = (byte) ((int) (var2 & 255L));
                    var2 = 0L;
                    var5 = true;
                } else {
                    int var9 = Character.digit(var8, 10);
                    if (var9 < 0) {
                        return null;
                    }

                    var2 *= 10L;
                    var2 += (long) var9;
                    var5 = false;
                }
            }

            if (!var5 && var2 >= 0L && var2 < 1L << (4 - var4) * 8) {
                switch (var4) {
                    case 0:
                        var1[0] = (byte) ((int) (var2 >> 24 & 255L));
                    case 1:
                        var1[1] = (byte) ((int) (var2 >> 16 & 255L));
                    case 2:
                        var1[2] = (byte) ((int) (var2 >> 8 & 255L));
                    case 3:
                        var1[3] = (byte) ((int) (var2 >> 0 & 255L));
                    default:
                        return var1;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
