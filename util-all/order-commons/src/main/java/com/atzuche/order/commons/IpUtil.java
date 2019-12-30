package com.atzuche.order.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author haibao.yan
 * 获取本地服务器Ip
 */
public class IpUtil {

    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);
    private IpUtil(){}

    public  static String getLocalIp(){
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("获取本地ip出错：",e);
        }
        return ip;
    }

}
