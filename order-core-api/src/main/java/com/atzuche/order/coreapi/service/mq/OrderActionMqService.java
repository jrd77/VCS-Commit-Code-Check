package com.atzuche.order.coreapi.service.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.CancelSourceEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.service.MqBuildService;
import com.atzuche.order.coreapi.service.RenterCostFacadeService;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.search.dto.OrderInfoDTO;
import com.autoyol.event.rabbit.neworder.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OrderActionMqService {

    private static Logger logger = LoggerFactory.getLogger(OrderActionMqService.class);


    @Autowired
    private BaseProducer baseProducer;

    @Autowired
    private MqBuildService mqBuildService;
    @Autowired
    RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired
    private RenterCostFacadeService facadeService;
    @Autowired
    OrderService orderService;
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
        logger.info("发送下单成功事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.ORDER_CREATE.exchange,
                NewOrderMQActionEventEnum.ORDER_CREATE.routingKey,
                JSON.toJSON(orderMessage));
        //发送套餐SMS
        if ("2".equals(orderCreateMq.getCategory())) {
            Map paramMaps = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.NOTIFY_RENTER_TRANS_REQACCEPTEDPACKAGE.getValue(), null, null);
            orderMessage.setMap(paramMaps);
        }
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_CREATE.exchange,
                NewOrderMQActionEventEnum.ORDER_CREATE.routingKey,
                orderMessage);
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
        logger.info("发送下单失败事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.exchange,
                NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.exchange,
                NewOrderMQActionEventEnum.ORDER_CREATE_FAIL.routingKey,
                orderMessage);
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
        logger.info("发送车主同意订单成功事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.ORDER_MODIFY.exchange,
                NewOrderMQActionEventEnum.ORDER_MODIFY.routingKey,
                JSON.toJSON(orderMessage));
        //车主同意SMS
        Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.NOTIFY_RENTER_TRANS_REQACCEPTED.getValue(), null, null);
        orderMessage.setMap(map);
        logger.info("发送车主同意订单短信:{}", JSONObject.toJSONString(map));
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
        logger.info("发送车主拒绝订单成功事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.exchange,
                NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.exchange,
                NewOrderMQActionEventEnum.OWNER_ORDER_REFUND.routingKey,
                orderMessage);
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
        logger.info("发送订单调度取消事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.ORDER_FAILCANCEL.exchange,
                NewOrderMQActionEventEnum.ORDER_FAILCANCEL.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_FAILCANCEL.exchange,
                NewOrderMQActionEventEnum.ORDER_FAILCANCEL.routingKey,
                orderMessage);
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
        logger.info("发送订单租客取车成功事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.exchange,
                NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.routingKey,
                JSON.toJSON(orderCreateMq));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.exchange,
                NewOrderMQActionEventEnum.RENTER_CONFIRM_GETCAR.routingKey,
                orderMessage);
    }


    /**
     * 发送订单车主确认还车成功事件
     * @param orderNo 订单号
     */
    public void sendOrderOwnerReturnCarSuccess(String orderNo,String renterOrderNo) {
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);
        OrderConfirmReturnCarMq orderCreateMq = new OrderConfirmReturnCarMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderCreateMq);
        orderCreateMq.setType(2);
        OrderMessage orderMessage = OrderMessage.builder().build();
        //租客还车
        Map paramsMap = findRenterDetailCost(orderNo,renterOrderNo,String.valueOf(orderBaseDataMq.getRenterMemNo()));
        Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.CAR_RENTALEND_2_RENTER.getValue(), null, paramsMap);
        orderMessage.setMap(map);
        logger.info("发送订单车主确认还车成功事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.exchange,
                NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.exchange,
                NewOrderMQActionEventEnum.OWNER_CONFIRM_RETURNCAR.routingKey,
                orderMessage);
    }

    /**
     * 获取租客费用数据
     * @param orderNo
     * @param renterOrderNo
     * @param memNo
     * @return
     */
    public Map findRenterDetailCost(String orderNo, String renterOrderNo,String memNo) {
        Map map = Maps.newHashMap();
        map.put("Rent", "0");
        map.put("SrvGetCost", "0");
        map.put("SrvReturnCost", "0");
        map.put("Insurance", "0");
        map.put("AbatementAmt", "0");
        map.put("ExtraDriverInsure", "0");
        map.put("You2OwnerAdjust", "0");
        map.put("Owner2YouAdjust", "0");
        map.put("CouponOffset", "0");
        map.put("WalletPay", "0");
        map.put("RenterPayPlatformContent", "0");
        int totalRentCostAmtWithoutFine = facadeService.getTotalRenterCostWithoutFine(orderNo, renterOrderNo, memNo);
        map.put("TotalAmount", String.valueOf(totalRentCostAmtWithoutFine).contains("-") ? String.valueOf(totalRentCostAmtWithoutFine).replace("-","") : String.valueOf(totalRentCostAmtWithoutFine));
        List<RenterOrderCostDetailEntity> list = renterOrderCostDetailService.listRenterOrderCostDetail(orderNo, renterOrderNo);
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        //车辆租金
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.RENT_AMT.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("Rent", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //取车费用
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SRV_GET_COST.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("SrvGetCost", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //还车费用
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("SrvReturnCost", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //平台保障费
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("Insurance", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //全面保障费
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("AbatementAmt", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //附加驾驶保险金额
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("ExtraDriverInsure", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //租客给车主的调价补贴
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("You2OwnerAdjust", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //车主给租客的调价补贴
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("Owner2YouAdjust", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //优惠券抵扣金额
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("CouponOffset", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //钱包抵扣金额
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.WALLET_DEDUCT.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("WalletPay", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        //车主给租客的租金补贴金额
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            map.put("RenterPayPlatformContent", String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        return map;
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
        logger.info("发送取消订单收取节假日罚金成功事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_SUCCESS.exchange,
                NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_SUCCESS.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_SUCCESS.exchange,
                NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_SUCCESS.routingKey,
                orderMessage);

    }


    /**
     * 发送撤销取消订单收取节假日罚金事件
     *
     * @param orderNo 订单号
     * @param memNo   会员号
     */
    public void sendRevokeOrderCancelMemHolidayDeduct(String orderNo, Integer memNo) {
        if (null == memNo || StringUtils.isBlank(orderNo)) {
            logger.warn("会员注册号为空.");
            return;
        }
        OrderBaseDataMq orderBaseDataMq = mqBuildService.buildOrderBaseDataMq(orderNo);

        OrderHolidayDeductMq orderHolidayDeductMq = new OrderHolidayDeductMq();
        BeanUtils.copyProperties(orderBaseDataMq, orderHolidayDeductMq);
        orderHolidayDeductMq.setMemNo(memNo);
        orderHolidayDeductMq.setOperateName("H5SystemOperator");

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderHolidayDeductMq);
        logger.info("发送撤销取消订单收取节假日罚金事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_CANCEL.exchange,
                NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_CANCEL.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_CANCEL.exchange,
                NewOrderMQActionEventEnum.ORDER_HOLIDAY_DEDUCT_CANCEL.routingKey,
                orderMessage);

    }


    /**
     * 发送通知,老系统处理重叠订单
     *
     * @param orderList 订单列表
     */
    public void sendOrderAgreeConflictNotice(List<OrderInfoDTO> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            logger.warn("No overlapping old orders found.");
            return;
        }

        List<String> orderNos = new ArrayList<>();
        orderList.forEach(order -> {
            orderNos.add(order.getOrderNo());
        });

        OrderAgreeConflictMq orderAgreeConflictMq = new OrderAgreeConflictMq();
        orderAgreeConflictMq.setOrderNos(orderNos);

        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderAgreeConflictMq);

        logger.info("通知老系统处理重叠订单事件.mq:[exchange={},routingKey={}],message=[{}]",
                NewOrderMQActionEventEnum.ORDER_AGREE_CONFLICT_NOTICE_OLD.exchange,
                NewOrderMQActionEventEnum.ORDER_AGREE_CONFLICT_NOTICE_OLD.routingKey,
                JSON.toJSON(orderMessage));
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_AGREE_CONFLICT_NOTICE_OLD.exchange,
                NewOrderMQActionEventEnum.ORDER_AGREE_CONFLICT_NOTICE_OLD.routingKey,
                orderMessage);

    }

}
