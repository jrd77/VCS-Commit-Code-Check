package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import org.springframework.stereotype.Service;

@Service
public class OrderCategoryFilter  implements OrderFilter {
    //public static orderType
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        Integer orderType = context.getOwnerGoodsDetailDto().getOrderType();

        if(orderType != null){

        }
    }
}
