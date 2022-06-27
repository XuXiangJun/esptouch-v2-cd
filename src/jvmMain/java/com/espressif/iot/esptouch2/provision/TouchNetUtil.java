package com.espressif.iot.esptouch2.provision;

import java.net.*;
import java.util.Enumeration;

public class TouchNetUtil {

    public static TouchNetInfo getNetInfo() {
        TouchNetInfo info = new TouchNetInfo();
        try {
            info.localAddress = InetAddress.getLocalHost();
            NetworkInterface ni = __getLocalNetwork(info.localAddress);
            info.hardBssid = __getConnectionBSSID(ni);
            info.broadcastAddress = __getBroadcastAddress(ni);
        } catch (UnknownHostException | SocketException ignore) {
        }

        return info;
    }

    private static NetworkInterface __getLocalNetwork(InetAddress localAddress) throws SocketException {
        Enumeration<NetworkInterface> enums = NetworkInterface.getNetworkInterfaces();
        while (enums.hasMoreElements()) {
            NetworkInterface ni = enums.nextElement();
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress address = addrs.nextElement();
                if (address.equals(localAddress)) {
                    return ni;
                }
            }
        }

        return null;
    }

    private static NetworkInterface getLocalNetwork() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            return __getLocalNetwork(localAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InetAddress __getBroadcastAddress(NetworkInterface ni) {
        if (ni != null) {
            for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                InetAddress broadcast = ia.getBroadcast();
                if (broadcast instanceof Inet4Address) {
                    return broadcast;
                }
            }
        }

        try {
            return InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // Impossible arrive here
        return null;
    }

    public static InetAddress getBroadcastAddress() {
        NetworkInterface ni = getLocalNetwork();
        return __getBroadcastAddress(ni);
    }

    private static String __getConnectionBSSID(NetworkInterface ni) {
        if (ni != null) {
            try {
                byte[] address = ni.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                for (byte b : address) {
                    if ((b & 0xff) < 16) {
                        sb.append("0");
                    }
                    sb.append(Integer.toHexString(b & 0xff));
                    sb.append(":");
                }
                sb.deleteCharAt(sb.length() - 1);
                return sb.toString();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getConnectionBSSID() {
        NetworkInterface ni = getLocalNetwork();
        return __getConnectionBSSID(ni);
    }

    /**
     * @param bssid the bssid like aa:bb:cc:dd:ee:ff
     * @return byte array converted from bssid
     */
    public static byte[] convertBssid2Bytes(String bssid) {
        String[] bssidSplits = bssid.split(":");
        if (bssidSplits.length != 6) {
            throw new IllegalArgumentException("Invalid bssid format");
        }
        byte[] result = new byte[bssidSplits.length];
        for (int i = 0; i < bssidSplits.length; i++) {
            result[i] = (byte) Integer.parseInt(bssidSplits[i], 16);
        }
        return result;
    }

    public static DatagramSocket createUdpSocket() {
        for (int port = 23233; port < 0xffff; ++port) {
            try {
                return new DatagramSocket(port);
            } catch (SocketException ignored) {
            }
        }

        return null;
    }

}
