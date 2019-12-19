package com.atzuche.order.coreapi.submitOrder.rules;

import com.atzuche.order.commons.entity.dto.OrderContextDTO;
import com.atzuche.order.coreapi.enums.SubmitOrderErrorEnum;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.coreapi.submitOrder.exception.CancleOrderCountException;
import com.atzuche.order.coreapi.submitOrder.filter.BaseFilter;
import com.atzuche.order.request.NormalOrderReqVO;
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
    public boolean validator(NormalOrderReqVO submitReqDto, OrderContextDTO orderContextDto) throws SubmitOrderException {
        if(true){
            throw new CancleOrderCountException(SubmitOrderErrorEnum.CANCLE_ORDER_COUNT_ERROR.getCode(), SubmitOrderErrorEnum.CANCLE_ORDER_COUNT_ERROR.getText());
        }
        return true;
    }
}
