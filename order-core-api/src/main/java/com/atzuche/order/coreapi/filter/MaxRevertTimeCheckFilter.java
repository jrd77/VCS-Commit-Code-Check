package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.RentTimeException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/*
 * @Author ZhangBin
 * @Date 2020/2/20 17:54
 * @Description: 最长租期校验
 *
 **/
@Slf4j
@Service("maxRevertTimeCheckFilter")
public class MaxRevertTimeCheckFilter implements OrderFilter {
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        LocalDate revertTime = orderReqVO.getRevertTime().toLocalDate();
        LocalDate maxRentDay = revertTime.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());//下下个月的最后一天
        if(revertTime.isAfter(maxRentDay)){
            String maxRentDayStr = maxRentDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            throw new RentTimeException("最长租期截至:"+maxRentDayStr);
        }
    }
}
