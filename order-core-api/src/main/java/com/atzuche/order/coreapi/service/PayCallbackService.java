package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
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
    @Autowired private DeliveryCarService deliveryCarService;

    /**
     * ModifyOrderForRenterService.supplementPayPostProcess（修改订单补付回掉）
     */
    @Override
    public void callBack(String orderNo,Integer isPayAgain){
        log.info("PayCallbackService callBack start param [{}] [{}]",orderNo,isPayAgain);
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())){
            throw new OrderSettleFlatAccountException();
        }
        log.info("PayCallbackService supplementPayPostProcess param [{}] [{}]",orderNo, renterOrder.getRenterOrderNo());
        if(YesNoEnum.YES.getCode().equals(isPayAgain)){
            // 修改订单补付成功后回调
            modifyOrderForRenterService.supplementPayPostProcess(orderNo,renterOrder.getRenterOrderNo());
        }

        log.info("PayCallbackService sendDataMessageToRenYun param [{}]", renterOrder.getRenterOrderNo());
        deliveryCarService.sendDataMessageToRenYun(renterOrder.getRenterOrderNo());

        log.info("PayCallbackService callBack end param [{}]", GsonUtils.toJson(renterOrder));

    }
}
