package com.atzuche.order.sms.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.sms.common.OrderMessageServiceScanner;
import com.atzuche.order.sms.common.base.OrderSendMessageManager;
import com.atzuche.order.sms.enums.MessageServiceTypeEnum;
import com.atzuche.order.sms.enums.PushMessageTypeEnum;
import com.atzuche.order.sms.enums.ShortMessageTypeEnum;
import com.atzuche.order.sms.interfaces.IOrderRouteKeyMessage;
import com.atzuche.order.sms.utils.SmsParamsMapUtil;
import com.autoyol.commons.utils.StringUtils;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderRenterPaySuccessMq;
import com.dianping.cat.Cat;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
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
    OrderMessageServiceScanner orderMessageServiceScanner;

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "order_action_11", durable = "true"),
            exchange = @Exchange(value = "auto-order-action", durable = "true", type = "topic"), key = "action.#")
    }, containerFactory = "orderRabbitListenerContainerFactory")
    public void process(Message message) {
        log.info("receive order action message: " + new String(message.getBody()));
        try {
            OrderMessage orderMessage = createOrderMessageService(message);
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
     * 获取对应得service
     * @param message
     * @return
     */
    public OrderMessage createOrderMessageService(Message message){
        OrderMessage orderMessage = JSONObject.parseObject(message.getBody(), OrderMessage.class);
        log.info("新订单动作总事件监听,入参orderMessage:[{}]", orderMessage.toString());
        String routeKeyName = message.getMessageProperties().getReceivedRoutingKey();
        String serviceName = MessageServiceTypeEnum.getSmsServiceTemplate(routeKeyName);
        if(StringUtils.isBlank(serviceName))
        {
            log.info("该事件没有需要发送得短信,routeKeyName:[{}]",routeKeyName);
            return orderMessage;
        }
        serviceName = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
        IOrderRouteKeyMessage orderRouteKeyMessage = orderMessageServiceScanner.getBean(serviceName);
        if(Objects.isNull(orderRouteKeyMessage))
        {
            log.info("该事件没有对应的短信服务,routeKeyName:[{}]",routeKeyName);
            return orderMessage;
        }
         orderMessage = orderRouteKeyMessage.sendOrderMessageWithNo(message);
        return orderMessage;
    }

    /**
     * 处理支付租车费用事件（凹凸自营）
     * @param message
     */
    public OrderMessage handlerOrderPayRentCostSuccessMq(Message message) {
        OrderMessage orderMessage = JSONObject.parseObject(message.getBody(), OrderMessage.class);
        log.info("新订单动作总事件监听,入参orderMessage:[{}]", orderMessage.toString());
        String routeKeyName = message.getMessageProperties().getReceivedRoutingKey();
        if (!NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS.routingKey.equals(routeKeyName)) {
            return orderMessage;
        }
        OrderMessage<OrderRenterPaySuccessMq> orderRenterPayMessage = JSON.parseObject(new String(message.getBody()), new TypeReference<OrderMessage<OrderRenterPaySuccessMq>>(){});
        log.info("----开始处理支付租车费用事件-----");
        if (1 == orderRenterPayMessage.getMessage().getType().intValue()) {
            String renterRealName = getRenterRealName(orderRenterPayMessage.getMessage().getOrderNo());
            Map paramsMap = Maps.newHashMap();
            if (StringUtils.isNotEmpty(renterRealName)) {
                paramsMap.put("renterRealName", renterRealName);
            }
            if (CarOwnerTypeEnum.isAuToByCode(getCarOwnerType(orderRenterPayMessage.getMessage().getOrderNo()))) {
                Map smsMap = SmsParamsMapUtil.getParamsMap(orderRenterPayMessage.getMessage().getOrderNo(), ShortMessageTypeEnum.SELF_SUPPORT_RENT_DEPOSIT_PAID_NOTICE.getValue(), ShortMessageTypeEnum.PAY_RENT_CAR_DEPOSIT_2_OWNER.getValue(), paramsMap);
                orderMessage.setMap(smsMap);
                Map map = SmsParamsMapUtil.getParamsMap(orderRenterPayMessage.getMessage().getOrderNo(), PushMessageTypeEnum.RENTER_PAY_CAR_SUCCESS.getValue(), PushMessageTypeEnum.RENTER_PAY_CAR_2_OWNER.getValue(), null);
                orderMessage.setPushMap(map);
            } else {
                Map smsMap = SmsParamsMapUtil.getParamsMap(orderRenterPayMessage.getMessage().getOrderNo(), null, ShortMessageTypeEnum.PAY_RENT_CAR_DEPOSIT_2_OWNER.getValue(), paramsMap);
                orderMessage.setMap(smsMap);
            }
        }
        log.info("----处理支付租车费用事件完成 发送参数：[{}]", JSONObject.toJSONString(orderRenterPayMessage));
        return orderMessage;
    }

    /**
     * 获取车主车辆类型
     * @param orderNo
     * @return
     */
    public Integer getCarOwnerType(String orderNo) {
//        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
//        OwnerGoodsDetailDTO ownerGoodsDetailDTO = ownerGoodsService.getOwnerGoodsDetail(ownerOrderEntity.getOwnerOrderNo(), false);
//        return ownerGoodsDetailDTO.getCarOwnerType();
        return null;
    }

    /**
     * 获取租客姓名
     * @return
     */
    public String getRenterRealName(String orderNo){
//        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
//        RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderEntity.getRenterOrderNo(), false);
//        return renterMemberDTO.getRealName();
        return null;
    }
}