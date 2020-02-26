package com.atzuche.order.coreapi.filter;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.exceptions.CarSettingCheckException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service("carSettingCheckFilter")
public class CarSettingCheckFilter implements OrderFilter {
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVo = context.getOrderReqVO();
        LocalDateTime revertTime =  orderReqVo.getRevertTime();
        LocalDateTime rentTime =  orderReqVo.getRentTime();
        String carNo = orderReqVo.getCarNo();
        LocalDateTime currDateTime = LocalDateTime.now();
        /*if (rentTime.isBefore(currDateTime)) {
            log.error("起租时间“rentTime={}”应晚于当前时间“currTime={}”,carNo={}", rentTime,currDateTime,carNo);
            throw new CarSettingCheckException(ErrorCode.RENT_TIME_LESS_CURR_TIME.getCode(),ErrorCode.RENT_TIME_LESS_CURR_TIME.getText());
        }*/
        long currTimeBeforeRentTimeHours = Duration.between(currDateTime,rentTime).toHours();
        if (currTimeBeforeRentTimeHours < 2) {
            log.error("起租时间“rentTime={}”应晚于当前时间2小时“currTime={}”,carNo={}", rentTime, currDateTime, carNo);
            throw new CarSettingCheckException(ErrorCode.RENT_TIME_LESS_CURR_TIME_2HOUR.getCode(),ErrorCode.RENT_TIME_LESS_CURR_TIME_2HOUR.getText());
        }
        long beforeHourNums = Duration.between(rentTime,revertTime).toHours();
        if (beforeHourNums < 1) {
            log.error("还车时间“revertTime={}”应晚于起租时间1小时“rentTime={}”,carNo={}", revertTime, rentTime, carNo);
            throw new CarSettingCheckException(ErrorCode.REVERT_TIME_LESS_RENT_TIME_1HOUR.getCode(),ErrorCode.REVERT_TIME_LESS_RENT_TIME_1HOUR.getText());
        }

    }
}
