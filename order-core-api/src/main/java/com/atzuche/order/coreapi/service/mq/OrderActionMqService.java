package com.atzuche.order.coreapi.service.mq;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.enums.CancelSourceEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.service.MqBuildService;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.event.rabbit.neworder.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderActionMqService {

    private static Logger logger = LoggerFactory.getLogger(OrderActionMqService.class);


    @Autowired
    private BaseProducer baseProducer;

    @Autowired
    private MqBuildService mqBuildService;


    /**
     * 发送下单成功事件
     *
     * @param orderNo 订单号
     * @param riskAuditId 风控审核ID
     * @param orderReqVO 下单请求参数
     */
    public void sendCreateOrderSuccess(String orderNo, String riskAuditId, OrderReqVO orderReqVO){
        OrderCreateMq orderCreateMq = new OrderCreateMq();
        orderCreateMq.setOrderNo(orderNo);
        orderCreateMq.setCategory(orderReqVO.getOrderCategory());
        orderCreateMq.setBusinessChildType(orderReqVO.getBusinessChildType());
        orderCreateMq.setPlatformChildType(orderReqVO.getPlatformChildType());
        orderCreateMq.setBusinessParentType(orderReqVO.getBusinessParentType());
        orderCreateMq.setPlatformParentType(orderReqVO.getPlatformParentType());
        orderCreateMq.setRentTime(DateUtil.asDate(orderReqVO.getRentTime().toLocalDate()));
        orderCreateMq.setRevertTime(DateUtil.asDate(orderReqVO.getRevertTime().toLocalDate()));
        orderCreateMq.setMemNo(Integer.valueOf(orderReqVO.getMemNo()));
        orderCreateMq.setRiskReqId(riskAuditId);
        orderCreateMq.setCarNo(Integer.valueOf(orderReqVO.getCarNo()));

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送下单成功事件.mq:[exchange={},routingKey={}],message=[{}]",NewOrderMQActionEventEnum.ORDER_CREATE.exchange, NewOrderMQActionEventEnum.ORDER_CREATE.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_CREATE.exchange,NewOrderMQActionEventEnum.ORDER_CREATE.routingKey,orderMessage);
    }


    /**
     * 发送下单失败事件
     *
     * @param orderNo 订单号
     * @param riskAuditId 风控审核ID
     * @param orderReqVO 下单请求参数
     */
    public void sendCreateOrderFail(String orderNo, String riskAuditId, OrderReqVO orderReqVO){
        OrderCreateFailMq orderCreateMq = new OrderCreateFailMq();
        orderCreateMq.setOrderNo(orderNo);
        orderCreateMq.setCategory(orderReqVO.getOrderCategory());
        orderCreateMq.setBusinessChildType(orderReqVO.getBusinessChildType());
        orderCreateMq.setPlatformChildType(orderReqVO.getPlatformChildType());
        orderCreateMq.setBusinessParentType(orderReqVO.getBusinessParentType());
        orderCreateMq.setPlatformParentType(orderReqVO.getPlatformParentType());
        orderCreateMq.setRentTime(DateUtil.asDate(orderReqVO.getRentTime().toLocalDate()));
        orderCreateMq.setRevertTime(DateUtil.asDate(orderReqVO.getRevertTime().toLocalDate()));
        orderCreateMq.setMemNo(Integer.valueOf(orderReqVO.getMemNo()));
        orderCreateMq.setRiskReqId(riskAuditId);
        orderCreateMq.setCarNo(Integer.valueOf(orderReqVO.getCarNo()));

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送下单失败事件.mq:[exchange={},routingKey={}],message=[{}]",NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.exchange, NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.exchange,NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.routingKey,orderMessage);
    }

    /**
     * 发送取消订单成功事件
     *
     * @param orderNo 订单号
     * @param memNo 租客或车主会员号
     * @param cancelSourceEnum 取消来源
     * @param actionEventEnum
     */
    public void sendCancelOrderSuccess(String orderNo, String memNo, CancelSourceEnum cancelSourceEnum, NewOrderMQActionEventEnum actionEventEnum){
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo,memNo);
        OrderCancelMq orderCreateMq = new OrderCancelMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);
        orderCreateMq.setCancelType(String.valueOf(cancelSourceEnum.getCode()));

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送取消订单成功事件.mq:[exchange={},routingKey={}],message=[{}]",actionEventEnum.exchange, actionEventEnum.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(actionEventEnum.exchange,actionEventEnum.routingKey,orderMessage);
    }


    /**
     * 发送车主同意订单成功事件
     *
     * @param orderNo 订单号
     * @param memNo 车主会员号
     */
    public void sendOwnerAgreeOrderSuccess(String orderNo, String memNo){
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo,memNo);
        OrderOwnerAgreeMq orderCreateMq = new OrderOwnerAgreeMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送车主同意订单成功事件.mq:[exchange={},routingKey={}],message=[{}]",NewOrderMQActionEventEnum.ORDER_MODIFY.exchange, NewOrderMQActionEventEnum.ORDER_MODIFY.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_MODIFY.exchange,NewOrderMQActionEventEnum.ORDER_MODIFY.routingKey,orderMessage);
    }



    /**
     * 发送车主拒绝订单成功事件
     *
     * @param orderNo 订单号
     * @param memNo 车主会员号
     */
    public void sendOwnerRefundOrderSuccess(String orderNo, String memNo){
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo,memNo);
        OrderOwnerRefundMq orderCreateMq = new OrderOwnerRefundMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送车主拒绝订单成功事件.mq:[exchange={},routingKey={}],message=[{}]",NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.exchange, NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.exchange,NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.routingKey,orderMessage);
    }

    /**
     * 发送订单调度取消事件
     *
     * @param orderNo 订单号
     * @param memNo 车主会员号
     */
    public void sendOrderDispatchCancelSuccess(String orderNo, String memNo){
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo,memNo);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderBaseDataMq);
        logger.info("发送订单调度取消事件.mq:[exchange={},routingKey={}],message=[{}]",NewOrderMQActionEventEnum.ORDER_FAILCANCEL.exchange, NewOrderMQActionEventEnum.ORDER_FAILCANCEL.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_FAILCANCEL.exchange,NewOrderMQActionEventEnum.ORDER_FAILCANCEL.routingKey,orderMessage);
    }


    /**
     * 发送订单租客取车成功事件
     *
     * @param orderNo 订单号
     * @param memNo 车主会员号O
     */
    public void sendOrderRenterPickUpCarSuccess(String orderNo, String memNo){
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo,memNo);
        OrderConfirmGetCarMq orderCreateMq = new OrderConfirmGetCarMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);
        orderCreateMq.setType(1);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送订单租客取车成功事件.mq:[exchange={},routingKey={}],message=[{}]",NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.exchange, NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.exchange,NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.routingKey,orderMessage);
    }


    /**
     * 发送订单车主确认还车成功事件
     *
     * @param orderNo 订单号
     * @param memNo 车主会员号
     */
    public void sendOrderOwnerReturnCarSuccess(String orderNo, String memNo){
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo,memNo);
        OrderConfirmReturnCarMq orderCreateMq = new OrderConfirmReturnCarMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);
        orderCreateMq.setType(2);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送订单车主确认还车成功事件.mq:[exchange={},routingKey={}],message=[{}]",NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.exchange, NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.exchange,NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.routingKey,orderMessage);
    }
}
