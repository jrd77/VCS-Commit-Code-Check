package com.atzuche.order.coreapi.filter;


import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.RentRevertTimeCheck1Exception;
import com.atzuche.order.commons.exceptions.RentRevertTimeCheckException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/*
 * @Author ZhangBin
 * @Date 2020/2/20 17:30
 * @Description: 取还车时间校验
 * 
 **/
@Slf4j
@Service("rentRevertTimeCheckFilter")
public class RentRevertTimeCheckFilter  implements OrderFilter {
    private static final SimpleDateFormat DATE_STR = new SimpleDateFormat("yyyyMMdd");
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        LocalDateTime revertTime =  orderReqVO.getRevertTime();
        LocalDateTime rentTime =  orderReqVO.getRentTime();
        if (revertTime.isBefore(rentTime)) {
            log.error("还车时间“revertTime={}”应晚于起租时间“rentTime={}”,carNo={}", revertTime, rentTime, orderReqVO.getCarNo());
            throw new RentRevertTimeCheckException();
        }

        LocalDateTime rendT = judgeLastTime();
        if (rentTime.isAfter(rendT)) {
            log.error("起租时间“rentTime={}”不能晚于下个月最后一天“rendT={}”,carNo={}", rentTime, rendT, orderReqVO.getCarNo());
            throw new RentRevertTimeCheck1Exception();
        }
    }
    /**
     * 获取当前时间的下个月最后一天
     * @version 20151217  改为获取第三个月的最后一天
     * @return
     */
    public LocalDateTime judgeLastTime() {
        // SimpleDateFormat sdf = CommonConstants.DATE_STR;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        /* System.out.println("下个月的第一天: " + sdf.format(c.getTime())); */
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 2);
        c.set(Calendar.DAY_OF_MONTH, 0);
        /* System.out.println("下个月的最后一天: " + sdf.format(c.getTime())); */
        Long rendT = Long.valueOf(DATE_STR_FORMAT(c.getTime())
                + "235959");
        Instant instant = Instant.ofEpochMilli(rendT);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }
    public static String DATE_STR_FORMAT(Date date) {
        synchronized(DATE_STR){
            return DATE_STR.format(date);
        }
    }
}
