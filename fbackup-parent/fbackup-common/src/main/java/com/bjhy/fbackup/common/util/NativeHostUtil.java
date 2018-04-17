package com.bjhy.fbackup.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 本地主机工具类
 * @author wubo
 *
 */
public class NativeHostUtil {
	
	/**
	 * 知道获取本地Ip
	 * @throws wubo
	 */
	public static String getHostAddress() {  
        Enumeration<NetworkInterface> netInterfaces = null;  
        try {  
            netInterfaces = NetworkInterface.getNetworkInterfaces();  
            while (netInterfaces.hasMoreElements()) {  
                NetworkInterface ni = netInterfaces.nextElement();  
                Enumeration<InetAddress> ips = ni.getInetAddresses();  
                while (ips.hasMoreElements()) {  
                    InetAddress ip = ips.nextElement();  
                    if (ip.isSiteLocalAddress()) {  
                        return ip.getHostAddress();  
                    }  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();
        }  
        
        String hostAddress=null;
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LoggerUtils.error("未知主机!!",e);
		}
        
        return hostAddress;  
    }
	
	/**
	 * 将serverIp变成一个用"/"分隔的路径
	 * @param serverIp
	 * @return
	 */
	public static String getPathOfServerIp(String serverIp){
		if(serverIp == null){
			LoggerUtils.error("serverIp 不能为null");
			return "";
		}
		String replace = serverIp.replace(".", "/");
		return replace;
	}

}
