package com.atzuche.order.coreapi.submitOrder.rules;

import com.atzuche.order.coreapi.entity.dto.OrderContextDto;
import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.coreapi.submitOrder.exception.CancleOrderCountException;
import com.atzuche.order.coreapi.submitOrder.filter.BaseFilter;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.stereotype.Component;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 16:21
 * @Description: 取消订单次数校验规则
 *  每人每天最多可取消订单三次，第三次取消后将禁止下单
 * 
 **/
@Component
public class CancleOrderCountFilter extends BaseFilter {

    @Override
    public boolean validator(SubmitOrderReq submitReqDto, OrderContextDto orderContextDto) throws SubmitOrderException {
        if(true){
            throw new CancleOrderCountException(ErrorCode.FAILED,"由于你当前内取消订单超过三次，禁止下单！");
        }
        return true;
    }
}
