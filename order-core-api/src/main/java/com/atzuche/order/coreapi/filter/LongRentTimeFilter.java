package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.LongRentGetReturnCarServiceException;
import com.atzuche.order.commons.exceptions.LongRentTimeException;
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
 * @Description: 长租订单租期必须大于30天 最多3个月
 *
 **/
@Slf4j
@Service("longRentTimeFilter")
public class LongRentTimeFilter implements OrderFilter {
    public static final int MIN_RENT_DAY = 30;

    public static final int MAX_RENT_DAY = 90;

    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        LocalDateTime rentTime = orderReqVO.getRentTime();
        LocalDateTime revertTime = orderReqVO.getRevertTime();
        long durationDay = Duration.between(rentTime, revertTime).toDays();
        if(durationDay < MIN_RENT_DAY || durationDay > MAX_RENT_DAY){
            throw new LongRentTimeException(MIN_RENT_DAY,MAX_RENT_DAY);
        }
    }

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.of(2019, 12, 31, 22, 00, 00);
        LocalDateTime end = LocalDateTime.of(2020, 1, 1, 21, 00, 00);
        System.out.println(Duration.between(start, end).toDays());
    }
}
