package com.atzuche.order.sms.listener;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.open.service.FeignSMSOwnerOrderService;
import com.atzuche.order.open.service.FeignSMSRenterOrderService;
import com.atzuche.order.sms.common.base.OrderSendMessageManager;
import com.atzuche.order.sms.enums.ShortMessageTypeEnum;
import com.atzuche.order.sms.utils.SmsParamsMapUtil;
import com.autoyol.commons.utils.StringUtils;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.dianping.cat.Cat;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @author 胡春林
 * 订单的总action事件处理
 */
@Component
@Slf4j
public class OrderActionEventListener extends OrderSendMessageManager {

    @Autowired
    FeignSMSOwnerOrderService smsOwnerOrderService;
    @Autowired
    FeignSMSRenterOrderService smsRenterOrderService;

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "order_action_11", durable = "true"),
            exchange = @Exchange(value = "auto-order-action", durable = "true", type = "topic"), key = "action.#")
    }, containerFactory = "orderRabbitListenerContainerFactory")
    public void process(Message message) {
        log.info("receive order action message: " + new String(message.getBody()));
        try {
            String routeKeyName = message.getMessageProperties().getReceivedRoutingKey();
            OrderMessage orderMessage = createOrderMessageService(message);
            if (NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS.routingKey.equals(routeKeyName)) {
                orderMessage = handlerOrderPayRentCostSuccessMq(orderMessage);
            }
            if (Objects.nonNull(orderMessage)) {
                sendSMSMessageData(orderMessage);
                sendPushMessageData(orderMessage);
            }
        } catch (Exception e) {
            log.info("新订单动作总事件监听发生异常,msg：[{}]", e);
            Cat.logError("新订单动作总事件监听发生异常", e);
        }
    }

    /**
     * 处理支付租车费用事件（凹凸自营 await）
     */
    public OrderMessage handlerOrderPayRentCostSuccessMq(OrderMessage orderMessage) {
        log.info("----开始处理支付租车费用事件-----");

        JSONObject jsonObject = (JSONObject)orderMessage.getMessage();
        if(Objects.isNull(jsonObject) || !jsonObject.containsKey("type"))
        {
            return orderMessage;
        }
        if (1 == Integer.valueOf(jsonObject.get("type").toString())) {
            String renterRealName = getRenterRealName(jsonObject.get("orderNo").toString());
            Map paramsMap = Maps.newHashMap();
            if (StringUtils.isNotEmpty(renterRealName)) {
                paramsMap.put("renterRealName", renterRealName);
            }
            if (!CarOwnerTypeEnum.isAuToByCode(getCarOwnerType(jsonObject.get("orderNo").toString()))) {
                Map smsMap = SmsParamsMapUtil.getParamsMap(jsonObject.get("orderNo").toString(), null, ShortMessageTypeEnum.PAY_RENT_CAR_DEPOSIT_2_OWNER.getValue(), paramsMap);
                orderMessage.setMap(smsMap);
                orderMessage.setPushMap(null);
            }
        }
        log.info("----处理支付租车费用事件完成 发送参数：[{}]", JSONObject.toJSONString(orderMessage));
        return orderMessage;
    }

    /**
     * 获取车主车辆类型
     * @param orderNo
     * @return
     */
    public Integer getCarOwnerType(String orderNo) {
        OwnerOrderDTO ownerOrderDTO = smsOwnerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo).getData();
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = smsOwnerOrderService.getOwnerGoodsDetail(ownerOrderDTO.getOwnerOrderNo()).getData();
        return ownerGoodsDetailDTO.getCarOwnerType();
    }

    /**
     * 获取租客姓名
     * @return
     */
    public String getRenterRealName(String orderNo){
        RenterOrderDTO renterOrderDTO = smsRenterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo).getData();
        RenterMemberDTO renterMemberDTO = smsRenterOrderService.selectrenterMemberByRenterOrderNo(renterOrderDTO.getRenterOrderNo()).getData();
        return renterMemberDTO.getRealName();
    }
}