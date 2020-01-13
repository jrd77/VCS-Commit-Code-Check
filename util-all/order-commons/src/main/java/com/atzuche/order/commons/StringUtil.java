package com.atzuche.order.commons;

public class StringUtil {


    /**
     * 判断是否是新订单
     * @param orderNo 主订单号
     * @return 是否是新订单 true：是   false：不是
     */
    public static boolean isNewOrderNo(String orderNo){
        if(orderNo==null || orderNo.length()!=GlobalConstant.NEW_ORDER_NO_LEN){
            return false;
        }
        if(orderNo.endsWith(GlobalConstant.NEW_ORDER_NO_SUFFIX)){
            return true;
        }
        return false;
    }
}
