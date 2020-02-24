package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.FreeDepositModeException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ Author        :  ZhangBin
 * @ CreateDate    :  2019/10/14 16:34
 * @ Description   :  免押方式校验
 *
 */

@Slf4j
@Service("freeDepositModeFilter")
public class FreeDepositModeFilter implements OrderFilter {
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        String freeDoubleTypeId = orderReqVO.getFreeDoubleTypeId() == null ? null: orderReqVO.getFreeDoubleTypeId();
        int appVersion = orderReqVO.getAppVersion() != null ? Integer.valueOf(orderReqVO.getAppVersion()) : 0;
        String os =  orderReqVO.getOS() != null ? orderReqVO.getOS().toLowerCase() : "";
        if (("Android".equalsIgnoreCase(os) && appVersion >= 100) || ("IOS".equalsIgnoreCase(os) && appVersion >= 95)) {
            if (org.apache.commons.lang.StringUtils.isBlank(freeDoubleTypeId)) {
                throw new FreeDepositModeException();
            }
        }

        // 5.10以后需要校验免押方式,如果是绑卡免押，需要校验是否绑卡成功
        /*if ("1".equals(freeDoubleTypeId)) {
            // 是否绑定信用卡
            Integer countBind = freeDepositModeMapper.getTransCardLianlianCount(memRenter.getMemNo()+"");
            if (countBind == null || countBind < 1) {
                throw new FreeDepositModeException(ErrorCode.FREE_DOUBLE_NOT_BIND_ERROR);
            }
        }*/
    }
}
