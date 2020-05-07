package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.RentTimeException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.autoyol.platformcost.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/*
 * @Author ZhangBin
 * @Date 2020/2/20 17:54
 * @Description: 长租订单租期必须大于30天
 *
 **/
@Slf4j
@Service("longRentTimeFilter")
public class LongMinRentTimeFilter implements OrderFilter {
    public static final int MIN_RENT_DAY = 30;

    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        LocalDateTime rentTime = orderReqVO.getRentTime();
        LocalDateTime revertTime = orderReqVO.getRevertTime();
        //long durationDay = Duration.between(rentTime, revertTime).toDays();
        Double durationDay = CommonUtils.getRentDays(rentTime, revertTime, CommonUtils.CONFIGHOURS);
        if(durationDay < MIN_RENT_DAY){
            throw new RentTimeException("最短租期:"+MIN_RENT_DAY+"天");
        }
    }

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 02, 00, 00);
        LocalDateTime end = LocalDateTime.of(2020, 1, 2, 03, 00, 00);

        Duration between = Duration.between(start, end);
        System.out.println(between.toDays());
    }


}
