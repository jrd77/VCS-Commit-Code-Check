package com.atzuche.order.coreapi.listener.sms;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.mq.util.MQSendPlatformSmsService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author 胡春林
 *  订单的总action事件处理
 */
@Component
@Slf4j
public class OrderActionEventListener {

    @Autowired
    MQSendPlatformSmsService sendPlatformSmsService;
    @Autowired
    RenterMemberService renterMemberService;
    @Autowired
    OwnerMemberService ownerMemberService;
    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    OwnerGoodsService ownerGoodsService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    OwnerOrderService ownerOrderService;

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "order_action_03", durable = "true"),
            exchange = @Exchange(value = "auto-order-action", durable = "true", type = "topic"), key = "action.order.create4.#")
    },containerFactory = "orderRabbitListenerContainerFactory")
    public void process(Message message) {
        log.info("receive order action message: " + new String(message.getBody()));
        OrderMessage orderMessage = JSONObject.parseObject(message.getBody(),OrderMessage.class);
        log.info("新订单动作总事件监听,入参orderMessage:[{}]", orderMessage.toString());
        try {
            Map smsParamsMap = orderMessage.getMap();
            if(CollectionUtils.isEmpty(smsParamsMap))
            {
                log.info("没有短信需要发送--->>>>orderMessage:[{}]",orderMessage.toString());
                return;
            }
            if(!smsParamsMap.containsKey("orderNo"))
            {
                log.info("缺少短信需要发送的订单号参数--->>>>orderMessage:[{}]",orderMessage.toString());
                return;
            }
            String orderNo = String.valueOf(smsParamsMap.get("orderNo"));
            //根据订单号查询相关参数数据
            if(StringUtils.isNotBlank(String.valueOf(smsParamsMap.get("renterFlag"))))
            {
//                RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
//                RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderEntity.getRenterOrderNo(),false);
//                RenterGoodsDetailDTO renterGoodsDetailDTO = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo(),false);
                return;
            }
        } catch (Exception e) {
            log.info("新订单动作总事件监听发生异常,msg：[{}]",e.getMessage());
            Cat.logError("新订单动作总事件监听发生异常",e);
        }
    }
}
