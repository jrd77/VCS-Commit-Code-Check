package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.OrderPayStatusEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.doc.util.StringUtil;
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
    @Autowired private DeliveryCarService deliveryCarService;
    @Autowired private OrderStatusService orderStatusService;

    /**
     * ModifyOrderForRenterService.supplementPayPostProcess（修改订单补付回掉）
     */
    @Override
    public void callBack(String orderNo,String renterOrderNo,Integer isPayAgain){
        log.info("PayCallbackService callBack start param [{}] [{}]  [{}]",orderNo,renterOrderNo,isPayAgain);
        if(YesNoEnum.YES.getCode().equals(isPayAgain) && !StringUtil.isBlank(renterOrderNo)){
            // 修改订单补付成功后回调
            modifyOrderForRenterService.supplementPayPostProcess(orderNo,renterOrderNo);
        }
        OrderStatusEntity entity = orderStatusService.getByOrderNo(orderNo);
        log.info("PayCallbackService sendDataMessageToRenYun param [{}]", GsonUtils.toJson(entity));
        if(Objects.nonNull(entity) && Objects.nonNull(entity.getRentCarPayStatus()) && OrderPayStatusEnum.PAYED.getStatus()==entity.getRentCarPayStatus()){
            deliveryCarService.sendDataMessageToRenYun(renterOrderNo);
        }
        log.info("PayCallbackService callBack end param [{}]", GsonUtils.toJson(renterOrderNo));

    }
}
