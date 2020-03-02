package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 4:09 下午
 **/
public class OwnerGoodsByOwnerOrderNoNotFoundException extends OrderException {
    private static final String errorCode="700147";
    private static final String errorMsg="获取车主商品异常";



    public OwnerGoodsByOwnerOrderNoNotFoundException(String ownerOrderNo){
        super(errorCode,"获取不到车主商品信息ownerOrderNo={"+ownerOrderNo+"}不存在");
    }
}
