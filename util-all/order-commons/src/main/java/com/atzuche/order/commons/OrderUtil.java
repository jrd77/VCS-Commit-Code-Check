package com.atzuche.order.commons;

import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/15 11:00 上午
 **/
public class OrderUtil {
    private OrderUtil(){}

    /**
     * 判断订单是否是新订单系统的订单
     * @param orderNo
     * @return
     */
    public static boolean isNewOrder(String orderNo){
        orderNo = StringUtils.trimToNull(orderNo);
        if(orderNo==null){
            throw new IllegalArgumentException("orderNo cannot be null :"+orderNo);
        }
        if(orderNo.length()>10&&orderNo.endsWith("99")){
            return true;
        }
        return false;
    }
}
