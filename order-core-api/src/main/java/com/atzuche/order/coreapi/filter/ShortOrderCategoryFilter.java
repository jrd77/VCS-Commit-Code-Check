package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.NotSupportLongRentException;
import com.atzuche.order.commons.exceptions.NotSupportShortRentException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ShortOrderCategoryFilter implements OrderFilter {
    public static List<Integer> orderTypeList = Arrays.asList(1,3,4);
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        Integer orderType = context.getOwnerGoodsDetailDto().getOrderType();
        if(orderType == null || !new ArrayList<>(orderTypeList).contains(orderType)){
            NotSupportShortRentException e = new NotSupportShortRentException();
            log.error("不支持短租下单orderType={}",orderType,e);
            throw e;
        }
    }
}
