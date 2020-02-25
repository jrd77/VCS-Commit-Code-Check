package com.atzuche.order.commons;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/25 5:17 下午
 **/
public class FeeUtil {

    public static Integer minusFee(Integer fee){
        if(fee!=null){
            return -fee;
        }
        return null;
    }
}
