package com.atzuche.order.admin.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class StringUtil {
    /*
     * @Author ZhangBin
     * @Date 2020/1/6 11:36
     * @Description: 经纬度转化 保留6位小数
     *
     **/
    public static String convertLatOrLon(String latOrLon){
        if(StringUtils.isNotBlank(latOrLon)) {
            String start=latOrLon.substring(0,latOrLon.indexOf(".")+1);
            String end=latOrLon.substring(latOrLon.indexOf(".")+1);
            if(end.length()>6){
                end=end.substring(0,6);
            }
            return start+end;
        }
        return latOrLon;
    }

    public static String getReqIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (!org.springframework.util.StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (!org.springframework.util.StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (!org.springframework.util.StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.error("",e);
        }
        return ip;
    }
}
