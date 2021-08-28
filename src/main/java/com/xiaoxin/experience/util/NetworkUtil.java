package com.xiaoxin.experience.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiaoxin
 */
public class NetworkUtil {

    private static final Logger log = LoggerFactory.getLogger(NetworkUtil.class);

    private NetworkUtil() {}

    public static Set<String> getLocalMacSet()
    {
        Set<String> macSet = new HashSet<>();
        try
        {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses)
                {
                    InetAddress inetAddress = interfaceAddress.getAddress();
                    NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);
                    byte[] hardwareAddress = network.getHardwareAddress();
                    if (hardwareAddress == null)
                    {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < hardwareAddress.length; i++)
                    {
                        sb.append(String.format("%02X%s", hardwareAddress[i], (i < hardwareAddress.length - 1) ? "-" : ""));
                    }
                    macSet.add(sb.toString());
                }
            }
        }
        catch (Exception e)
        {
            log.error("获取mac地址失败: ",e);
        }
        return macSet;
    }

    public static boolean isLocalMac(String mac)
    {
        mac = mac.toUpperCase();
        return getLocalMacSet().contains(mac);
    }
}
