package com.atzuche.order.admin.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    
    /*
     * @Author ZhangBin
     * @Date 2020/1/6 14:22 
     * @Description: yyyyMMddHHmmss 转 yyyy-MM-dd HH:mm:ss
     * 
     **/
    public static String longStrToTimeStr(String longStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDateTime = LocalDateTime.parse(longStr, dateTimeFormatter);
        DateTimeFormatter newDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String newDateTimeStr = localDateTime.format(newDateTimeFormatter);
        return newDateTimeStr;
    }
}
