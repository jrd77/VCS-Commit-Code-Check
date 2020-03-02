package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 4:09 下午
 **/
public class OwnerGoodsNotFoundException extends OrderException {
    private static final String errorCode="700145";
    private static final String errorMsg="获取车主商品异常";



    public OwnerGoodsNotFoundException(Integer carNo,String orderNo){
        super(errorCode,"获取不到车主商品信息car_no={"+carNo+"},orderNo={"+orderNo+"}不存在");
    }
}
