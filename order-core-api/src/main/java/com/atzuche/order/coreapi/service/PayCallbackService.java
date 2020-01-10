package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.autoyol.commons.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 订单支付成功 回调子订单
 */
@Service
@Slf4j
public class PayCallbackService implements OrderPayCallBack {

    @Autowired ModifyOrderForRenterService modifyOrderForRenterService;
    @Autowired private RenterOrderService renterOrderService;

    /**
     * ModifyOrderForRenterService.supplementPayPostProcess（修改订单补付回掉）
     */
    @Override
    public void callBack(String orderNo){
        log.info("PayCallbackService callBack start param [{}]",orderNo);
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())){
            throw new OrderSettleFlatAccountException();
        }
        modifyOrderForRenterService.supplementPayPostProcess(orderNo,renterOrder.getRenterOrderNo());
        log.info("PayCallbackService callBack end param [{}]", GsonUtils.toJson(renterOrder));

    }
}
