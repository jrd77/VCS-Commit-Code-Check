package com.atzuche.order.coreapi.service.mq;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.enums.CancelSourceEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.service.MqBuildService;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.event.rabbit.neworder.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusMqService {

    private static Logger logger = LoggerFactory.getLogger(OrderStatusMqService.class);

    @Autowired
    private BaseProducer baseProducer;

    @Autowired
    private MqBuildService mqBuildService;


    /**
     * 发送下单成功订单状态通知事件
     *
     * @param orderNo 订单号
     * @param ownerMemNo 车主会员号
     * @param status 订单状态
     * @param orderReqVO 下单请求参数
     * @param newOrderMQStatusEventEnum
     */
    public void sendOrderStatusToCreate(String orderNo,String ownerMemNo, String status, OrderReqVO orderReqVO, NewOrderMQStatusEventEnum newOrderMQStatusEventEnum){
        OrderStatusMq orderCreateMq = new OrderStatusMq();
        orderCreateMq.setOrderNo(orderNo);
        orderCreateMq.setCategory(orderReqVO.getOrderCategory());
        orderCreateMq.setBusinessChildType(orderReqVO.getBusinessChildType());
        orderCreateMq.setPlatformChildType(orderReqVO.getPlatformChildType());
        orderCreateMq.setBusinessParentType(orderReqVO.getBusinessParentType());
        orderCreateMq.setPlatformParentType(orderReqVO.getPlatformParentType());
        orderCreateMq.setRentTime(DateUtil.asDate(orderReqVO.getRentTime().toLocalDate()));
        orderCreateMq.setRevertTime(DateUtil.asDate(orderReqVO.getRevertTime().toLocalDate()));
        orderCreateMq.setRenterMemNo(Integer.valueOf(orderReqVO.getMemNo()));
        orderCreateMq.setOwnerMemNo(StringUtils.isNotBlank(ownerMemNo) ? Integer.valueOf(ownerMemNo) : null);
        orderCreateMq.setCarNo(Integer.valueOf(orderReqVO.getCarNo()));

        orderCreateMq.setStatus(Integer.valueOf(status));
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送下单成功订单状态通知事件.mq:[exchange={},routingKey={}],message=[{}]", newOrderMQStatusEventEnum.exchange, newOrderMQStatusEventEnum.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(newOrderMQStatusEventEnum.exchange,newOrderMQStatusEventEnum.routingKey,orderMessage);
    }


    /**
     * 发送订单状态变更成功事件
     *
     * @param orderNo 订单号
     * @param status 订单状态
     * @param newOrderMQStatusEventEnum
     */
    public void sendOrderStatusByOrderNo(String orderNo, Integer status,NewOrderMQStatusEventEnum newOrderMQStatusEventEnum){
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);
        OrderStatusMq orderStatusMq = new OrderStatusMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderStatusMq);

        orderStatusMq.setStatus(status);
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderStatusMq);
        logger.info("发送订单状态变更成功事件.mq:[exchange={},routingKey={}],message=[{}]",newOrderMQStatusEventEnum.exchange, newOrderMQStatusEventEnum.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(newOrderMQStatusEventEnum.exchange,newOrderMQStatusEventEnum.routingKey,orderMessage);
    }


}
