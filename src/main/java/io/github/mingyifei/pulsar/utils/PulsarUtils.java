package io.github.mingyifei.pulsar.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/10/26 4:58 下午
 **/
@Slf4j
public class PulsarUtils {

    public static final String EL_START = "${";
    public static final String EL_END = "}";

    private PulsarUtils() {
    }

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();
                if (nif.isPointToPoint() || nif.isLoopback()) {
                    continue;
                }
                if (nif.getName().startsWith("docker")) {
                    continue;
                }
                Enumeration<InetAddress> addresses = nif.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
            return "127.0.0.1";
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }
}
