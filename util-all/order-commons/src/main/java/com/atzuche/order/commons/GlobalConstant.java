package com.atzuche.order.commons;

import java.time.format.DateTimeFormatter;

public class GlobalConstant {
    //是否使用特供价、1：使用特供价 0：不使用特供价
    public static final String USE_SPECIAL_PRICE = "1";
    //是否为封面,1：是，0：否
    public static final String IS_CAR_COVER_PIC = "1";
    //会员权益-内部员工-车辆押金
    public static final int MEMBER_RIGHT_STAFF_CAR_DEPOSIT = 300;
    //会员权益-内部员工-违章押金
    public static final int MEMBER_RIGHT_STAFF_WZ_DEPOSIT = 1;

    public static final DateTimeFormatter DATE_FORMAT_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter DATE_FORMAT_2 = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static final DateTimeFormatter DATE_TIME_FORMAT_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter DATE_TIME_FORMAT_2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

}
