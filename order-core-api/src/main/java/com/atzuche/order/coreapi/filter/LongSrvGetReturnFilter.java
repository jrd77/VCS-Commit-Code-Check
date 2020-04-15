package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.RenterTime4HourException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/*
 * @Author ZhangBin
 * @Date 2020/2/20 17:54
 * @Description: 取还车服务起租时间需大于4小时校验
 *
 **/
@Slf4j
@Service
public class LongSrvGetReturnFilter implements OrderFilter {
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        Integer srvReturnFlag = orderReqVO.getSrvReturnFlag()==null?0:orderReqVO.getSrvReturnFlag();
        Integer srvGetFlag = orderReqVO.getSrvGetFlag()==null?0:orderReqVO.getSrvGetFlag();
        if(srvReturnFlag != 1 || srvGetFlag != 1) {

        }
    }

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.of(2019, 12, 31, 23, 00, 00);
        LocalDateTime end = LocalDateTime.of(2020, 1, 1, 23, 00, 00);
        System.out.println(Duration.between(start, end).toHours());
    }
}
