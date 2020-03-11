package com.atzuche.order.coreapi.listener;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coreapi.common.RabbitConstants;
import com.atzuche.order.coreapi.service.OwnerRefuseOrderService;
import com.autoyol.event.rabbit.neworder.OrderAgreeConflictMq;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 车主同意老订单通知处理租期重叠的新订单
 *
 * @author pengcheng.fu
 * @date 2020/3/9 16:51
 */
@Service
public class OrderAgreeConflictNoticeListener {

    private static Logger logger = LoggerFactory.getLogger(OrderAgreeConflictNoticeListener.class);

    @Autowired
    private OwnerRefuseOrderService ownerRefuseOrderService;


    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = RabbitConstants.QUEUE_ORDER_AGREE_CONFLICT_NEW, durable = "true"),
            exchange = @Exchange(value = RabbitConstants.EXCHANGE_AUTO_ORDER_ACTION, durable = "true", type = "topic"), key =
            RabbitConstants.ROUTINGKEY_ORDER_AGREE_CONFLICT_NEW)
    })
    public void process(Message message) {
        String msg = new String(message.getBody());
        try {
            logger.info("接收车主同意老订单通知处理租期重叠的新订单信息,msg:[{}]", msg);
            OrderAgreeConflictMq orderAgreeConflict = JSON.parseObject(msg, OrderAgreeConflictMq.class);
            if (null != orderAgreeConflict && !CollectionUtils.isEmpty(orderAgreeConflict.getOrderNos())) {
                for (String orderNo : orderAgreeConflict.getOrderNos()) {
                    try {
                        ownerRefuseOrderService.refuse(orderNo);
                    } catch (Exception e) {
                        logger.error("MQ处理租期重叠订单处理失败,orderNo:[{}]", orderNo);
                        Cat.logError("MQ处理租期重叠订单处理失败,orderNo:" + orderNo, e);
                    }
                }

            }

        } catch (Exception e) {
            logger.error("接收车主同意老订单通知处理租期重叠的新订单信息异常,msg:[{}]", msg);
            Cat.logError("接收车主同意老订单通知处理租期重叠的新订单信息异常," + msg, e);
        }
    }


}
