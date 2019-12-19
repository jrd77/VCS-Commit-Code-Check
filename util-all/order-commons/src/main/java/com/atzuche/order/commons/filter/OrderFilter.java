package com.atzuche.order.commons.filter;

import com.atzuche.order.commons.OrderReqContext;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 7:33 下午
 **/
public interface OrderFilter {
    /**
     * 检查该订单的请求是否合法，如果不合法，将抛出OrderFilterException
     * @param context
     */
    public void validate(OrderReqContext context) throws OrderFilterException;
}
