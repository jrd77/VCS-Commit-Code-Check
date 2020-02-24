package com.atzuche.order.coreapi.filter;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.RenterTime4HourException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.autoyol.cardate.TimeSeparater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

/*
 * @Author ZhangBin
 * @Date 2020/2/20 17:54
 * @Description: 取还车服务起租时间需大于4小时校验
 *
 **/
@Slf4j
@Service
public class RenterTime4HourFilter implements OrderFilter {
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        Integer srvReturnFlag = orderReqVO.getSrvReturnFlag()==null?0:orderReqVO.getSrvReturnFlag();
        Integer srvGetFlag = orderReqVO.getSrvGetFlag()==null?0:orderReqVO.getSrvGetFlag();
        LocalDateTime revertTime =  orderReqVO.getRevertTime();
        LocalDateTime rentTime =  orderReqVO.getRentTime();
        String businessChildType = orderReqVO.getBusinessChildType();
        if ("1".equals(businessChildType) && (srvReturnFlag == 1 || srvGetFlag == 1)) {
            long hours = Duration.between(rentTime, revertTime).toHours();
            if (hours < 4) {
                throw new RenterTime4HourException();
            }
        }
    }
}
