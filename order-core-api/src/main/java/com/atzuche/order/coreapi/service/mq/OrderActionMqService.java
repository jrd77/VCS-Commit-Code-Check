package com.atzuche.order.coreapi.service.mq;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.CancelSourceEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.service.MqBuildService;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.autoyol.event.rabbit.neworder.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
     * @param orderNo     订单号
     * @param ownerMemNo  车主会员号
     * @param riskAuditId 风控审核ID
     * @param orderReqVO  下单请求参数
     */
    public void sendCreateOrderSuccess(String orderNo, String ownerMemNo, String riskAuditId, OrderReqVO orderReqVO) {
        OrderCreateMq orderCreateMq = new OrderCreateMq();
        orderCreateMq.setOrderNo(orderNo);
        orderCreateMq.setCategory(orderReqVO.getOrderCategory());
        orderCreateMq.setBusinessChildType(orderReqVO.getBusinessChildType());
        orderCreateMq.setPlatformChildType(orderReqVO.getPlatformChildType());
        orderCreateMq.setBusinessParentType(orderReqVO.getBusinessParentType());
        orderCreateMq.setPlatformParentType(orderReqVO.getPlatformParentType());
        orderCreateMq.setRentTime(LocalDateTimeUtils.localDateTimeToDate(orderReqVO.getRentTime()));
        orderCreateMq.setRevertTime(LocalDateTimeUtils.localDateTimeToDate(orderReqVO.getRevertTime()));
        orderCreateMq.setRenterMemNo(Integer.valueOf(orderReqVO.getMemNo()));
        orderCreateMq.setOwnerMemNo(StringUtils.isNotBlank(ownerMemNo) ? Integer.valueOf(ownerMemNo) : null);
        orderCreateMq.setRiskReqId(riskAuditId);
        orderCreateMq.setCarNo(Integer.valueOf(orderReqVO.getCarNo()));

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送下单成功事件.mq:[exchange={},routingKey={}],message=[{}]", NewOrderMQActionEventEnum.ORDER_CREATE.exchange, NewOrderMQActionEventEnum.ORDER_CREATE.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_CREATE.exchange, NewOrderMQActionEventEnum.ORDER_CREATE.routingKey, orderMessage);
    }


    /**
     * 发送下单失败事件
     *
     * @param orderNo     订单号
     * @param ownerMemNo  车主会员号
     * @param riskAuditId 风控审核ID
     * @param orderReqVO  下单请求参数
     */
    public void sendCreateOrderFail(String orderNo, String ownerMemNo, String riskAuditId, OrderReqVO orderReqVO) {
        OrderCreateFailMq orderCreateMq = new OrderCreateFailMq();
        orderCreateMq.setOrderNo(orderNo);
        orderCreateMq.setCategory(orderReqVO.getOrderCategory());
        orderCreateMq.setBusinessChildType(orderReqVO.getBusinessChildType());
        orderCreateMq.setPlatformChildType(orderReqVO.getPlatformChildType());
        orderCreateMq.setBusinessParentType(orderReqVO.getBusinessParentType());
        orderCreateMq.setPlatformParentType(orderReqVO.getPlatformParentType());
        orderCreateMq.setRentTime(LocalDateTimeUtils.localDateTimeToDate(orderReqVO.getRentTime()));
        orderCreateMq.setRevertTime(LocalDateTimeUtils.localDateTimeToDate(orderReqVO.getRevertTime()));
        orderCreateMq.setRenterMemNo(Integer.valueOf(orderReqVO.getMemNo()));
        orderCreateMq.setOwnerMemNo(StringUtils.isNotBlank(ownerMemNo) ? Integer.valueOf(ownerMemNo) : null);
        orderCreateMq.setRiskReqId(riskAuditId);
        orderCreateMq.setCarNo(Integer.valueOf(orderReqVO.getCarNo()));

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送下单失败事件.mq:[exchange={},routingKey={}],message=[{}]", NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.exchange, NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.exchange, NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.routingKey, orderMessage);
    }

    /**
     * 发送取消订单成功事件
     *
     * @param orderNo          订单号
     * @param cancelSourceEnum 取消来源
     * @param actionEventEnum
     */
    public void sendCancelOrderSuccess(String orderNo, CancelSourceEnum cancelSourceEnum, NewOrderMQActionEventEnum actionEventEnum, Map map) {
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);
        OrderCancelMq orderCreateMq = new OrderCancelMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);
        orderCreateMq.setCancelType(String.valueOf(cancelSourceEnum.getCode()));

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送取消订单成功事件.mq:[exchange={},routingKey={}],message=[{}]", actionEventEnum.exchange, actionEventEnum.routingKey,
                JSON.toJSON(orderMessage));
        orderMessage.setMap(map);
        baseProducer.sendTopicMessage(actionEventEnum.exchange, actionEventEnum.routingKey, orderMessage);
    }


    /**
     * 发送车主同意订单成功事件
     *
     * @param orderNo 订单号
     */
    public void sendOwnerAgreeOrderSuccess(String orderNo) {
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);
        OrderOwnerAgreeMq orderCreateMq = new OrderOwnerAgreeMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送车主同意订单成功事件.mq:[exchange={},routingKey={}],message=[{}]", NewOrderMQActionEventEnum.ORDER_MODIFY.exchange, NewOrderMQActionEventEnum.ORDER_MODIFY.routingKey,
                JSON.toJSON(orderMessage));
        //车主同意SMS
        Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.NOTIFY_RENTER_TRANS_REQACCEPTED.getValue(), null, null);
        orderMessage.setMap(map);
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_MODIFY.exchange, NewOrderMQActionEventEnum.ORDER_MODIFY.routingKey, orderMessage);
    }


    /**
     * 发送车主拒绝订单成功事件
     *
     * @param orderNo 订单号
     */
    public void sendOwnerRefundOrderSuccess(String orderNo) {
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);
        OrderOwnerRefundMq orderCreateMq = new OrderOwnerRefundMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送车主拒绝订单成功事件.mq:[exchange={},routingKey={}],message=[{}]", NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.exchange, NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.exchange, NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.routingKey, orderMessage);
    }

    /**
     * 发送订单调度取消事件
     *
     * @param orderNo 订单号
     */
    public void sendOrderDispatchCancelSuccess(String orderNo) {
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderBaseDataMq);
        logger.info("发送订单调度取消事件.mq:[exchange={},routingKey={}],message=[{}]", NewOrderMQActionEventEnum.ORDER_FAILCANCEL.exchange, NewOrderMQActionEventEnum.ORDER_FAILCANCEL.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_FAILCANCEL.exchange, NewOrderMQActionEventEnum.ORDER_FAILCANCEL.routingKey, orderMessage);
    }


    /**
     * 发送订单租客取车成功事件
     *
     * @param orderNo 订单号
     */
    public void sendOrderRenterPickUpCarSuccess(String orderNo) {
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);
        OrderConfirmGetCarMq orderCreateMq = new OrderConfirmGetCarMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);
        orderCreateMq.setType(1);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderCreateMq);
        logger.info("发送订单租客取车成功事件.mq:[exchange={},routingKey={}],message=[{}]", NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.exchange, NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.routingKey,
                JSON.toJSON(orderCreateMq));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.exchange, NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.routingKey, orderMessage);
    }


    /**
     * 发送订单车主确认还车成功事件
     *
     * @param orderNo 订单号
     */
    public void sendOrderOwnerReturnCarSuccess(String orderNo) {
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);
        OrderConfirmReturnCarMq orderCreateMq = new OrderConfirmReturnCarMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);
        orderCreateMq.setType(2);

        OrderMessage orderMessage = OrderMessage.builder().build();

        //租客还车
        Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.CAR_RENTALEND_2_RENTER.getValue(), null, null);
        orderMessage.setMap(map);
        logger.info("发送订单车主确认还车成功事件.mq:[exchange={},routingKey={}],message=[{}]", NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.exchange, NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.exchange, NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.routingKey, orderMessage);
    }


    /**
     * 发送取消订单收取节假日罚金成功事件
     *
     * @param orderNo     订单号
     * @param memNo       会员号
     * @param holidayId   节假日id
     * @param operateName 操作人名称
     */
    public void sendOrderCancelMemHolidayDeduct(String orderNo, Integer memNo, Integer holidayId, String operateName) {
        if (null == holidayId || null == memNo) {
            logger.warn("租期没有命中节假日.");
            return;
        }
        if (StringUtils.isBlank(operateName)) {
            operateName = "H5SystemOperator";
        }
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);

        OrderHolidayDeductMq orderHolidayDeductMq = new OrderHolidayDeductMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderHolidayDeductMq);
        orderHolidayDeductMq.setHolidayId(holidayId);
        orderHolidayDeductMq.setMemNo(memNo);
        orderHolidayDeductMq.setOperateName(operateName);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderHolidayDeductMq);
        logger.info("发送取消订单收取节假日罚金成功事件.mq:[exchange={},routingKey={}],message=[{}]", NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_SUCCESS.exchange, NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_SUCCESS.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_SUCCESS.exchange, NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_SUCCESS.routingKey, orderMessage);

    }


}
