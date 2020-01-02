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

    public static final int GET_RETURN_OVER_COST = 50;

    public static final DateTimeFormatter DATE_TIME_FORMAT_2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static final String CAR_YEAR_NEQTWO = "car_year_neqtwo";
    public static final double CAR_YEAR_NEQTWO_DEFAULT_VALUE = 1.4;
    public static final String CAR_YEAR_LTTWO = "car_year_lttwo";
    public static final double CAR_YEAR_LTTWO_DEFAULT_VALUE = 1D;
    public static final double NEW_CAR_COEFFICIENT_DEFAULT_VALUE=1D;

    public static final String SPECIAL_CITY_CODE = "special_city_code";
    public static final String SPECIAL_ILLEGAL_DEPOSIT_AMT_CODE = "special_illegal_deposit_amt";

    public static final String GET_RETURN_FINE_AMT = "get_return_fine_amt";
    public static final String GET_FINE_AMT = "get_fine_amt";
    public static final String RETURN_FINE_AMT = "return_fine_amt";

}
