package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.CommercialInsuranceAuditFailException;
import com.atzuche.order.commons.exceptions.NotSupportLongRentException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CommercialInsuranceAuditFilter implements OrderFilter {
    public static List<Integer> orderTypeList = Arrays.asList(1,2);
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        Integer orderType = context.getOwnerGoodsDetailDto().getOrderType();
        Integer longRentVerifyStatus = context.getOwnerGoodsDetailDto().getLongRentVerifyStatus();
        if(!(orderType != null && new ArrayList<>(orderTypeList).contains(orderType) && longRentVerifyStatus !=null && longRentVerifyStatus == 1)){
            CommercialInsuranceAuditFailException e = new CommercialInsuranceAuditFailException();
            log.error("商业险审核状态不通过orderType={}",orderType,e);
            throw e;
        }
    }
}
